package org.bookmark.pro.context;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.project.Project;
import org.bookmark.pro.service.document.DocumentService;
import org.bookmark.pro.service.document.handler.DocumentServiceHandler;
import org.bookmark.pro.service.settings.BookmarkSettings;
import org.bookmark.pro.service.task.ScheduledService;
import org.bookmark.pro.service.task.handler.ScheduledServiceHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 应用项目管理
 *
 * @author Lyon
 * @date 2024/08/12
 */
public class AppProjectManage {
    /**
     * 项目管理
     */
    private static Map<Project, AppRunContext> projectManage = new ConcurrentHashMap<>();

    /**
     * 书签设置
     */
    private static BookmarkSettings bookmarkSettings;


    public static BookmarkSettings getBookmarkSettings() {
        if (bookmarkSettings == null) {
            bookmarkSettings = new BookmarkSettings(PropertiesComponent.getInstance());
        }
        return bookmarkSettings;
    }

    public static AppRunContext getRunContext(Project project) {
        if (projectManage.containsKey(project)) {
            return projectManage.get(project);
        }
        AppRunContext runContext = new AppRunContext(project);
        projectManage.put(project, runContext);
        return runContext;
    }

    public static <T> T getInstance(Project project, Class<T> serviceType) {
        T service = project.getService(serviceType);
        if (service == null) {
            throw new IllegalStateException("Service " + serviceType.getName() + " not found");
        }
        return service;
    }

    public static DocumentService getDocumentService(Project project) {
        return getInstance(project, DocumentServiceHandler.class);
    }

    public static ScheduledService getScheduledService(Project project) {
        return getInstance(project, ScheduledServiceHandler.class);
    }
}
