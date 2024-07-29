package org.bookmark.pro.service.persistence;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.bookmark.pro.domain.BookmarkPro;
import org.bookmark.pro.service.tree.handler.BookmarkTreeNode;

/**
 * 持久性服务
 *
 * @author Lyon
 * @date 2024/04/24
 */
public interface PersistenceService {
    Project getOpenProject();

    /**
     * 保存书签
     *
     * @param project 项目
     */
    void saveBookmark(Project project);

    /**
     * 导出书签
     *
     * @param project  项目
     * @param savePath 保存路径
     * @return boolean
     */
    boolean exportBookmark(Project project, String savePath);

    /**
     * 导入书签
     *
     * @param project     项目
     * @param virtualFile 虚拟文件
     * @return boolean
     */
    boolean importBookmark(Project project, VirtualFile virtualFile);

    /**
     * 添加一个书签
     *
     * @param bookmark 书签
     * @param project  项目
     */
    void addOneBookmark(Project project, BookmarkPro bookmark);

    /**
     * 获取书签节点
     *
     * @param project 项目
     * @return {@link BookmarkTreeNode}
     */
    BookmarkTreeNode getBookmarkNode(Project project);

    /**
     * 获取书签节点----具有搜索功能
     *
     * @param project 项目
     * @return {@link BookmarkTreeNode}
     */
    BookmarkTreeNode getBookmarkNodeSearch(Project project,String searchText);
}
