package org.bookmark.pro.service.document;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.bookmark.pro.service.tree.handler.BookmarkTreeNode;

import javax.swing.tree.TreeNode;
import java.util.List;
import java.util.Set;

/**
 * 文档检查|索引服务
 *
 * @author Lyon
 * @date 2024/04/19
 */
public interface DocumentService {
    /**
     * 根据文件名和行数 获取书签节点
     *
     * @param project
     * @param virtualFile 虚拟文件
     * @param line        行数
     * @return {@link BookmarkTreeNode}
     */
    BookmarkTreeNode getBookmarkNode(Project project, VirtualFile virtualFile, int line);

    /**
     * 根据书签UUID 获取书签节点
     *
     * @param uuid UUID
     * @return {@link BookmarkTreeNode}
     */
    BookmarkTreeNode getBookmarkNode(String uuid);

    /**
     * 获取书签节点
     *
     * @param virtualFile 虚拟文件
     * @param project     项目
     * @return {@link Set}<{@link BookmarkTreeNode}>
     */
    Set<BookmarkTreeNode> getBookmarkNodes(Project project, VirtualFile virtualFile);

    /**
     * 添加书签节点
     *
     * @param bookmarkNode 书签节点
     * @param project      项目
     */
    void addBookmarkNode(Project project, BookmarkTreeNode bookmarkNode);

    /**
     * 从缓存中删除书签节点
     *
     * @param bookmarkNode 书签节点
     * @param project      项目
     */
    void removeBookmarkNode(Project project, BookmarkTreeNode bookmarkNode);

    /**
     * 设置书签无效
     *
     * @param uuid UUID
     */
    void setBookmarkInvalid(String uuid);

    /**
     * 重新加载树节点
     *
     * @param project
     * @param treeNode 树节点
     */
    void reloadingCacheNode(Project project, TreeNode treeNode);

    /**
     * 获取书签分组
     *
     * @return {@link List}<{@link BookmarkTreeNode}>
     */
    List<BookmarkTreeNode> getBookmarkGroup();

    /**
     * 获取组节点
     *
     * @param nodeName 节点名称
     * @return {@link BookmarkTreeNode}
     */
    BookmarkTreeNode getGroupNode(String nodeName);
}
