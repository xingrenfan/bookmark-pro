package org.bookmark.pro.listeners.app;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import org.bookmark.pro.service.base.persistence.PersistService;
import org.bookmark.pro.service.base.task.ScheduledService;
import org.bookmark.pro.service.tree.TreeService;
import org.bookmark.pro.service.tree.component.BookmarkTree;
import org.jetbrains.annotations.NotNull;

/**
 * 项目关闭侦听器
 *
 * @author Lyon
 * @date 2024/03/21
 */
public class AppCloseService implements ProjectManagerListener {
    @Override
    public void projectClosing(@NotNull Project project) {
        // 获取书签树
        BookmarkTree bookmarkTree = TreeService.getInstance(project).getBookmarkTree();
        // 处理项目关闭事件
        PersistService.getInstance(project).saveBookmark(bookmarkTree);
        // 关闭线程池
        ScheduledService.getInstance(project).shutdown();
    }
}
