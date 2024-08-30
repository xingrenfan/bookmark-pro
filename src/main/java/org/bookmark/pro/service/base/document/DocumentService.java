package org.bookmark.pro.service.base.document;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.bookmark.pro.service.ServiceContext;
import org.bookmark.pro.service.tree.component.BookmarkTreeNode;

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
     * @param virtualFile 虚拟文件
     * @param line        行数
     * @return {@link BookmarkTreeNode}
     */
    BookmarkTreeNode getBookmarkNode(VirtualFile virtualFile, int line);

    /**
     * 根据书签UUID 获取书签节点
     *
     * @param commitHash UUID
     * @return {@link BookmarkTreeNode}
     */
    BookmarkTreeNode getBookmarkNode(String commitHash);

    /**
     * 获取书签节点
     *
     * @param virtualFile 虚拟文件
     * @return {@link Set}<{@link BookmarkTreeNode}>
     */
    Set<BookmarkTreeNode> getBookmarkNodes(VirtualFile virtualFile);

    /**
     * 添加书签节点
     *
     * @param bookmarkNode 书签节点
     */
    void addBookmarkNode(BookmarkTreeNode bookmarkNode);

    /**
     * 从缓存中删除书签节点
     *
     * @param bookmarkNode 书签节点
     */
    void removeBookmarkNode(BookmarkTreeNode bookmarkNode);

    /**
     * 设置书签无效
     *
     * @param commitHash UUID
     */
    void setBookmarkInvalid(String commitHash);

    /**
     * 重新加载树节点
     *
     * @param treeNode 树节点
     */
    void reloadingCacheNode(TreeNode treeNode);

    /**
     * 获取书签分组
     *
     * @return {@link List}<{@link BookmarkTreeNode}>
     */
    List<BookmarkTreeNode> getBookmarkGroup();

    static DocumentService getInstance(Project project) {
        return ServiceContext.getContextAttribute(project).getDocumentService();
    }
}
