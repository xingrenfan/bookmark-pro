package org.bookmark.pro.windows;

import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;
import org.apache.commons.lang3.StringUtils;
import org.bookmark.pro.base.I18N;
import org.bookmark.pro.context.BookmarkRunService;
import org.bookmark.pro.domain.BookmarkPro;
import org.bookmark.pro.domain.model.BookmarkConverter;
import org.bookmark.pro.domain.model.BookmarkNodeModel;
import org.bookmark.pro.service.persistence.PersistenceService;
import org.bookmark.pro.service.tree.handler.BookmarkTree;
import org.bookmark.pro.service.tree.handler.BookmarkTreeNode;
import org.bookmark.pro.utils.BookmarkNoticeUtil;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.util.Objects;
import java.util.concurrent.ExecutionException;


/**
 * 标签目录管理面板
 *
 * @author Nonoas
 * @date 2023/6/1
 */
public class BookmarkManagerPanel extends JPanel {

    /**
     * 标记 tree 是否已经从持久化文件加载完成
     */
    private volatile boolean treeLoaded = false;

    private final JEditorPane jepDesc = new JEditorPane();
    private final JTextField searchField = new JTextField(20); // 添加搜索框

    private Project openProject;

    public BookmarkManagerPanel(Project project) {
        this.openProject = project;
        BookmarkTree bookmarkTree = BookmarkRunService.getBookmarkManage(project).getBookmarkTree();
        // 布局
        setLayout(new BorderLayout());
        // 设置书签树滚动窗口 鼠标滚轮滑动
        JBScrollPane scrollPane = new JBScrollPane(bookmarkTree);

        // 设置区域不可编辑
        jepDesc.setEditable(false);
        scrollPane.setBorder(JBUI.Borders.empty());

        // 居中显示书签树
        add(scrollPane, BorderLayout.CENTER);
        add(jepDesc, BorderLayout.SOUTH);

        // 添加搜索框到顶部
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(new JLabel(I18N.get("manager.panel.search")), BorderLayout.WEST);
        topPanel.add(searchField, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // 设置边框样式
        setBorder(JBUI.Borders.empty(2));

        // 设置背景色
        setBackground(JBColor.WHITE);

        reloadBookmarkTree(project, bookmarkTree);

        // 添加搜索框监听器
        addSearchListener(project, bookmarkTree);
    }

    private void addSearchListener(Project project, BookmarkTree bookmarkTree) {
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterTree();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterTree();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterTree();
            }

            private void filterTree() {
                String filterText = searchField.getText();
                if (StringUtils.isBlank(filterText)) {
                    reloadBookmarkTree(project, bookmarkTree);
                } else {
                    new TreeLoadWorker(project, bookmarkTree, filterText, true).execute();
                }
            }
        });
    }

    public Project getOpenProject() {
        return openProject;
    }

    /**
     * 重新加载书签树
     *
     * @param project      项目
     * @param bookmarkTree 书签树
     */
    public void reloadBookmarkTree(Project project, BookmarkTree bookmarkTree) {
        new TreeLoadWorker(project, bookmarkTree, null, false).execute();
    }

    /**
     * 展开所有节点
     *
     * @param tree     书签树
     * @param start    起始行
     * @param rowCount 行数
     */
    private void expandAllNodes(JTree tree, int start, int rowCount) {
        for (int i = start; i < rowCount; ++i) {
            tree.expandRow(i);
        }
        if (tree.getRowCount() != rowCount) {
            expandAllNodes(tree, rowCount, tree.getRowCount());
        }
    }

    /**
     * 添加一个书签
     *
     * @param project
     * @param bookmarkModel 书签模型
     */
    public void addOneBookmark(Project project, BookmarkTreeNode parentNode, BookmarkNodeModel bookmarkModel) {
        // 一次都没有创建过项目
        if (BookmarkRunService.getBookmarkManagerPanel(project) == null) {
            BookmarkPro bookmark = BookmarkConverter.modelToBean(bookmarkModel);
            // 获取持久化书签对象
            BookmarkRunService.getPersistenceService(project).addOneBookmark(project, bookmark);
            return;
        }
        // 添加书签
        if (!treeLoaded) {
            return;
        }
        BookmarkTreeNode treeNode = new BookmarkTreeNode(bookmarkModel, true);
        if (parentNode == null) {
            // 书签管理窗口添加书签
            BookmarkRunService.getBookmarkManage(project).addBookmarkNode(treeNode);
        } else {
            // 书签管理窗口添加书签
            BookmarkRunService.getBookmarkManage(project).addBookmarkNode(parentNode, treeNode);
        }
        bookmarkModel.setIndex(treeNode.getParent().getIndex(treeNode));
        // 添加书签到缓存
        BookmarkRunService.getDocumentService(project).addBookmarkNode(project, treeNode);
    }

    public class TreeLoadWorker extends SwingWorker<DefaultTreeModel, Void> {
        private final Project project;
        private final BookmarkTree bookmarkTree;
        private final String searchText;
        private final boolean enableSearch;

        TreeLoadWorker(Project project, BookmarkTree tree, String searchText, boolean enableSearch) {
            this.bookmarkTree = tree;
            this.project = project;
            this.searchText = searchText;
            this.enableSearch = enableSearch;
        }

        @Override
        protected DefaultTreeModel doInBackground() throws Exception {
            PersistenceService service = BookmarkRunService.getPersistenceService(openProject);
            if (enableSearch) {
                return new DefaultTreeModel(service.getBookmarkNodeSearch(openProject, searchText));
            }
            // 获取持久化书签对象
            return new DefaultTreeModel(service.getBookmarkNode(openProject));
        }

        @Override
        protected void done() {
            DefaultTreeModel treeModel;
            try {
                treeModel = get();
            } catch (InterruptedException | ExecutionException e) {
                return;
            }

            treeModel.addTreeModelListener(new TreeModelListener() {
                @Override
                public void treeNodesChanged(TreeModelEvent e) {
                    persistenceSave();
                }

                @Override
                public void treeNodesInserted(TreeModelEvent e) {
                    persistenceSave();
                }

                @Override
                public void treeNodesRemoved(TreeModelEvent e) {
                    persistenceSave();
                }

                @Override
                public void treeStructureChanged(TreeModelEvent e) {
                    // do nothing
                }

                private void persistenceSave() {
                    new SwingWorker<Void, Void>() {
                        @Override
                        protected Void doInBackground() throws Exception {
                            try {
                                BookmarkRunService.getPersistenceService(project).saveBookmark(project);
                            } catch (Exception e) {
                                BookmarkNoticeUtil.projectNotice(project, "出现异常" + e.getMessage());
                            }
                            return null;
                        }
                    }.execute();
                }
            });

            try {
                this.bookmarkTree.setModel(treeModel);
                TreeNode treeNode = (TreeNode) treeModel.getRoot();
                treeModel.nodeStructureChanged(treeNode);
                // 初始化加载数据到缓存
                BookmarkRunService.getDocumentService(project).reloadingCacheNode(project, treeNode);
                // 书签选中之后显示内容
                this.bookmarkTree.addTreeSelectionListener(event -> {
                    BookmarkTreeNode selectedNode = (BookmarkTreeNode) bookmarkTree.getLastSelectedPathComponent();
                    if (selectedNode != null && selectedNode.isBookmark()) {
                        BookmarkNodeModel bookmark = (BookmarkNodeModel) selectedNode.getUserObject();
                        jepDesc.setText(StringUtils.abbreviate(Objects.toString(bookmark.getDesc()), "...", BookmarkRunService.getBookmarkSettings().getTreePanelShowNum()));
                    } else {
                        jepDesc.setText("");
                    }
                });
                treeLoaded = true;
                if (enableSearch) {
                    expandAllNodes(bookmarkTree, 0, bookmarkTree.getRowCount());
                }
            } catch (Exception e) {
                BookmarkNoticeUtil.projectNotice(project, "出现异常" + e.getMessage());
            }
        }
    }
}
