package org.bookmark.pro.listeners.app;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import org.bookmark.pro.base.BaseExportService;
import org.bookmark.pro.context.BookmarkRunService;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BackupScheduler implements BaseExportService {

    private final ScheduledExecutorService scheduler;

    public BackupScheduler() {
        scheduler = Executors.newScheduledThreadPool(1);
        // 默认12个小时备份一次
        long backupInterval = 12;
        try {
            backupInterval = Integer.parseInt(BookmarkRunService.getBookmarkSettings().getBackUpTime()); // 获取备份间隔，时间为小时
        } catch (Exception e) {
            e.printStackTrace();
        }
        scheduler.scheduleAtFixedRate(this::performBackupForAllProjects, 0, backupInterval, TimeUnit.HOURS);
    }

    private void performBackupForAllProjects() {
        Project[] projects = ProjectManager.getInstance().getOpenProjects();
        for (Project project : projects) {
            if (project != null) {
                performBackup(project);
            }
        }
    }

    private void performBackup(Project project) {
        if (BookmarkRunService.getBookmarkSettings().getAutoBackup()) {
            File autoBackupFile = getAutoBackupRootPath(project);
            String fileName = project.getName() + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HHmmss")) + ".json";
            BookmarkRunService.getPersistenceService(project).exportBookmark(project, autoBackupFile.getPath() + File.separator + fileName);
        }
    }
}