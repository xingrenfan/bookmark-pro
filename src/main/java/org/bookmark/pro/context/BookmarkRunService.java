package org.bookmark.pro.context;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.project.Project;
import org.bookmark.pro.dialogs.BookmarkManagerPanel;
import org.bookmark.pro.service.document.DocumentService;
import org.bookmark.pro.service.document.handler.DocumentServiceHandler;
import org.bookmark.pro.service.persistence.PersistenceService;
import org.bookmark.pro.service.persistence.handler.PersistenceServiceHandler;
import org.bookmark.pro.service.persistence.settings.BookmarkSettings;
import org.bookmark.pro.service.queue.ScheduledService;
import org.bookmark.pro.service.queue.handler.ScheduledServiceHandler;
import org.bookmark.pro.service.tree.BookmarkTreeManage;
import org.bookmark.pro.service.tree.handler.BookmarkTreeManager;
import org.bookmark.pro.utils.BookmarkNoticeUtil;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 书签运行上下文
 *
 * @author Lyon
 * @date 2024/03/27
 */
public class BookmarkRunService {
    /**
     * 公平锁
     */
    private final static Lock FAIR_LOCKS = new ReentrantLock(true);

    /**
     * 书签全局设置
     */
    private static BookmarkSettings settings;

    /**
     * 书签管理器面板
     */
    private static BookmarkManagerPanel bookmarkManagerPanel;

    /**
     * 书签树管理
     */
    private static BookmarkTreeManage bookmarkTreeManage;

    /**
     * 持久性服务
     */
    private static PersistenceService persistenceService;

    /**
     * 获取书签设置
     *
     * @return {@link BookmarkSettings}
     */
    public static BookmarkSettings getBookmarkSettings() {
        if (settings == null) {
            settings = new BookmarkSettings(PropertiesComponent.getInstance());
        }
        return settings;
    }

    private static <T> T getInstance(Project project, Class<T> serviceType) {
        T service = project.getService(serviceType);
        if (service == null) {
            throw new IllegalStateException("Service " + serviceType.getName() + " not found");
        }
        return service;
    }

    /**
     * 获取书签管理器面板
     *
     * @param project 项目
     * @return {@link BookmarkManagerPanel}
     */
    public static BookmarkManagerPanel getBookmarkManagerPanel(Project project) {
        if (bookmarkManagerPanel == null || !project.equals(bookmarkManagerPanel.getOpenProject())) {
            try {
                if (FAIR_LOCKS.tryLock()) {
                    bookmarkManagerPanel = new BookmarkManagerPanel(project);
                }
            } catch (Exception e) {
                BookmarkNoticeUtil.errorMessages(project, "Bookmark tree manager panel initialization failed. message:" + e.getMessage());
            } finally {
                FAIR_LOCKS.unlock();
            }
        }
        return bookmarkManagerPanel;
    }

    /**
     * 获取书签树管理
     *
     * @param project 项目
     * @return {@link BookmarkTreeManage}
     */
    public static BookmarkTreeManage getBookmarkManage(Project project) {
        if (bookmarkTreeManage == null || !project.equals(bookmarkTreeManage.getOpenProject())) {
            try {
                if (FAIR_LOCKS.tryLock()) {
                    bookmarkTreeManage = new BookmarkTreeManager(project);
                }
            } catch (Exception e) {
                BookmarkNoticeUtil.errorMessages(project, "Bookmark tree manager initialization failed. message:" + e.getMessage());
            } finally {
                FAIR_LOCKS.unlock();
            }
        }
        return bookmarkTreeManage;
    }

    /**
     * 获取持久性服务
     *
     * @param project 项目
     * @return {@link PersistenceService}
     */
    public static PersistenceService getPersistenceService(Project project) {
        if (persistenceService == null || !project.equals(persistenceService.getOpenProject())) {
            try {
                if (FAIR_LOCKS.tryLock()) {
                    persistenceService = new PersistenceServiceHandler(project);
                }
            } catch (Exception e) {
                BookmarkNoticeUtil.errorMessages(project, "Bookmark persistence service initialization failed. message:" + e.getMessage());
            } finally {
                FAIR_LOCKS.unlock();
            }
        }
        return persistenceService;
    }

    /**
     * 获取文档服务
     *
     * @return {@link DocumentService}
     */
    public static DocumentService getDocumentService(Project project) {
        return getInstance(project, DocumentServiceHandler.class);
    }

    public static ScheduledService getScheduledService(Project project) {
        return getInstance(project, ScheduledServiceHandler.class);
    }
}
