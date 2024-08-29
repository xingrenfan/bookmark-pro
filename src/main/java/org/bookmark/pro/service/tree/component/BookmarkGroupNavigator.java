package org.bookmark.pro.service.tree.component;

import com.intellij.openapi.project.Project;
import org.apache.commons.lang3.Validate;
import org.bookmark.pro.domain.model.BookmarkNodeModel;

import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 * 标签树的导航器，与快捷键绑定，用于遍历当前选中的分组下的标签，当前分组的下级分组不会被遍历
 *
 * @author Lyon
 * @date 2024/03/27
 */
public class BookmarkGroupNavigator {
    private final BookmarkTree bookmarkTree;

    private BookmarkTreeNode activatedGroup;

    private BookmarkTreeNode activatedBookmark;

    private Project openProject;

    public BookmarkGroupNavigator(Project project, BookmarkTree bookmarkTree) {
        this.openProject = project;
        this.bookmarkTree = bookmarkTree;
    }

    public void setActivatedGroup(BookmarkTreeNode groupTreeNode) {
        this.activatedGroup = groupTreeNode;
    }


    public void setActivatedBookmark(BookmarkTreeNode bookmarkNode) {
        this.activatedBookmark = bookmarkNode;
    }

    public void pre() {
        BookmarkTreeNode group = ensureActivatedGroup();
        if (0 == group.getBookmarkChildCount()) {
            return;
        }

        BookmarkTreeNode bookmark = ensureActivatedBookmark();
        int index = preTreeNodeIndex(group, bookmark);
        navigateTo(index);
    }

    public void next() {
        BookmarkTreeNode group = ensureActivatedGroup();
        if (0 == group.getBookmarkChildCount()) {
            return;
        }

        BookmarkTreeNode bookmark = ensureActivatedBookmark();
        int index = nextTreeNodeIndex(group, bookmark);
        navigateTo(index);
    }

    /**
     * 选中分组时激活
     *
     * @param node 节点
     */
    public void activeGroup(BookmarkTreeNode node) {
        activatedGroup = node;
        if (node.getChildCount() > 0) {
            activatedBookmark = (BookmarkTreeNode) node.getChildAt(0);
        } else {
            activatedBookmark = null;
        }
    }

    /**
     * 选中书签时激活
     *
     * @param node 节点
     */
    public void activeBookmark(BookmarkTreeNode node) {
        activatedBookmark = node;
        activatedGroup = (BookmarkTreeNode) node.getParent();

        TreePath treePath = new TreePath(node.getPath());
        this.bookmarkTree.setSelectionPath(treePath);

        if (!this.bookmarkTree.isVisible(treePath)) {
            this.bookmarkTree.scrollPathToVisible(treePath);
        }
    }

    /**
     * 确保 {@code activatedGroup} 一定是一个在当前树上的节点，
     * 所有读取 {@code activatedGroup} 值的地方都应该调用这个方法，
     * 避免当 {@code activatedGroup} 指向的节点已经从当前的 tree 中移除
     *
     * @return 激活的节点或者根节点
     */
    public BookmarkTreeNode ensureActivatedGroup() {
        if (null == activatedGroup) {
            return (BookmarkTreeNode) this.bookmarkTree.getModel().getRoot();
        }
        TreeNode[] path = activatedGroup.getPath();
        int row = this.bookmarkTree.getRowForPath(new TreePath(path));
        if (row < 0) {
            return (BookmarkTreeNode) this.bookmarkTree.getModel().getRoot();
        }
        return activatedGroup;
    }

    /**
     * 确保 {@code activatedBookmark} 一定是一个在当前树上的节点，
     * 所有读取 {@code activatedBookmark} 值的地方都应该调用这个方法，
     * 避免当 {@code activatedBookmark} 指向的节点已经从当前的 tree 中移除
     *
     * @return 激活的节点 或者 {@code null}
     */
    private BookmarkTreeNode ensureActivatedBookmark() {
        if (null == activatedBookmark) {
            return null;
        }
        TreeNode[] path = activatedBookmark.getPath();
        int row = this.bookmarkTree.getRowForPath(new TreePath(path));

        return row < 0 ? null : activatedBookmark;
    }

    private void navigateTo(int index) {
        Validate.isTrue(index >= 0, "index must be greater than 0");

        BookmarkTreeNode nextNode = (BookmarkTreeNode) activatedGroup.getChildAt(index);
        activeBookmark(nextNode);

        BookmarkNodeModel model = (BookmarkNodeModel) nextNode.getUserObject();
        model.openFileDescriptor(this.openProject);
    }

    private int preTreeNodeIndex(BookmarkTreeNode activeGroup, BookmarkTreeNode activatedBookmark) {
        Validate.isTrue(activeGroup.getBookmarkChildCount() > 0, "activeGroup has no child");
        if (null == activatedBookmark) {
            return activeGroup.firstChildIndex();
        }
        int currIndex = activeGroup.getIndex(activatedBookmark);
        int groupSize = activeGroup.getChildCount();

        BookmarkTreeNode node;
        do {
            currIndex = (currIndex - 1 + groupSize) % groupSize;
            node = (BookmarkTreeNode) activeGroup.getChildAt(currIndex);
        } while (node.isGroup());
        return currIndex;
    }

    private int nextTreeNodeIndex(BookmarkTreeNode activeGroup, BookmarkTreeNode activatedBookmark) {
        Validate.isTrue(activeGroup.getBookmarkChildCount() > 0, "activeGroup has no child");
        if (null == activatedBookmark) {
            return activeGroup.firstChildIndex();
        }

        int currIndex = activeGroup.getIndex(activatedBookmark);
        BookmarkTreeNode node;
        do {
            currIndex = (currIndex + 1) % activeGroup.getChildCount();
            node = (BookmarkTreeNode) activeGroup.getChildAt(currIndex);
        } while (node.isGroup());
        return currIndex;
    }
}
