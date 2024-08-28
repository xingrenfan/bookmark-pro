package org.bookmark.pro.service.tree.handler;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.awt.RelativePoint;
import org.bookmark.pro.base.BookmarkTipPanel;
import org.bookmark.pro.constants.BookmarkConstants;
import org.bookmark.pro.constants.BookmarkIcons;
import org.bookmark.pro.domain.model.AbstractTreeNodeModel;
import org.bookmark.pro.domain.model.BookmarkNodeModel;
import org.bookmark.pro.domain.model.GroupNodeModel;
import org.bookmark.pro.service.base.document.DocumentService;
import org.bookmark.pro.service.base.settings.GlobalSettings;
import org.bookmark.pro.service.tree.TreeService;
import org.bookmark.pro.service.tree.component.BookmarkDragHandler;
import org.bookmark.pro.service.tree.component.BookmarkGroupNavigator;
import org.bookmark.pro.service.tree.component.BookmarkMenus;
import org.bookmark.pro.service.tree.component.BookmarkTree;
import org.bookmark.pro.service.tree.component.BookmarkTreeNode;
import org.bookmark.pro.utils.BookmarkNoticeUtil;
import org.bookmark.pro.utils.BookmarkUtil;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Objects;
import java.util.UUID;

/**
 * 书签树管理器
 *
 * @author Lyon
 * @date 2024/04/17
 */
public final class TreeServiceImpl implements TreeService {
    private BookmarkGroupNavigator groupNavigator;

    private Project openProject;

    public TreeServiceImpl(Project openProject) {
        this.openProject = openProject;
        // 初始化分组导航
        this.groupNavigator = new BookmarkGroupNavigator(openProject, BookmarkTree.getInstance(openProject));
        // 添加Root节点
        addProjectNode(openProject, BookmarkTree.getInstance(openProject), this.groupNavigator);
        // 添加搜索提示
        bookmarkTip(openProject, BookmarkTree.getInstance(openProject));
        // 添加拖拽处理方案
        addDragHandler(BookmarkTree.getInstance(openProject));
        // 书签图标渲染
        bookmarkRendering(openProject, BookmarkTree.getInstance(openProject));
        // 增加选中和点击事件
        addTreeListeners(openProject, BookmarkTree.getInstance(openProject));
        // 书签书菜单初始化
        BookmarkMenus.getInstance(this.openProject).addTreeMenus(BookmarkTree.getInstance(openProject));
    }

    @Override
    public BookmarkTree getBookmarkTree() {
        return BookmarkTree.getInstance(this.openProject);
    }

    @Override
    public void addBookmarkNode(BookmarkTreeNode node) {
        // 获取父节点
        BookmarkTreeNode parent = this.groupNavigator.ensureActivatedGroup();
        addBookmarkNode(parent, node);
    }

    @Override
    public void addBookmarkNode(BookmarkTreeNode parentNode, BookmarkTreeNode node) {
        this.groupNavigator.setActivatedBookmark(node);
        // 父节点添加书签节点
        BookmarkTree.getInstance(openProject).addNode(parentNode, node);
    }

    @Override
    public void changeBookmarkNode(BookmarkTreeNode parentNode, BookmarkTreeNode node) {
        if (node.isBookmark() && node.isGroup()) {
            node.setAllowsChildren(true);
        }
        if (parentNode == null) {
            // 不指定父节点直接更新
            BookmarkTree.getInstance(this.openProject).getModel().nodeChanged(node);
        } else {
            // 先移除
            removeBookmarkNode(node);
            // 在添加到新节点
            addBookmarkNode(parentNode, node);
        }
        // 更新书签缓存
        DocumentService.getInstance(this.openProject).addBookmarkNode(node);
    }

    @Override
    public void changeBookmarkNode(BookmarkTreeNode node) {
        BookmarkTreeNode parentTreeNode = (BookmarkTreeNode) node.getParent();
        // 删除原书签
        removeBookmarkNode(node);
        //重新插入新书签
        addBookmarkNode(parentTreeNode, node);
    }


    @Override
    public void setSelectNode(BookmarkTreeNode selectedNode) {
        if (selectedNode.isGroup()) {
            this.groupNavigator.activeGroup(selectedNode);
        } else {
            this.groupNavigator.activeBookmark(selectedNode);
        }
    }

