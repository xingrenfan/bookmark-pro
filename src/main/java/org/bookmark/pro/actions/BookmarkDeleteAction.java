package org.bookmark.pro.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import org.bookmark.pro.context.BookmarkRunService;
import org.bookmark.pro.service.tree.handler.BookmarkTreeNode;
import org.bookmark.pro.utils.BookmarkNoticeUtil;
import org.bookmark.pro.utils.BookmarkUtil;
import org.jetbrains.annotations.NotNull;

/**
 * 书签删除操作
 *
 * @author Lyon
 * @date 2024/04/20
 */
public class BookmarkDeleteAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        VirtualFile file = e.getData(CommonDataKeys.VIRTUAL_FILE);
        if (project == null || editor == null || file == null) return;
        int result = Messages.showOkCancelDialog(project, "Delete Selected Bookmark", "Delete Bookmark", "Delete", "Cancel", Messages.getQuestionIcon());
        if (result == Messages.CANCEL) {
            return;
        }
        // 创建或编辑书签组件
        CaretModel caretModel = editor.getCaretModel();
        // 获取添加标记的行号
        int line = caretModel.getLogicalPosition().line;
        BookmarkTreeNode bookmarkNode = BookmarkRunService.getDocumentService(project).getBookmarkNode(project, file, line);
        if (bookmarkNode != null) {
            BookmarkRunService.getBookmarkManage(project).removeBookmarkNode(bookmarkNode);
        }
        BookmarkNoticeUtil.warningMessages(project, "This line not found bookmark");
    }
}
