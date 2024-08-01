package org.bookmark.pro.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.DialogWrapper;
import org.bookmark.pro.dialogs.windows.BookmarkHelpForm;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

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
/*
        DialogWrapper helpDialog = new DialogWrapper(true) {
            @Override
            protected @Nullable JComponent createCenterPanel() {
                JPanel dialogPanel = new JPanel(new BorderLayout());
                JLabel addEdit = new JLabel("Add/Edit bookmark: Alt + shift + A");
                addEdit.setPreferredSize(new Dimension(300, 100));
                JLabel delete = new JLabel("Delete bookmark: Alt + shift + D");
                delete.setPreferredSize(new Dimension(300, 100));
                JLabel switchBookmark = new JLabel("Switch bookmark: Alt + shift + left/right keyboard");
                switchBookmark.setPreferredSize(new Dimension(300, 100));
                dialogPanel.add(addEdit, BorderLayout.CENTER);
                dialogPanel.add(delete, BorderLayout.CENTER);
                dialogPanel.add(switchBookmark, BorderLayout.CENTER);
                return dialogPanel;
            }
        };
        helpDialog.setTitle("BookmarkPro Using Help");
        helpDialog.show();*/
    }
}
