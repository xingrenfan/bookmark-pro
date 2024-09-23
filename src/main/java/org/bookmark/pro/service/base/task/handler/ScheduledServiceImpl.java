package org.bookmark.pro.service.base.task.handler;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.Messages;
import org.bookmark.pro.base.BaseExportService;
import org.bookmark.pro.service.base.persistence.PersistService;
import org.bookmark.pro.service.base.settings.BackupSettings;
import org.bookmark.pro.service.base.task.ScheduledService;
import org.bookmark.pro.utils.BookmarkNoticeUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
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

                    // 读取文件内容并存储到 MySQL
                    String backupFilePath = autoBackupFile.getPath() + File.separator + fileName;
                    try {
                        // 读取文件中的 JSON 内容
                        String bookmarkData = new String(Files.readAllBytes(Paths.get(backupFilePath)));
                        // 保存到 MySQL
                        saveToDatabase(project.getName(), bookmarkData);
                    } catch (IOException e) {
                        e.printStackTrace();
                        BookmarkNoticeUtil.projectNotice(project, "Failed to read backup file and store to database.", null);
                    }
                }
            }
        }
    }

    @Override
    public void shutdown() {
        scheduler.shutdown();
    }

    public static String url = "jdbc:mysql://www.nihaoii.fun:63306/bookmarks";
    public static String user = "bookmarks";
    public static String password = "bookmarks";

    public static void saveToDatabase(String projectName, String bookmarkData) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        String sql = "INSERT INTO bookmarks (project_name, bookmark_data,created_at) VALUES (?, ?,?)";
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, projectName);
            pstmt.setString(2, bookmarkData);
            pstmt.setString(3, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            pstmt.executeUpdate();
            // 成功保存书签数据，通知用户
            Messages.showInfoMessage("Bookmark exported to database successfully!", "Export Success");
        } catch (SQLException ex) {
            ex.printStackTrace();
            Messages.showErrorDialog("Failed to export bookmark to database.", "Export Failed");
        }
    }

    public static String getLatestBookmarkData(String projectName) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        String sql = "SELECT project_name, bookmark_data, created_at FROM bookmarks where project_name = '"+projectName+"' ORDER BY created_at DESC LIMIT 1";
        String latestData = null;
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
//                String projectName = rs.getString("project_name");
                String bookmarkData = rs.getString("bookmark_data");
                String createdAt = rs.getString("created_at");
                // 拼接查询到的结果
                latestData = bookmarkData;
            } else {
                latestData = "No data found.";
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            latestData = "Error occurred while fetching data.";
        }
        return latestData;
    }

}
