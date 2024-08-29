package org.bookmark.pro.windows.mark;

import org.bookmark.pro.service.tree.component.BookmarkTreeNode;

/**
 * 书签选择器
 *
 * @author Lyon
 * @date 2024/04/29
 */
public interface BookmarkSelector {
    /**
     * 选择器
     *
     * @param parentNode  书签父节点
     * @param enableGroup 书签是否启用分组功能
     */
    void selector(BookmarkTreeNode parentNode, boolean enableGroup);
}
