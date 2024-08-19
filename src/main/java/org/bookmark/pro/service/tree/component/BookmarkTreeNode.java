package org.bookmark.pro.service.tree.component;

import org.bookmark.pro.domain.model.AbstractTreeNodeModel;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * 书签树节点
 *
 * @author Nonoas
 * @date 2023/6/1
 */
public class BookmarkTreeNode extends DefaultMutableTreeNode {

    /**
     * 是分组
     */
    private boolean isGroup;

    /**
     * 是书签
     */
    private boolean isBookmark;

    /**
     * 失效
     */
    private Boolean invalid;

    public BookmarkTreeNode() {

    }

    public void setInvalid(boolean invalid) {
        this.invalid = invalid;
    }

    public boolean getInvalid() {
        if (invalid == null) {
            return false;
        }
        return invalid;
    }

    public BookmarkTreeNode(AbstractTreeNodeModel nodeModel) {
        super(nodeModel);
        setGroup(nodeModel.isGroup());
        setBookmark(nodeModel.isBookmark());
    }

    /**
     * 书签树节点
     *
     * @param nodeModel      节点模型
     * @param allowsChildren 允许子节点
     */
    public BookmarkTreeNode(AbstractTreeNodeModel nodeModel, boolean allowsChildren) {
        super(nodeModel, allowsChildren);
        setGroup(nodeModel.isGroup());
        setBookmark(nodeModel.isBookmark());
    }

    public BookmarkTreeNode(boolean isGroup) {
        super();
        setGroup(isGroup);
    }

    public void setGroup(boolean group) {
        isGroup = group;
        setAllowsChildren(isGroup);
    }

    public boolean isGroup() {
        return isGroup;
    }

    public void setBookmark(boolean bookmark) {
        isBookmark = bookmark;
    }

    public boolean isBookmark() {
        return isBookmark;
    }

    /**
     * 统计当前节点下，非分组节点的「书签节点」数，如果当前节点为书签节点，直接返回 0
     *
     * @return 当前节点下，非分组节点的「书签节点」数
     */
    public int getBookmarkChildCount() {
        if (!isGroup) {
            return 0;
        }
        int count = getChildCount();
        int bookmarkCount = 0;

        BookmarkTreeNode node;
        for (int i = 0; i < count; i++) {
            node = (BookmarkTreeNode) getChildAt(i);
            if (node.isBookmark()) {
                bookmarkCount++;
            }
        }
        return bookmarkCount;
    }

    public int firstChildIndex() {
        if (isBookmark()) {
            return -1;
        }
        int childCount = getChildCount();
        BookmarkTreeNode node;
        for (int i = 0; i < childCount; i++) {
            node = (BookmarkTreeNode) getChildAt(i);
            if (node.isBookmark()) {
                return i;
            }
        }
        return -1;
    }

}
