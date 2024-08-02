package org.bookmark.pro.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.bookmark.pro.dialogs.windows.BookmarkHelpForm;
import org.jetbrains.annotations.NotNull;

/**
 * 书签帮助组件
 *
 * @author Lyon
 * @date 2024/03/21
 */
public final class BookmarkHelpAction extends AnAction {
    public BookmarkHelpAction() {
        super("Bookmark Using Help", null, AllIcons.Actions.Help);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        BookmarkHelpForm helpForm = new BookmarkHelpForm();
        helpForm.show();
    }
}
