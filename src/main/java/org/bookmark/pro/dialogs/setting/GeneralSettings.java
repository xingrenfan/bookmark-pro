package org.bookmark.pro.dialogs.setting;

import org.bookmark.pro.context.BookmarkRunService;
import org.bookmark.pro.utils.BookmarkEditorUtil;
import org.bookmark.pro.utils.CharacterUtil;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.Objects;

public class GeneralSettings {
    protected JTextField markText;
    protected JTextField sjzBeiFen;
    protected JLabel markLabSel;
    protected JLabel noteLabSel;
    protected JLabel separatorSel;
    protected JSpinner maxCharNum;

    private JSpinner selectedShowNum;

    protected JCheckBox lineDocument;

    public GeneralSettings(JTextField markText, JLabel markLabSel, JLabel noteLabSel,
                           JLabel separatorSel, JSpinner maxCharNum, JCheckBox lineDocument, JSpinner selectedShowNum,JTextField  sjzBeiFen) {
        this.markText = markText;
        this.sjzBeiFen=sjzBeiFen;
        this.markLabSel = markLabSel;
        this.noteLabSel = noteLabSel;
        this.separatorSel = separatorSel;
        this.selectedShowNum = selectedShowNum;
        this.maxCharNum = maxCharNum;
        this.lineDocument = lineDocument;
    }

    protected void initGeneralSettings() {
        // 设置是否启用行尾拓展
        lineEndDocument();
        // 前缀
        markLabSel.setForeground(BookmarkRunService.getBookmarkSettings().getPrefixColor());
        String bookmarkPrefix = BookmarkRunService.getBookmarkSettings().getPrefix();
        markLabSel.setText(bookmarkPrefix);
        // 分隔符
        if (CharacterUtil.isEmpty(bookmarkPrefix)) {
            separatorSel.setText(" ");
        } else {
            separatorSel.setText(" : ");
        }
        separatorSel.setForeground(BookmarkRunService.getBookmarkSettings().getSeparatorColor());
        // 内容
        noteLabSel.setForeground(BookmarkRunService.getBookmarkSettings().getContentColor());
        noteLabSel.setText(BookmarkRunService.getBookmarkSettings().getContent());
        // 前缀
        markText.setText(BookmarkRunService.getBookmarkSettings().getPrefix());
        // 行尾最大显示字符数
        SpinnerModel lineEndModel = new SpinnerNumberModel(BookmarkRunService.getBookmarkSettings().getShowNum().intValue(), 10, 1000, 5);
        BookmarkEditorUtil.addNumberFormatter(maxCharNum, lineEndModel);
        // 选中面板显示最大字符数
        SpinnerModel treeSelectedModel = new SpinnerNumberModel(BookmarkRunService.getBookmarkSettings().getTreePanelShowNum().intValue(), 10, 1000, 10);
        BookmarkEditorUtil.addNumberFormatter(selectedShowNum, treeSelectedModel);
        // 输入框内容变更监听
        markText.getDocument().addDocumentListener(new DocumentListener() {
            void updatePreview() {
                // 分隔符
                String prefix = markText.getText();
                if (CharacterUtil.isEmpty(prefix)) {
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

    /**
     * 行尾文档添加
     */
    private void lineEndDocument() {
        lineDocument.addChangeListener(e -> {
            if (lineDocument.isSelected()) {
                enableLabel();
            } else {
                notEnableLabel();
            }
        });
        lineDocument.setSelected(BookmarkRunService.getBookmarkSettings().getLineDocument());
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
        // 前缀颜色
        BookmarkRunService.getBookmarkSettings().setPrefixColor(markLabSel.getForeground());
        // 内容颜色
        BookmarkRunService.getBookmarkSettings().setContentColor(noteLabSel.getForeground());
        // 分隔符颜色
        BookmarkRunService.getBookmarkSettings().setSeparatorColor(separatorSel.getForeground());
        // 行尾拓展 最大字符数
        BookmarkRunService.getBookmarkSettings().setShowNum(Objects.toString(maxCharNum.getValue(), null));
        // 书签树选中简介显示最大字符
        BookmarkRunService.getBookmarkSettings().setTreePanelShowNum(Objects.toString(selectedShowNum.getValue(), null));
        // 前缀
        BookmarkRunService.getBookmarkSettings().setPrefix(markText.getText());
        // 行尾拓展器：在行尾显示书签内容
        BookmarkRunService.getBookmarkSettings().setLineDocument(lineDocument.isSelected());
        // 备份路径
        BookmarkRunService.getBookmarkSettings().setSjzBeiFen(sjzBeiFen.getText());
    }
}
