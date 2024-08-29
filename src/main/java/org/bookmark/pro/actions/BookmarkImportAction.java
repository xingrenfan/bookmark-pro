package org.bookmark.pro.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import org.bookmark.pro.base.I18N;
import org.bookmark.pro.service.base.persistence.PersistService;
import org.bookmark.pro.service.tree.TreeService;
import org.bookmark.pro.utils.BookmarkNoticeUtil;
import org.bookmark.pro.windows.BookmarkPanel;
import org.jetbrains.annotations.NotNull;

/**
 * 导入书签操作
 *
 * @author Lyon
 * @date 2024/03/21
 */
public final class BookmarkImportAction extends AnAction {
    public BookmarkImportAction() {
        super(I18N.get("import.title"), null, AllIcons.ToolbarDecorator.Import);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        FileChooserDescriptor descriptor = new FileChooserDescriptor(true, false, false, false, false, false);
        // 之筛选JSON文件
        descriptor.withFileFilter(file -> file != null && file.getName().toLowerCase().endsWith(".json"));
        // 获取项目信息
        Project project = e.getProject();
        // 获取文件信息
        VirtualFile virtualFile = FileChooser.chooseFile(descriptor, project, null);
        if (null == virtualFile || null == project) {
            return;
        }
        int result = Messages.showOkCancelDialog(project, "Bookmark Import Override", "Bookmark Import", "Import", "Cancel", Messages.getQuestionIcon());
        if (result == Messages.CANCEL) {
            return;
        }
        if (PersistService.getInstance(project).importBookmark(virtualFile)) {
            // 重新加载标签书
            BookmarkPanel.getInstance(project).reloadBookmarkTree(TreeService.getInstance(project).getBookmarkTree());
            BookmarkNoticeUtil.projectNotice(project, "Bookmark import success.");
        }
    }
}
