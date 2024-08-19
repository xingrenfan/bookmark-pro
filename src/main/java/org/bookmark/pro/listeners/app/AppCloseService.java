package org.bookmark.pro.listeners.app;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import org.bookmark.pro.service.persistence.PersistService;
import org.bookmark.pro.service.task.ScheduledService;
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
        // 处理项目关闭事件
        PersistService.getInstance(project).saveBookmark();
        // 关闭线程池
        ScheduledService.getInstance().shutdown();
    }
}
