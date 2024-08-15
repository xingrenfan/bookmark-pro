package org.bookmark.pro.context;

import com.intellij.openapi.project.Project;
import org.bookmark.pro.windows.BookmarkPanel;
import org.bookmark.pro.service.document.DocumentService;
import org.bookmark.pro.service.persistence.PersistService;
import org.bookmark.pro.service.settings.BookmarkSettings;
import org.bookmark.pro.service.tree.TreeService;
import org.bookmark.pro.service.tree.handler.TreeServiceImpl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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
    private static final Map<Project, BookmarkPanel> bookmarkManagerPanelMap = new ConcurrentHashMap<>();

    /**
     * 书签树管理
     */
    private static final Map<Project, TreeServiceImpl> bookmarkTreeManagerMap = new ConcurrentHashMap<>();
/*
    *//**
     * 获取书签设置
     *
     * @return {@link BookmarkSettings}
     *//*
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

    *//**
     * 获取书签管理器面板
     *
     * @param project 项目
     * @return {@link BookmarkPanel}
     *//*
    public static BookmarkPanel getBookmarkManagerPanel(Project project) {
        if (! bookmarkManagerPanelMap.containsKey(project)) {
            if (FAIR_LOCKS.tryLock()) {
                try {
                    bookmarkManagerPanelMap.put(project, new BookmarkPanel(project));
                } catch (Exception e) {
                    BookmarkNoticeUtil.errorMessages(project, "Bookmark tree manager panel initialization failed. message:" + e.getMessage());
                } finally {
                    FAIR_LOCKS.unlock();
                }
            }
        }
        return bookmarkManagerPanelMap.getOrDefault(project, null);
    }

    *//**
     * 获取书签树管理
     *
     * @param project 项目
     * @return {@link TreeService}
     *//*
    public static BookmarkTreeManage getBookmarkManage(Project project) {
        if (!bookmarkTreeManagerMap.containsKey(project)) {
            if (FAIR_LOCKS.tryLock()) {
                try {
                    bookmarkTreeManagerMap.put(project, new BookmarkTreeManager(project));
                } catch (Exception e) {
                    BookmarkNoticeUtil.errorMessages(project, "Bookmark tree manager initialization failed. message:" + e.getMessage());
                } finally {
                    FAIR_LOCKS.unlock();
                }
            }
        }
        return bookmarkTreeManagerMap.getOrDefault(project, null);
    }

    *//**
     * 获取持久性服务
     *
     * @param project 项目
     * @return {@link PersistService}
     *//*
    public static PersistService getPersistenceService(Project project) {
        if (! persistenceServiceMap.containsKey(project)) {
            if (FAIR_LOCKS.tryLock()) {
                try {
                    persistenceServiceMap.put(project, new PersistServiceImpl(project));
                } catch (Exception e) {
                    BookmarkNoticeUtil.errorMessages(project, "Bookmark persistence service initialization failed. message:" + e.getMessage());
                } finally {
                    FAIR_LOCKS.unlock();
                }
            }
        }
        return persistenceServiceMap.getOrDefault(project, null);
    }

    *//**
     * 获取文档服务
     *
     * @return {@link DocumentService}
     *//*
    public static DocumentService getDocumentService(Project project) {
        return getInstance(project, DocumentServiceImpl.class);
    }

    public static ScheduledService getScheduledService(Project project) {
        return getInstance(project, ScheduledServiceImpl.class);
    }*/
}
