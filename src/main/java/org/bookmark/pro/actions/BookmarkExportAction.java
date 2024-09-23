package org.bookmark.pro.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.InputValidatorEx;
import com.intellij.openapi.ui.Messages;
import org.apache.commons.lang3.StringUtils;
import org.bookmark.pro.base.BaseExportService;
import org.bookmark.pro.base.I18N;
import org.bookmark.pro.service.tree.component.BookmarkTree;
import org.bookmark.pro.utils.BookmarkNoticeUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.bookmark.pro.service.base.task.handler.ScheduledServiceImpl.saveToDatabase;

/**
 * 书签导出操作
 *
 * @author Nonoas
 * @date 2024-1-31
 */
public final class BookmarkExportAction extends AnAction implements BaseExportService {
    public BookmarkExportAction() {
        super(I18N.get("export.title"), null, AllIcons.ToolbarDecorator.Export);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (null == project) {
            return;
        }
        File backupRootFile = getRootPath(project);
        InputValidatorEx validatorEx = inputString -> {
            if (StringUtils.isBlank(inputString)) return "Save file name is not empty.";
            return null;
        };
        // 备份文件名
        String fileName = project.getName() + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HHmmss")) + ".json";
        String newFileName = Messages.showInputDialog("name:", "SaveFileName", null, fileName, validatorEx);
        if (StringUtils.isNotEmpty(newFileName)) {
            fileName = newFileName;
            exportSendNotice(project, backupRootFile.getPath(), fileName);
            // 读取文件内容并存储到 MySQLame;
            try {
                // 读取文件中的 JSON 内容
                String bookmarkData = new String(Files.readAllBytes(Paths.get(backupRootFile.getPath()+File.separator + fileName)));
                // 保存到 MySQL
                saveToDatabase(project.getName(), bookmarkData);
            } catch (IOException ee) {
                ee.printStackTrace();
                BookmarkNoticeUtil.projectNotice(project, "Failed to read backup file and store to database.", null);
            }
        }
    }
}
