package org.bookmark.pro.service.task;

/**
 * 书签工程定时服务
 *
 * @author Lyon
 * @date 2024/04/24
 */
public interface ScheduledService {
    /**
     * 执行延迟任务
     *
     * @param delay 延迟
     */
    void inspectionFileBookmark();

    /**
     * 关闭
     */
    void shutdown();
}
