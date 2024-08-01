package org.bookmark.pro.service.tree.handler;

import com.intellij.openapi.project.Project;
import com.intellij.ui.TreeSpeedSearch;
import com.intellij.ui.TreeUIHelper;
import org.bookmark.pro.constants.BookmarkProConstant;
import org.bookmark.pro.constants.BookmarkProIcon;
import org.bookmark.pro.context.BookmarkRunService;
import org.bookmark.pro.domain.model.BookmarkNodeModel;
import org.bookmark.pro.domain.model.GroupNodeModel;
import org.bookmark.pro.service.tree.BookmarkTreeManage;
import org.bookmark.pro.utils.BookmarkNoticeUtil;
import org.bookmark.pro.utils.BookmarkProUtil;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;
import java.util.UUID;

/**
 * 书签树管理器
 *
 * @author Lyon
 * @date 2024/04/17
 */
public final class BookmarkTreeManager extends BookmarkMenus implements BookmarkTreeManage {
    private BookmarkGroupNavigator groupNavigator;

    private BookmarkTree bookmarkTree;

    private Project project;

    public BookmarkTreeManager(Project project) {
        this.project = project;
        // 初始化书签树
        this.bookmarkTree = new BookmarkTree();
        // 初始化分组导航
        this.groupNavigator = new BookmarkGroupNavigator(project, this.bookmarkTree);
        // 添加Root节点
        addProjectNode(project, this.bookmarkTree, this.groupNavigator);
        // 添加Ctrl+F搜索功能
//        addTreeSearch(this.bookmarkTree);
        // 添加拖拽处理方案
        addDragHandler(this.bookmarkTree);
        // 书签图标渲染
        bookmarkRendering(project, this.bookmarkTree);
        // 增加选中和点击事件
        addTreeListeners(project, this.bookmarkTree);
        // 书签书菜单初始化
        addTreeMenus(project, this.bookmarkTree);
    }

    public Project getOpenProject() {
        return project;
    }

    @Override
    public BookmarkTree getBookmarkTree() {
        return this.bookmarkTree;
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
        this.bookmarkTree.addNode(this.project, parentNode, node);
    }

    @Override
    public void changeBookmarkNode(BookmarkTreeNode parentNode, BookmarkTreeNode node) {
        if (node.isBookmark() && node.isGroup()) {
            node.setAllowsChildren(true);
        }
        if (parentNode == null) {
            // 不指定父节点直接更新
            bookmarkTree.getModel().nodeChanged(node);
        } else {
            // 先移除
            removeBookmarkNode(node);
            // 在添加到新节点
            addBookmarkNode(parentNode, node);
        }
        // 更新书签缓存
        BookmarkRunService.getDocumentService(project).addBookmarkNode(project, node);
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
        this.bookmarkTree.removeNode(this.project, node);
        BookmarkRunService.getDocumentService(project).removeBookmarkNode(this.project, node);
    }

    @Override
    public void nextBookmark(Project project) {
        this.groupNavigator.next();
    }

    @Override
    public void preBookmark(Project project) {
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
     * 添加书签树搜索功能
     *
     * @param bookmarkTree 书签树
     */
    /*private void addTreeSearch(BookmarkTree bookmarkTree) {
        TreeSpeedSearch treeSpeedSearch = new TreeSpeedSearch(bookmarkTree);
        treeSpeedSearch.setCanExpand(true);
        bookmarkTree.setShowsRootHandles(true);
    }*/

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
                    setIcon(BookmarkProIcon.FOLDER_ICON);
                } else if (row > 0) {
                    // 系统文件图标 AllIcons.Nodes.Folder;
                    if (node.isBookmark() && node.isGroup()) {
                        BookmarkNodeModel nodeModel = (BookmarkNodeModel) node.getUserObject();
                        if (nodeModel.getInvalid()) {
                            // 无效书签 显示失效图标
                            setIcon(BookmarkProIcon.BOOKMARK_INVALID_ICON);
                        } else if (nodeModel.getVirtualFile() != null && BookmarkProUtil.virtualFileExist(project, nodeModel.getVirtualFile())) {
                            // 虚拟文件有效 显示书签分组图标
                            setIcon(BookmarkProIcon.BOOKMARK_GROUP_ICON);
                        } else {
                            // 无效书签 显示文件丢失图标 不更新书签状态 否则会造成文件丢失的状态错误的显示成失效
                            // node.setInvalid(true);
                            // nodeModel.setInvalid(true);
                            setIcon(BookmarkProIcon.FILE_LOSE_ICON);
                        }
                    } else if (node.isBookmark()) {
                        BookmarkNodeModel nodeModel = (BookmarkNodeModel) node.getUserObject();
                        if (nodeModel.getInvalid()) {
                            // 无效书签 显示失效图标
                            setIcon(BookmarkProIcon.BOOKMARK_INVALID_ICON);
                        } else if (nodeModel.getVirtualFile() != null && BookmarkProUtil.virtualFileExist(project, nodeModel.getVirtualFile())) {
                            // 虚拟文件有效 显示书签图标
                            setIcon(BookmarkProIcon.BOOKMARK_ICON);
                        } else {
                            // 无效书签 显示文件丢失图标 不更新书签状态 否则会造成文件丢失的状态错误的显示成失效
                            // node.setInvalid(true);
                            // nodeModel.setInvalid(true);
                            setIcon(BookmarkProIcon.FILE_LOSE_ICON);
                        }
                    } else if (node.isGroup()) {
                        // 书签分组图标
                        setIcon(BookmarkProIcon.GROUP_ICON);
                    }
                }
                return this;
            }
        });
    }

    /**
     * 增加书签树选中和鼠标点击事件
     *
     * @param project
     * @param bookmarkTree 书签树
     */
    private void addTreeListeners(Project project, BookmarkTree bookmarkTree) {
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
                            // selectedNode.setInvalid(true);
                            BookmarkRunService.getDocumentService(project).setBookmarkInvalid(bookmark.getUuid());
                            BookmarkNoticeUtil.errorMessages(project, "Bookmark [" + bookmark.getName() + BookmarkProConstant.BOOKMARK_NAME_AND_DESC_SEPARATOR + bookmark.getDesc() + "] invalid");
                        }
                    }
                }
            }
        });
    }
}
