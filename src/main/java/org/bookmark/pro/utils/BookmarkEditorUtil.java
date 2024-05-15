package org.bookmark.pro.utils;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;

import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

/**
 * 编辑器 工具
 *
 * @author Lyon
 * @date 2024/04/26
 */
public class BookmarkEditorUtil {
    private BookmarkEditorUtil() {
    }

    /**
     * 自动获取标记行的描述
     *
     * @param editor    编辑器
     * @param lineIndex 行索引
     * @return {@link String}
     */
    public static String getAutoDescription(final Editor editor, final int lineIndex) {
        String autoDescription = editor.getSelectionModel().getSelectedText();
        if (autoDescription == null) {
            Document document = editor.getDocument();
            autoDescription = document.getCharsSequence().subSequence(document.getLineStartOffset(lineIndex), document.getLineEndOffset(lineIndex)).toString().trim();
        }
        return autoDescription;
    }

    /**
     * 添加数字格式化程序
     *
     * @param numberSpinner 数字微调器
     * @param model         型
     */
    public static void addNumberFormatter(JSpinner numberSpinner, SpinnerModel model) {
        numberSpinner.setModel(model);
        // 允许输入
        final JSpinner.NumberEditor editor = new JSpinner.NumberEditor(numberSpinner, "0");
        numberSpinner.setEditor(editor);
        final JFormattedTextField textField = ((JSpinner.NumberEditor) numberSpinner.getEditor()).getTextField();
        textField.setEditable(true);//开启输入功能
        // 开启输入的值的限制
        final DefaultFormatterFactory factory = (DefaultFormatterFactory) textField.getFormatterFactory();
        final NumberFormatter formatter = (NumberFormatter) factory.getDefaultFormatter();
        // 此处对输入的有效性进行控制。若改为true，则不控制有效性
        formatter.setAllowsInvalid(false);
    }
}
