package org.bookmark.pro.service.persistence;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.bookmark.pro.domain.BookmarkPro;
import org.bookmark.pro.service.ServiceContext;
import org.bookmark.pro.service.persistence.handler.PersistServiceImpl;
import org.bookmark.pro.service.tree.component.BookmarkTreeNode;

/**
 * 持久性服务
 *
 * @author Lyon
 * @date 2024/04/24
 */
public interface PersistService {
    /**
     * 保存书签
     *
     * @param project 项目
     */
    void saveBookmark();

    /**
     * 导出书签
     *
     * @param project  项目
     * @param savePath 保存路径
     * @return boolean
     */
    boolean exportBookmark(String savePath);

    /**
     * 导入书签
     *
     * @param project     项目
     * @param virtualFile 虚拟文件
     * @return boolean
     */
    boolean importBookmark(VirtualFile virtualFile);

    /**
     * 添加一个书签
     *
     * @param bookmark 书签
     * @param project  项目
     */
    void addOneBookmark(BookmarkPro bookmark);

    /**
     * 获取书签节点
     *
     * @param project 项目
     * @return {@link BookmarkTreeNode}
     */
    BookmarkTreeNode getBookmarkNode();

    /**
     * 获取书签节点----具有搜索功能
     *
     * @param project 项目
     * @return {@link BookmarkTreeNode}
     */
    BookmarkTreeNode getBookmarkNodeSearch(String searchText);

    static PersistService getInstance(Project project) {
        return ServiceContext.getContextAttribute(project).getPersistService();
    }
}
