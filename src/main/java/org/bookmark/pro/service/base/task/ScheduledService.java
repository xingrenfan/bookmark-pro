package org.bookmark.pro.service.base.task;

import com.intellij.openapi.project.Project;
import org.bookmark.pro.service.ServiceContext;

/**
 * 书签工程定时服务
 *
 * @author Lyon
 * @date 2024/04/24
 */
public interface ScheduledService {
    /**
     * 初始化定时任务
     */
    void initScheduledService();

    /**
     * 关闭
     */
    void shutdown();

    static ScheduledService getInstance(Project project) {
        return ServiceContext.getContextAttribute(project).getScheduledService();
    }
}
