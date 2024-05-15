package org.bookmark.pro.service.tree.handler;

import com.intellij.openapi.project.Project;
import com.intellij.ui.treeStructure.Tree;
import org.bookmark.pro.context.BookmarkRunService;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * 书签树
 *
 * @author Lyon
 * @date 2024/04/18
 */
public class BookmarkTree extends Tree {
    private BookmarkGroupNavigator groupNavigator;

    private DefaultTreeModel model;

    protected BookmarkTree() {
        super();
    }

    protected void setDefaultModel(DefaultTreeModel model, BookmarkGroupNavigator groupNavigator) {
        this.groupNavigator = groupNavigator;
        this.model = model;
        setModel(model);
    }

    protected DefaultTreeModel getDefaultModel() {
        return this.model;
    }

    @Override
    public void setModel(TreeModel newModel) {
        this.model = (DefaultTreeModel) newModel;
        Object root = model.getRoot();
        if (root instanceof BookmarkTreeNode) {
            this.groupNavigator.setActivatedGroup((BookmarkTreeNode) root);
        }
        super.setModel(this.model);
    }

    @Override
    public DefaultTreeModel getModel() {
        return this.model;
    }

    /**
     * 向当前激活的分组添加指定节点，并刷新树结构
     *
     * @param parent 父节点
     * @param node   要添加的节点
     */
    protected void addNode(Project project, BookmarkTreeNode parent, BookmarkTreeNode node) {
        model.insertNodeInto(node, parent, parent.getChildCount());
        // 定位到新增的节点并使其可见
        scrollPathToVisible(new TreePath(node.getPath()));
        // 缓存书签树
        BookmarkRunService.getDocumentService(project).addBookmarkNode(project, node);
    }

    /**
     * 删除指定节点，并刷新树结构
     *
     * @param node 要删除的节点
     */
    protected void removeNode(Project project, BookmarkTreeNode node) {
        model.removeNodeFromParent(node);
        // 删除缓存书签树
        BookmarkRunService.getDocumentService(project).removeBookmarkNode(project, node);
    }

    /**
     * 从指定行获取节点
     *
     * @param row 指定行
     * @return {@link BookmarkTreeNode}
     */
    protected BookmarkTreeNode getNodeForRow(int row) {
        TreePath path = getPathForRow(row);
        if (path != null) {
            return (BookmarkTreeNode) path.getLastPathComponent();
        } else {
            return null;
        }
    }
}
