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
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
        }
    }
}
