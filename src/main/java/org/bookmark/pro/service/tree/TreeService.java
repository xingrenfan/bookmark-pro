package org.bookmark.pro.service.tree;

import com.intellij.openapi.project.Project;
import org.bookmark.pro.service.tree.handler.BookmarkTree;
import org.bookmark.pro.service.tree.handler.BookmarkTreeNode;
import org.bookmark.pro.service.tree.handler.TreeServiceImpl;

/**
 * 书签树管理器
 *
 * @author Lyon
 * @date 2024/04/19
 */
public interface TreeService {
    /**
     * 获取书签树
     *
     * @return {@link BookmarkTree}
     */
    BookmarkTree getBookmarkTree();

    /**
     * 添加书签树节点 - 指定父节点
     *
     * @param node 节点
     */
    void addBookmarkNode(BookmarkTreeNode parentNode, BookmarkTreeNode node);

    /**
     * 更改书签节点
     *
     * @param parentNode 父节点
     * @param node       节点
     */
    void changeBookmarkNode(BookmarkTreeNode parentNode, BookmarkTreeNode node);

    /**
     * 更改书签节点
     *
     * @param node 节点
     */
    void changeBookmarkNode(BookmarkTreeNode node);

    /**
     * 设置选择节点
     *
     * @param selectedNode 选定节点
     */
    void setSelectNode(BookmarkTreeNode selectedNode);

    /**
     * 删除书签节点
     *
     * @param node 节点
     */
    void removeBookmarkNode(BookmarkTreeNode node);

    /**
     * 下一个书签
     *
     * @param project 项目
     */
    void nextBookmark(Project project);

    /**
     * 上一个书签
     *
     * @param project 项目
     */
    void preBookmark(Project project);

    static TreeService getInstance(Project project) {
        return project.getService(TreeServiceImpl.class);
    }
}
