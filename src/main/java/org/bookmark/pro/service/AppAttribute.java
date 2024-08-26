package org.bookmark.pro.service;

import com.intellij.openapi.project.Project;
import org.bookmark.pro.service.base.document.DocumentService;
import org.bookmark.pro.service.base.document.handler.DocumentServiceImpl;
import org.bookmark.pro.service.base.persistence.PersistService;
import org.bookmark.pro.service.base.persistence.handler.PersistServiceImpl;
import org.bookmark.pro.service.base.task.ScheduledService;
import org.bookmark.pro.service.base.task.handler.ScheduledServiceImpl;
import org.bookmark.pro.service.tree.TreeService;
import org.bookmark.pro.service.tree.component.BookmarkMenus;
import org.bookmark.pro.service.tree.component.BookmarkTree;
import org.bookmark.pro.service.tree.handler.TreeServiceImpl;
import org.bookmark.pro.windows.BookmarkPanel;

/**
 * 应用
 *
 * @author Lyon
 * @date 2024/08/20
 */
public class AppAttribute {
    private Project openProject;

    private BookmarkTree bookmarkTree;

    private BookmarkMenus bookmarkMenus;

    private BookmarkPanel bookmarkPanel;

    private TreeService treeService;

    private PersistService persistService;

    private DocumentService documentService;

    private ScheduledService scheduledService;

    public AppAttribute(Project project) {
        if (this.openProject != null) {
            return;
        }
        this.openProject = project;
        this.scheduledService = new ScheduledServiceImpl(project);
        this.bookmarkMenus = new BookmarkMenus(project);
        this.persistService = new PersistServiceImpl(project);
        this.documentService = new DocumentServiceImpl(project);
        this.bookmarkTree = new BookmarkTree(project);
    }

    public BookmarkTree getBookmarkTree() {
        return bookmarkTree;
    }

    public BookmarkMenus getBookmarkMenus() {
        return bookmarkMenus;
    }

    public BookmarkPanel getBookmarkPanel() {
        if (this.bookmarkPanel == null) {
            this.bookmarkPanel = new BookmarkPanel(openProject);
        }
        return bookmarkPanel;
    }

    public TreeService getTreeService() {
        if (this.treeService == null) {
            this.treeService = new TreeServiceImpl(this.openProject);
        }
        return treeService;
    }

    public PersistService getPersistService() {
        return persistService;
    }

    public DocumentService getDocumentService() {
        return documentService;
    }

    public ScheduledService getScheduledService() {
        return scheduledService;
    }
}
