package org.bookmark.pro.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.bookmark.pro.base.I18N;
import org.bookmark.pro.constants.BookmarkConstants;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.net.URI;

/**
 * 问题反馈组件
 *
 * @author Lyon
 * @date 2024/03/21
 */
public class BookmarkIssueAction extends AnAction {

    public BookmarkIssueAction() {
        super(I18N.get("issue.title"), null, AllIcons.Actions.QuickfixBulb);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        try {
            URI uri = new URI(BookmarkConstants.BOOKMARK_ISSUES_URI);
            Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
            if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
                desktop.browse(uri);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

}
