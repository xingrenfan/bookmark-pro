package org.bookmark.pro.context;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ComponentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;

/**
 * 应用运行上下文
 *
 * @author Lyon
 * @date 2024/08/12
 */
public class AppRunContext {
    public static <T> T getServiceImpl(Project project, Class<T> serviceType) {
        T service = project.getService(serviceType);
        if (service == null) {
            throw new IllegalStateException("Service " + serviceType.getName() + " not found");
        }
        return service;
    }

    public static <T> T getAppService(Class<?> serviceType) {
        return ComponentManager.getService(serviceType);

        return getServiceImpl(ProjectManager.getInstance().getDefaultProject(), serviceType);
    }
}
