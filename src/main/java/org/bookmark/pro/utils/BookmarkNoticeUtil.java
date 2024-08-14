package org.bookmark.pro.utils;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowId;
import org.bookmark.pro.constants.BookmarkIcons;

/**
 * 通知工具类
 *
 * @author Lyon
 * @date 2024/04/02
 */
public class BookmarkNoticeUtil {
    private BookmarkNoticeUtil() {
    }

    /**
     * 项目通知
     *
     * @param project 项目
     * @param message 消息
     */
    public static void projectNotice(Project project, String message, AnAction... actions) {
        String groupId = ToolWindowId.PROJECT_VIEW;
        Notification notification = new Notification(groupId, "Bookmark Notice", BookmarkIcons.BOOKMARK_NOTICE_SIGN + message, NotificationType.INFORMATION);
        notification.setIcon(BookmarkIcons.BOOKMARK_LOGO_ICON);
        if (actions != null) {
            for (AnAction action : actions) {
                notification.addAction(action);
            }
        }
        Notifications.Bus.notify(notification, project);
    }

    /**
     * 警告消息
     *
     * @param project 项目
     * @param message 消息
     * @param actions 行动
     */
    public static void warningMessages(Project project, String message, AnAction... actions) {
        String groupId = ToolWindowId.PROJECT_VIEW;
        Notification notification = new Notification(groupId, "Bookmark Warning", BookmarkIcons.BOOKMARK_WANING_SIGN + message, NotificationType.WARNING);
        notification.setIcon(BookmarkIcons.BOOKMARK_LOGO_ICON);
        if (actions != null) {
            for (AnAction action : actions) {
                notification.addAction(action);
            }
        }
        Notifications.Bus.notify(notification, project);
    }

    /**
     * 异常消息
     *
     * @param project 项目
     * @param message 消息
     * @param actions 行动
     */
    public static void errorMessages(Project project, String message, AnAction... actions) {
        String groupId = ToolWindowId.PROJECT_VIEW;
        Notification notification = new Notification(groupId, "Bookmark Error", BookmarkIcons.WARNING_ERROR_SIGN + message, NotificationType.ERROR);
        notification.setIcon(BookmarkIcons.BOOKMARK_LOGO_ICON);
        if (actions != null) {
            for (AnAction action : actions) {
                notification.addAction(action);
            }
        }
        Notifications.Bus.notify(notification, project);
    }
}
