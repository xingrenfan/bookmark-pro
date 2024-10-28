package org.bookmark.pro.service.base.task.handler;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.Messages;
import org.bookmark.pro.base.BaseExportService;
import org.bookmark.pro.service.base.persistence.PersistService;
import org.bookmark.pro.service.base.settings.BackupSettings;
import org.bookmark.pro.service.base.settings.GlobalSettings;
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
import java.util.HashMap;
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

    @Override
    public void shutdown() {
        scheduler.shutdown();
    }

    public static void saveToDatabase(String projectName, String bookmarkData) {
        HashMap<String, String> mysqlMsg = getMysqlMsg();
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        String sql = "INSERT INTO bookmarks (project_name, bookmark_data,created_at) VALUES (?, ?,?)";
        try (Connection conn = DriverManager.getConnection(mysqlMsg.get("url"), mysqlMsg.get("user"),  mysqlMsg.get("password"));
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
        HashMap<String, String> mysqlMsg = getMysqlMsg();
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        String sql = "SELECT project_name, bookmark_data, created_at FROM bookmarks where project_name = '"+projectName+"' ORDER BY created_at DESC LIMIT 1";
        String latestData = null;
        try (Connection conn = DriverManager.getConnection(mysqlMsg.get("url"), mysqlMsg.get("user"),  mysqlMsg.get("password"));
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
//                String projectName = rs.getString("project_name");
                String bookmarkData = rs.getString("bookmark_data");
                String createdAt = rs.getString("created_at");
                // 拼接查询到的结果
                latestData = bookmarkData;
            } else {
                Messages.showErrorDialog("查不到mysql中的数据.", "Export Failed");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            latestData = "Error occurred while fetching data.";
        }
        return latestData;
    }

    public static HashMap<String,String> getMysqlMsg(){
        GlobalSettings globalSettings = GlobalSettings.getInstance();
        String mysqlBackUp = globalSettings.getMysqlBackUp();
        if (mysqlBackUp == null || mysqlBackUp.isBlank()) {
            Messages.showErrorDialog("没有配置mysql链接信息.", "Export Failed");
            return new HashMap<>();
        }
        String[] split = mysqlBackUp.split("\\|");
        if (split.length != 3) {
            Messages.showErrorDialog("mysql链接信息异常.", "Export Failed");
            return new HashMap<>();
        }
      String url = split[0];
      String user = split[1];
      String password = split[2];
      HashMap<String,String> msg = new HashMap<>();
      msg.put("url", url);
      msg.put("user", user);
      msg.put("password", password);
      return msg;
    }

}
