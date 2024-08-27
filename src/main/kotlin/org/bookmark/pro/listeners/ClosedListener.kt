package org.bookmark.pro.listeners

import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManagerListener
import org.bookmark.pro.service.base.persistence.PersistService
import org.bookmark.pro.service.base.task.ScheduledService
import org.bookmark.pro.service.tree.TreeService

class ClosedListener : ProjectManagerListener {
    override fun projectClosing(project: Project) {
        // 获取书签树
        val bookmarkTree = TreeService.getInstance(project).bookmarkTree
        // 处理项目关闭事件
        PersistService.getInstance(project).saveBookmark(bookmarkTree)
        // 关闭线程池
        ScheduledService.getInstance(project).shutdown()
    }
}