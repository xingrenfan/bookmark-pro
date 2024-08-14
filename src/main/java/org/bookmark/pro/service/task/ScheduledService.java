package org.bookmark.pro.service.task;

import com.intellij.openapi.application.ApplicationManager;
import org.bookmark.pro.service.task.handler.ScheduledServiceImpl;

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

    static ScheduledService getInstance() {
        return ApplicationManager.getApplication().getService(ScheduledServiceImpl.class);
    }
}
