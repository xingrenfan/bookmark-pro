package org.bookmark.pro.service.task.handler;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import org.bookmark.pro.base.BaseExportService;
import org.bookmark.pro.context.AppRunContext;
import org.bookmark.pro.context.BookmarkRunService;
import org.bookmark.pro.service.settings.BackupSettings;
import org.bookmark.pro.service.task.ScheduledService;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 计划服务实施
 *
 * @author Lyon
 * @date 2024/08/14
 */
public final class ScheduledServiceImpl implements ScheduledService, BaseExportService {
    private ScheduledExecutorService scheduler;

    @Override
    public void initScheduledService() {
        scheduler = Executors.newScheduledThreadPool(1);
        BackupSettings globalSettings = AppRunContext.getAppService(BackupSettings.class);
        // 获取备份间隔，时间为小时 默认12个小时备份一次
        long backupInterval = Integer.parseInt(globalSettings.getBackUpTime());
        // 设置初始延迟时间为备份间隔时间
        scheduler.scheduleAtFixedRate(() -> this.performBackupForAllProjects(globalSettings), backupInterval, backupInterval, TimeUnit.HOURS);
    }

    private void performBackupForAllProjects(BackupSettings globalSettings) {
        if (globalSettings.getAutoBackup()) {
            Project[] projects = ProjectManager.getInstance().getOpenProjects();
            for (Project project : projects) {
                if (project != null) {
                    File autoBackupFile = getAutoBackupRootPath(project);
                    String fileName = project.getName() + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HHmmss")) + ".json";
                    BookmarkRunService.getPersistenceService(project).exportBookmark(project, autoBackupFile.getPath() + File.separator + fileName);
                }
            }
        }
    }

    public void shutdown() {
        scheduler.shutdown();
    }
}
