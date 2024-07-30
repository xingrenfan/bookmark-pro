package org.bookmark.pro.listeners.app;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationAction;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.util.io.FileUtil;
import org.bookmark.pro.constants.BookmarkProIcon;
import org.bookmark.pro.context.BookmarkRunService;
import org.bookmark.pro.utils.BookmarkNoticeUtil;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BackupScheduler {

    private final ScheduledExecutorService scheduler;

    public BackupScheduler() {
        scheduler = Executors.newScheduledThreadPool(1);
        // 默认12个小时备份一次
        long backupInterval = 12;
        try {
            backupInterval = Integer.parseInt(BookmarkRunService.getBookmarkSettings().getBackUpTime()); // 获取备份间隔，时间为小时
        }catch (Exception e) {
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
        // 项目根目录
        String projectDir = FileUtil.toSystemIndependentName(Objects.requireNonNull(project.getBasePath()));
        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();
        // 定义时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日HH小时mm分钟ss秒");
        // 格式化当前时间
        String currentTimeString = now.format(formatter);
        String sjzFileName = project.getName() + currentTimeString + ".json";

        // 如果配置了备份路径
        if (!"".equals(BookmarkRunService.getBookmarkSettings().getBackUp())) {
            String sjzPath2 = BookmarkRunService.getBookmarkSettings().getBackUp()
                    + File.separator + "MarkBook" + File.separator + project.getName();
            File folder2 = new File(sjzPath2);
            // 检查文件夹是否已经存在
            if (!folder2.exists()) {
                boolean created = folder2.mkdirs();
            }
            if (BookmarkRunService.getPersistenceService(project).exportBookmark(project,
                    sjzPath2 + File.separator + sjzFileName)) {
                sendExportNotice(project, sjzPath2);
            }
        } else {
            // 没有配置备份路径，默认导出到项目根目录下
            String sjzPath = projectDir + File.separator + "MarkBook";
            File folder = new File(sjzPath);
            // 检查文件夹是否已经存在
            if (!folder.exists()) {
                boolean created = folder.mkdirs();
            }
            if (BookmarkRunService.getPersistenceService(project).exportBookmark(project,
                    sjzPath + File.separator + sjzFileName)) {
                sendExportNotice(project, sjzPath);
            }
        }
    }

    /**
     * 发送导出通知
     *
     * @param project    项目
     * @param projectDir 项目根目录
     */
    private void sendExportNotice(Project project, String projectDir) {
        AnAction openExportFile = new NotificationAction(BookmarkProIcon.EYE_SIGN + "ViewFile") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e, @NotNull Notification notification) {
                try {
                    Desktop desktop = Desktop.getDesktop();
                    desktop.open(new File(projectDir));
                } catch (Exception ex) {
                }
            }
        };

        BookmarkNoticeUtil.projectNotice(project, String.format("Export bookmark success. Output file directory: [%s]", projectDir), openExportFile);
    }
}