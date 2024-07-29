package org.bookmark.pro.dialogs.setting;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.NlsContexts;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * 书签设置窗口
 *
 * @author Lyon
 * @date 2024/04/11
 */
public class BookmarkSettingForm implements Configurable {
    private JPanel mainPane;
    private JTextField markText;
    private JLabel markLabSel;
    private JLabel noteLabSel;
    private JLabel separatorSel;
    private JSpinner maxCharNum;
    private JCheckBox lineDocument;
    private JSpinner selectedShowNum;
    private JTextField sjzBeiFen;

    private GeneralSettings generalSettings;

    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return "Bookmark Setting";
    }

    @Override
    public @Nullable JComponent createComponent() {
        // 初始化全局设置
        generalSettings = new GeneralSettings(markText, markLabSel, noteLabSel, separatorSel, maxCharNum, lineDocument, selectedShowNum,sjzBeiFen);
        generalSettings.initGeneralSettings();
        //设置颜色选择监听
        colorChooseListener(mainPane, noteLabSel);
        colorChooseListener(mainPane, markLabSel);
        colorChooseListener(mainPane, separatorSel);
        return this.mainPane;
    }

    @Override
    public boolean isModified() {
        return true;
    }

    @Override
    public void apply() throws ConfigurationException {
        // 保存常规配置
        generalSettings.saveGeneralSetting();
    }

    /**
     * 颜色选择侦听器
     *
     * @param placement 放置
     * @param lc
     */
    private void colorChooseListener(JComponent placement, JComponent lc) {
        lc.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent e) {
                final Color chooseColor = JColorChooser.showDialog(placement, "Color choose", lc.getForeground(), true);
                if (null != chooseColor) {
                    lc.setForeground(chooseColor);
                }
            }
        });
    }
}
