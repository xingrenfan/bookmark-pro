package org.bookmark.pro.listeners.app;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationAction;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import org.bookmark.pro.constants.BookmarkConstants;
import org.bookmark.pro.constants.BookmarkIcons;
import org.bookmark.pro.service.ServiceContext;
import org.bookmark.pro.service.base.task.ScheduledService;
import org.bookmark.pro.utils.BookmarkNoticeUtil;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.net.URI;

/**
 * 应用激活侦听器
 *
 * @author Lyon
 * @date 2024/04/23
 */
public class AppActivationService implements StartupActivity {
    @Override
    public void runActivity(@NotNull Project project) {
        new ServiceContext(project);
        // 插件启动通知
        startNotice(project);
        // 启动定时任务
        ScheduledService.getInstance(project).initScheduledService(); // 启动定时备份任务
    }

    /**
     * 启动时通知
     *
     * @param project 项目
     */
    private void startNotice(Project project) {
        // 反馈
        AnAction issueAction = new NotificationAction(BookmarkIcons.ISSUE_SIGN + "Feedback") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e, @NotNull Notification notification) {
                try {
                    Desktop desktop = Desktop.getDesktop();
                    if (desktop.isSupported(Desktop.Action.BROWSE)) {
                        desktop.browse(new URI(BookmarkConstants.BOOKMARK_ISSUES_URI));
                    }
                } catch (Exception ex) {
                }
            }
        };
        // 评价
        AnAction supportSign = new NotificationAction(BookmarkIcons.SUPPORT_SIGN + "Appraise") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e, @NotNull Notification notification) {
                try {
                    Desktop desktop = Desktop.getDesktop();
                    if (desktop.isSupported(Desktop.Action.BROWSE)) {
                        desktop.browse(new URI(BookmarkConstants.IDEA_PLUGIN_URI));
                    }
                } catch (Exception ex) {
                }
            }
        };
        BookmarkNoticeUtil.projectNotice(project, "Thanks for using bookmark pro.", issueAction, supportSign);
    }
}
