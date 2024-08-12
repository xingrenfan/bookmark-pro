package org.bookmark.pro.context;

import com.intellij.openapi.project.Project;
import org.bookmark.pro.windows.BookmarkManagerPanel;
import org.bookmark.pro.service.persistence.PersistenceService;
import org.bookmark.pro.service.persistence.handler.PersistenceServiceHandler;
import org.bookmark.pro.service.tree.BookmarkTreeManage;
import org.bookmark.pro.service.tree.handler.BookmarkTreeManager;

/**
 * 应用运行上下文
 *
 * @author Lyon
 * @date 2024/08/12
 */
public class AppRunContext {
    /**
     * 书签管理器面板
     */
    private BookmarkManagerPanel bookmarkManagerPanel;

    /**
     * 书签树管理器
     */
    private BookmarkTreeManager bookmarkTreeManager;

    /**
     * 持久性服务
     */
    private PersistenceService persistenceService;


    public AppRunContext(Project project) {
        this.bookmarkManagerPanel = new BookmarkManagerPanel(project);
        this.bookmarkTreeManager = new BookmarkTreeManager(project);
        this.persistenceService = new PersistenceServiceHandler(project);
    }

    public BookmarkManagerPanel getManagePanel() {
        return this.bookmarkManagerPanel;
    }

    public BookmarkTreeManage getTreeManage() {
        return this.bookmarkTreeManager;
    }

    public PersistenceService getPersistenceService() {
        return this.persistenceService;
    }
}
