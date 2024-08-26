package org.bookmark.pro.service.base.task.handler;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import org.bookmark.pro.base.BaseExportService;
import org.bookmark.pro.service.base.persistence.PersistService;
import org.bookmark.pro.service.base.settings.BackupSettings;
import org.bookmark.pro.service.base.task.ScheduledService;

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

    private Project openProject;

    public ScheduledServiceImpl(Project openProject) {
        this.openProject = openProject;
    }

    @Override
    public void initScheduledService() {
        scheduler = Executors.newScheduledThreadPool(1);
        BackupSettings globalSettings = BackupSettings.getInstance();
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
                    PersistService.getInstance(project).exportBookmark(autoBackupFile.getPath() + File.separator + fileName);
                }
            }
        }
    }

    public void shutdown() {
        scheduler.shutdown();
    }
}
