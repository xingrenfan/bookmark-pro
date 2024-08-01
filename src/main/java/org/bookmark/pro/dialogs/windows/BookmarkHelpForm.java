package org.bookmark.pro.dialogs.windows;

import com.intellij.openapi.ui.DialogWrapper;
import org.bookmark.pro.constants.BookmarkProConstant;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;

public class BookmarkHelpForm extends DialogWrapper {
    private JPanel mainPanel;
    private JLabel readme;

    public BookmarkHelpForm() {
        super(true); // use current window as parent
        setTitle("bookmark Helper");
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        readme.setText("<html><u>Github Readme</u></html>");
        readme.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
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
        });
        return mainPanel;
    }
}
