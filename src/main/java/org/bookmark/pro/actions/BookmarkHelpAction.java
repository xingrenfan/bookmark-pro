package org.bookmark.pro.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.bookmark.pro.constants.BookmarkProConstant;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.net.URI;

/**
 * 书签帮助组件
 *
 * @author Lyon
 * @date 2024/03/21
 */
public class BookmarkHelpAction extends AnAction {

    public BookmarkHelpAction() {
        super("Bookmark Help", null, AllIcons.Actions.Help);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        try {
            URI uri = new URI(BookmarkProConstant.BOOKMARK_README_URI);
            Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
            if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
                desktop.browse(uri);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

}
