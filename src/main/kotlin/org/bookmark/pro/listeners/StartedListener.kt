package org.bookmark.pro.listeners

import com.intellij.notification.Notification
import com.intellij.notification.NotificationAction
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import org.bookmark.pro.base.I18N
import org.bookmark.pro.constants.BookmarkConstants
import org.bookmark.pro.constants.BookmarkIcons
import org.bookmark.pro.service.ServiceContext
import org.bookmark.pro.service.base.task.ScheduledService
import org.bookmark.pro.utils.BookmarkNoticeUtil
import java.awt.Desktop
import java.net.URI

class StartedListener : ProjectActivity {
    override suspend fun execute(project: Project) {
        // 初始化服务上下文
        ServiceContext(project)
        // 插件启动通知
        startNotice(project)
        // 启动定时备份任务
        ScheduledService.getInstance(project).initScheduledService()
    }

    /**
     * 启动时通知
     *
     * @param project 项目
     */
    private fun startNotice(project: Project) {
        // 反馈
        val issueAction: AnAction = object : NotificationAction(BookmarkIcons.ISSUE_SIGN + I18N.get("start.notice.feedback")) {
            override fun actionPerformed(e: AnActionEvent, notification: Notification) {
                try {
                    val desktop = Desktop.getDesktop()
                    if (desktop.isSupported(Desktop.Action.BROWSE)) {
                        desktop.browse(URI(BookmarkConstants.BOOKMARK_ISSUES_URI))
                    }
                } catch (ex: Exception) {
                }
            }
        }
        // 评价
        val supportSign: AnAction = object : NotificationAction(BookmarkIcons.SUPPORT_SIGN + I18N.get("start.notice.appraise")) {
            override fun actionPerformed(e: AnActionEvent, notification: Notification) {
                try {
                    val desktop = Desktop.getDesktop()
                    if (desktop.isSupported(Desktop.Action.BROWSE)) {
                        desktop.browse(URI(BookmarkConstants.IDEA_PLUGIN_URI))
                    }
                } catch (ex: Exception) {
                }
            }
        }
        BookmarkNoticeUtil.projectNotice(project, I18N.get("start.notice.message"), issueAction, supportSign)
    }
}