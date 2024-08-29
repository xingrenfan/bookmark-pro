package org.bookmark.pro.service;

import com.intellij.openapi.project.Project;
import org.bookmark.pro.base.I18N;
import org.bookmark.pro.utils.BookmarkNoticeUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ServiceContext {
    /**
     * 公平锁
     */
    private static final Lock FAIR_LOCKS = new ReentrantLock(true);

    private static final Map<Project, AppAttribute> context = new ConcurrentHashMap<>(16);

    public ServiceContext(Project openProject) {
        if (!context.containsKey(openProject)) {
            if (FAIR_LOCKS.tryLock()) {
                if (!context.containsKey(openProject)) {
                    try {
                        this.context.put(openProject, new AppAttribute(openProject));
                    } catch (Exception e) {
                        BookmarkNoticeUtil.errorMessages(openProject, I18N.get("exception.project.initializeFail") + e.getMessage());
                    } finally {
                        FAIR_LOCKS.unlock();
                    }
                }
            }
        }
    }

    public static AppAttribute getContextAttribute(Project openProject) {
        if (context.containsKey(openProject)) {
            return context.get(openProject);
        } else {
            AppAttribute appAttribute = new AppAttribute(openProject);
            context.put(openProject, appAttribute);
            return appAttribute;
        }
    }
}
