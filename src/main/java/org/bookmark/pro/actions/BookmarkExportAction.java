package org.bookmark.pro.actions;

import com.intellij.icons.AllIcons;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationAction;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.InputValidatorEx;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.io.FileUtil;
import org.bookmark.pro.constants.BookmarkProIcon;
import org.bookmark.pro.context.BookmarkRunService;
import org.bookmark.pro.dialogs.setting.GeneralSettings;
import org.bookmark.pro.utils.BookmarkNoticeUtil;
import org.bookmark.pro.utils.CharacterUtil;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.File;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * 书签导出操作
 *
 * @author Nonoas
 * @date 2024-1-31
 */
public final class BookmarkExportAction extends AnAction {

    private GeneralSettings generalSettings;

    public BookmarkExportAction() {
        super("Bookmark Export", null, AllIcons.ToolbarDecorator.Export);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (null == project) {
            return;
        }
        // 项目跟目录
        String projectDir = FileUtil.toSystemIndependentName(Objects.requireNonNull(project.getBasePath()));
        String fileName = project.getName() + ".json";
        InputValidatorEx validatorEx = inputString -> {
            if (CharacterUtil.isBlank(inputString)) return "Save file name is not empty.";
            return null;
        };
        String newFileName = Messages.showInputDialog("name:", "SaveFileName", null, fileName, validatorEx);
        if (CharacterUtil.isNotEmpty(newFileName)) {
            fileName = newFileName;
        }
//        if (BookmarkRunService.getPersistenceService(project).exportBookmark(project, projectDir + File.separator + fileName)) {
//            sendExportNotice(project, projectDir);
//        }
        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();
        // 定义时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日HH小时mm分钟ss秒");
        // 格式化当前时间
        String currentTimeString = now.format(formatter);
        // 创建File对象
        String sjzPath = projectDir + File.separator + "SjzMarkBook";
        File folder = new File(sjzPath);
        // 检查文件夹是否已经存在
        if (!folder.exists()) {
            // 创建文件夹
            boolean created = folder.mkdirs();
        }
        String sjzFileName = project.getName()+currentTimeString + ".json";
        if (BookmarkRunService.getPersistenceService(project).exportBookmark(project,
                sjzPath+ File.separator + sjzFileName)) {
            sendExportNotice(project, sjzPath);
        }
        // 导出到另一个文件夹
        if (!"".equals(BookmarkRunService.getBookmarkSettings().getSjzBeiFen())){
            String sjzPath2 = BookmarkRunService.getBookmarkSettings().getSjzBeiFen() + File.separator + "SjzMarkBook";
            File folder2 = new File(sjzPath2);
            // 检查文件夹是否已经存在
            if (!folder2.exists()) {
                // 创建文件夹
                boolean created = folder2.mkdirs();
            }
            if (BookmarkRunService.getPersistenceService(project).exportBookmark(project,
                    sjzPath2+ File.separator + sjzFileName)) {
                sendExportNotice(project, sjzPath2);
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

        BookmarkNoticeUtil.projectNotice(project, String.format("Export bookmark success.Out file directory:[%s]", projectDir), openExportFile);
    }
}