    @Override
    public void removeBookmarkNode(BookmarkTreeNode node) {
        BookmarkTree.getInstance(this.openProject).removeNode(node);
        DocumentService.getInstance(this.openProject).removeBookmarkNode(node);
    }

    @Override
    public void nextBookmark() {
        this.groupNavigator.next();
    }

    @Override
    public void preBookmark() {
        this.groupNavigator.pre();
    }

    /**
     * 添加项目Root节点
     *
     * @param project        项目
     * @param bookmarkTree   书签树
     * @param groupNavigator 组导航器
     */
    private void addProjectNode(Project project, BookmarkTree bookmarkTree, BookmarkGroupNavigator groupNavigator) {
        // 获取项目名称 创建Root节点文件夹
        BookmarkTreeNode root = new BookmarkTreeNode(new GroupNodeModel(project.getName(), UUID.randomUUID().toString()), true);
        bookmarkTree.setDefaultModel(new DefaultTreeModel(root), groupNavigator);
        bookmarkTree.getSelectionModel().setSelectionMode(TreeSelectionModel.CONTIGUOUS_TREE_SELECTION);
        this.groupNavigator.setActivatedGroup(root);
    }

    /**
     * 添加拖动处理程序
     *
     * @param bookmarkTree 书签树
     */
    private void addDragHandler(BookmarkTree bookmarkTree) {
        bookmarkTree.setDragEnabled(true);
        bookmarkTree.setDropMode(DropMode.ON_OR_INSERT);
        bookmarkTree.setTransferHandler(new BookmarkDragHandler());
    }

