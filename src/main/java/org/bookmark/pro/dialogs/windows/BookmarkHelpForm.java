package org.bookmark.pro.dialogs.windows;

import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class BookmarkHelpForm extends DialogWrapper {
    private JPanel mainPanel;
    private JTabbedPane tabbedPane1;

    public BookmarkHelpForm() {
        super(true); // use current window as parent
        setTitle("bookmark Helper");
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return mainPanel;
    }
}
