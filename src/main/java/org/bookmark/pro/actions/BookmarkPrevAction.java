package org.bookmark.pro.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.bookmark.pro.service.tree.TreeService;
import org.jetbrains.annotations.NotNull;

/**
 * 上一个书签操作
 *
 * @author Lyon
 * @date 2024/03/21
 */
public class BookmarkPrevAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (null == project) {
            return;
        }
        // 上一个书签
        TreeService.getInstance(project).preBookmark();
    }
}