    /**
     * 书签图标等信息渲染
     *
     * @param project
     * @param bookmarkTree 书签树
     */
    private void bookmarkRendering(Project project, BookmarkTree bookmarkTree) {
        bookmarkTree.setCellRenderer(new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean isLeaf, int row, boolean hasFocus) {
                super.getTreeCellRendererComponent(tree, value, sel, expanded, isLeaf, row, hasFocus);
                // 设置书签树图标
                BookmarkTreeNode node = (BookmarkTreeNode) value;
                if (0 == row) {
                    // 系统图标 icon = AllIcons.Nodes.Module;
                    setIcon(BookmarkIcons.FOLDER_ICON);
                } else if (row > 0) {
                    // 系统文件图标 AllIcons.Nodes.Folder;
                    if (node.isBookmark() && node.isGroup()) {
                        BookmarkNodeModel nodeModel = (BookmarkNodeModel) node.getUserObject();
                        if (nodeModel.getInvalid()) {
                            // 无效书签 显示失效图标
                            setIcon(BookmarkIcons.BOOKMARK_INVALID_ICON);
                        } else if (nodeModel.getVirtualFile() != null && BookmarkUtil.virtualFileExist(project, nodeModel.getVirtualFile())) {
                            // 虚拟文件有效 显示书签分组图标
                            setIcon(BookmarkIcons.BOOKMARK_GROUP_ICON);
                        } else {
                            // 无效书签 显示文件丢失图标 不更新书签状态 否则会造成文件丢失的状态错误的显示成失效
                            // node.setInvalid(true);
                            // nodeModel.setInvalid(true);
                            setIcon(BookmarkIcons.FILE_LOSE_ICON);
                        }
                    } else if (node.isBookmark()) {
                        BookmarkNodeModel nodeModel = (BookmarkNodeModel) node.getUserObject();
                        if (nodeModel.getInvalid()) {
                            // 无效书签 显示失效图标
                            setIcon(BookmarkIcons.BOOKMARK_INVALID_ICON);
                        } else if (nodeModel.getVirtualFile() != null && BookmarkUtil.virtualFileExist(project, nodeModel.getVirtualFile())) {
                            // 虚拟文件有效 显示书签图标
                            setIcon(BookmarkIcons.BOOKMARK_ICON);
                        } else {
                            // 无效书签 显示文件丢失图标 不更新书签状态 否则会造成文件丢失的状态错误的显示成失效
                            // node.setInvalid(true);
                            // nodeModel.setInvalid(true);
                            setIcon(BookmarkIcons.FILE_LOSE_ICON);
                        }
                    } else if (node.isGroup()) {
                        // 书签分组图标
                        setIcon(BookmarkIcons.GROUP_ICON);
                    }
                }
                return this;
            }
        });
    }

    private void bookmarkTip(Project project, BookmarkTree bookmarkTree) {
        if (BookmarkConstants.TIPS_FOR_TOOL.equals(GlobalSettings.getInstance().getTipType())) {
            bookmarkTree.addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    // Get the selected node
                    TreePath path = bookmarkTree.getPathForLocation(e.getX(), e.getY());
                    if (path != null) {
                        // Show tooltip for the node
                        showToolTip(getToolTipText(e), e);
                    } else {
                        if (this.lastPopup != null) {
                            lastPopup.cancel();
                        }
                    }
                }

                private AbstractTreeNodeModel getToolTipText(MouseEvent e) {
                    TreePath path = bookmarkTree.getPathForLocation(e.getX(), e.getY());
                    if (path != null) {
                        BookmarkTreeNode selectedNode = (BookmarkTreeNode) path.getLastPathComponent();
                        if (selectedNode != null) {
                            return (AbstractTreeNodeModel) selectedNode.getUserObject();
                        }
                    }
                    return null;
                }

                private JBPopup lastPopup;
                private AbstractTreeNodeModel lastAbstractTreeNodeModel;

                private void showToolTip(AbstractTreeNodeModel nodeModel, MouseEvent e) {
                    if (nodeModel == null) {
                        return;
                    }
                    if (lastAbstractTreeNodeModel == nodeModel) {
                        return;
                    }
                    if (this.lastPopup != null) {
                        lastPopup.cancel();
                    }
                    if (nodeModel.isBookmark()) {
                        lastAbstractTreeNodeModel = nodeModel;

                        JBPopupFactory popupFactory = JBPopupFactory.getInstance();
                        lastPopup = popupFactory.createComponentPopupBuilder(new BookmarkTipPanel(lastAbstractTreeNodeModel), null).setFocusable(true).setResizable(true).setRequestFocus(true).createPopup();

                        Point adjustedLocation = new Point(e.getLocationOnScreen().x + 5, e.getLocationOnScreen().y + 10); // Adjust position
                        lastPopup.show(RelativePoint.fromScreen(adjustedLocation));
                    }
                }
            });
        } else {
            // 书签选中监听
            bookmarkTree.addTreeSelectionListener(event -> {
                int selectionCount = bookmarkTree.getSelectionCount();
                BookmarkTreeNode selectedNode = (BookmarkTreeNode) bookmarkTree.getLastSelectedPathComponent();
                if (selectionCount != 1 || null == selectedNode) {
                    return;
                }

                if (selectedNode.isGroup()) {
                    this.groupNavigator.activeGroup(selectedNode);
                } else {
                    this.groupNavigator.activeBookmark(selectedNode);
                }
            });
        }
    }

    /**
     * 增加书签树选中和鼠标点击事件
     *
     * @param project
     * @param bookmarkTree 书签树
     */
    private void addTreeListeners(Project project, BookmarkTree bookmarkTree) {
        // 鼠标点击事件
        bookmarkTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // 双击事件
                if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {
                    TreePath path = bookmarkTree.getSelectionPath();
                    if (Objects.isNull(path)) {
                        return;
                    }
                    BookmarkTreeNode selectedNode = (BookmarkTreeNode) path.getLastPathComponent();
                    if (selectedNode != null && selectedNode.isBookmark()) {
                        // 是书签则直接跳转
                        BookmarkNodeModel bookmark = (BookmarkNodeModel) selectedNode.getUserObject();
                        if (bookmark.getVirtualFile() != null) {
                            // 打开文件跳转
                            bookmark.openFileDescriptor(project);
                        } else {
                            // 双击不变更书签信息 否则会造成文件丢失的状态错误的显示成失效
                            DocumentService.getInstance(project).setBookmarkInvalid(bookmark.getCommitHash());
                            BookmarkNoticeUtil.errorMessages(project, "Bookmark [" + bookmark.getName() + BookmarkConstants.BOOKMARK_NAME_AND_DESC_SEPARATOR + bookmark.getDesc() + "] invalid");
                        }
                    }
                }
            }
        });
    }
}
