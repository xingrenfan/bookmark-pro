package org.bookmark.pro.base;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationAction;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import org.apache.commons.lang3.StringUtils;
import org.bookmark.pro.constants.BookmarkIcons;
import org.bookmark.pro.service.base.persistence.PersistService;
import org.bookmark.pro.service.base.settings.BackupSettings;
import org.bookmark.pro.utils.BookmarkNoticeUtil;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

import static org.bookmark.pro.service.base.task.handler.ScheduledServiceImpl.saveToDatabase;

/**
 * 基础出口服务
 *
 * @author Lyon
 * @date 2024/07/30
 */
public interface BaseExportService {
    default File getRootPath(Project project) {
        File backupRootFile = new File(getRoot(project));
        // 检查文件夹是否已经存在
        if (!backupRootFile.exists()) {
            backupRootFile.mkdirs();
        }
        return backupRootFile;
    }

    default String getRoot(Project project) {
        BackupSettings backupSettings = BackupSettings.getInstance();
        if (StringUtils.isBlank(backupSettings.getBackUp())) {
            // 项目跟目录
            String backupRoot = FileUtil.toSystemIndependentName(Objects.requireNonNull(project.getBasePath())) + File.separator + "BookmarkBackup";
            backupSettings.setBackUP(backupRoot);
            return backupRoot;
        } else {
            // 用户自定义路径
            return backupSettings.getBackUp();
        }
    }

    default File getAutoBackupRootPath(Project project) {
        File backupRootFile = new File(getRoot(project) + File.separator + "AutoBackup");
        // 检查文件夹是否已经存在
        if (!backupRootFile.exists()) {
            backupRootFile.mkdirs();
        }
        return backupRootFile;
    }

    /**
     * 导出并且发送通知
     *
     * @param project    项目
     * @param backupRoot 备份根
     * @param fileName   文件名
     */
    default void exportSendNotice(Project project, String backupRoot, String fileName) {
        String backupFile = backupRoot + File.separator + fileName;
        if (!PersistService.getInstance(project).exportBookmark(backupFile)) {

            // 读取文件内容并存储到 MySQLame;
            try {
                // 读取文件中的 JSON 内容
                String bookmarkData = new String(Files.readAllBytes(Paths.get(backupFile)));
                // 保存到 MySQL
                saveToDatabase(project.getName(), bookmarkData);
            } catch (IOException e) {
                e.printStackTrace();
                BookmarkNoticeUtil.projectNotice(project, "Failed to read backup file and store to database.", null);
            }

            return;
        }
        AnAction openExportFile = new NotificationAction(BookmarkIcons.EYE_SIGN + "ViewFile") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e, @NotNull Notification notification) {
                try {
                    Desktop desktop = Desktop.getDesktop();
                    desktop.open(new File(backupRoot));
                } catch (Exception ex) {
                }
            }
        };
        BookmarkNoticeUtil.projectNotice(project, String.format("Export bookmark success.Out file directory:[%s]", backupFile), openExportFile);
    }
}
