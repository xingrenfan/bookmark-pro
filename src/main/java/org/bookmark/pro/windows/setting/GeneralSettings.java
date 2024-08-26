package org.bookmark.pro.windows.setting;

import org.apache.commons.lang3.StringUtils;
import org.bookmark.pro.base.I18N;
import org.bookmark.pro.constants.BookmarkConstants;
import org.bookmark.pro.service.base.settings.GlobalSettings;
import org.bookmark.pro.utils.BookmarkEditorUtil;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.Objects;

public class GeneralSettings {
    protected JTextField markText;
    protected JLabel markLabSel;
    protected JLabel noteLabSel;
    protected JLabel separatorSel;
    protected JSpinner maxCharNum;
    private JSpinner selectedShowNum;
    protected JCheckBox lineDocument;

    private JLabel selectTips;
    private JComboBox selectTipBox;

    public GeneralSettings(JTextField markText, JLabel markLabSel, JLabel noteLabSel, JLabel separatorSel, JSpinner maxCharNum, JCheckBox lineDocument, JSpinner selectedShowNum, JLabel selectTips, JComboBox selectTipBox) {
        this.markText = markText;
        this.markLabSel = markLabSel;
        this.noteLabSel = noteLabSel;
        this.separatorSel = separatorSel;
        this.selectedShowNum = selectedShowNum;
        this.maxCharNum = maxCharNum;
        this.lineDocument = lineDocument;
        this.selectTips = selectTips;
        this.selectTipBox = selectTipBox;
    }

    protected void initGeneralSettings() {
        GlobalSettings globalSettings = GlobalSettings.getInstance();

        // 设置是否启用行尾拓展
        lineEndDocument(globalSettings);
        // 前缀
        markLabSel.setForeground(globalSettings.getPrefixColor());
        markLabSel.setText(globalSettings.getPrefix());
        // 分隔符
        if (StringUtils.isBlank(globalSettings.getPrefix())) {
            separatorSel.setText(" ");
        } else {
            separatorSel.setText(" : ");
        }
        separatorSel.setForeground(globalSettings.getSeparatorColor());
        // 内容
        noteLabSel.setForeground(globalSettings.getContentColor());
        noteLabSel.setText(BookmarkConstants.APP_NAME_STR);
        // 前缀
        markText.setText(globalSettings.getPrefix());
        // 行尾最大显示字符数
        SpinnerModel lineEndModel = new SpinnerNumberModel(globalSettings.getShowNum().intValue(), 10, 1000, 5);
        BookmarkEditorUtil.addNumberFormatter(maxCharNum, lineEndModel);
        // 选中面板显示最大字符数
        SpinnerModel treeSelectedModel = new SpinnerNumberModel(globalSettings.getTreePanelShowNum().intValue(), 10, 1000, 10);
        BookmarkEditorUtil.addNumberFormatter(selectedShowNum, treeSelectedModel);
        if (StringUtils.isNotBlank(globalSettings.getTipType())) {
            selectTipBox.setSelectedItem(globalSettings.getTipType());
        }

        // 输入框内容变更监听
        markText.getDocument().addDocumentListener(new DocumentListener() {
            void updatePreview() {
                // 分隔符
                String prefix = markText.getText();
                if (StringUtils.isEmpty(prefix)) {
                    separatorSel.setText(" ");
                } else {
                    separatorSel.setText(" : ");
                }
                markLabSel.setText(prefix);
            }

            @Override
            public void insertUpdate(final DocumentEvent e) {
                updatePreview();
            }

            @Override
            public void removeUpdate(final DocumentEvent e) {
                updatePreview();
            }

            @Override
            public void changedUpdate(final DocumentEvent e) {
                updatePreview();
            }
        });
    }

    private void initDefaultValue() {
        selectTips.setText(I18N.get("setting.general.tipLabel"));
        selectTipBox.addItem(I18N.get("setting.general.tipItem1"));
        selectTipBox.addItem(I18N.get("setting.general.tipItem2"));
    }

    /**
     * 行尾文档添加
     */
    private void lineEndDocument(GlobalSettings globalSettings) {
        lineDocument.addChangeListener(e -> {
            if (lineDocument.isSelected()) {
                enableLabel();
            } else {
                notEnableLabel();
            }
        });
        lineDocument.setSelected(globalSettings.getLineDocument());
        if (lineDocument.isSelected()) {
            enableLabel();
        }
    }

    private void notEnableLabel() {
        markText.setEnabled(false);
        markLabSel.setEnabled(false);
        noteLabSel.setEnabled(false);
        separatorSel.setEnabled(false);
        maxCharNum.setEnabled(false);
    }

    private void enableLabel() {
        markText.setEnabled(true);
        markLabSel.setEnabled(true);
        noteLabSel.setEnabled(true);
        separatorSel.setEnabled(true);
        maxCharNum.setEnabled(true);
    }

    protected void saveGeneralSetting() {
        GlobalSettings globalSettings = GlobalSettings.getInstance();

        // 前缀颜色
        globalSettings.setPrefixColor(markLabSel.getForeground());
        // 内容颜色
        globalSettings.setContentColor(noteLabSel.getForeground());
        // 分隔符颜色
        globalSettings.setSeparatorColor(separatorSel.getForeground());
        // 行尾拓展 最大字符数
        globalSettings.setShowNum(Objects.toString(maxCharNum.getValue(), null));
        // 书签树选中简介显示最大字符
        globalSettings.setTreePanelShowNum(Objects.toString(selectedShowNum.getValue(), null));
        // 前缀
        globalSettings.setPrefix(markText.getText());
        // 行尾拓展器：在行尾显示书签内容
        globalSettings.setLineDocument(lineDocument.isSelected());
        // 选中提示样式
        globalSettings.setTipType(Objects.toString(selectTipBox.getSelectedItem()));
    }
}
