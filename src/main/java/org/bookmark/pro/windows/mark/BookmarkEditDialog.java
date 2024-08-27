package org.bookmark.pro.windows.mark;

import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComponentValidator;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBDimension;
import com.intellij.util.ui.JBUI;
import org.bookmark.pro.base.I18N;
import org.bookmark.pro.domain.model.BookmarkNodeModel;
import org.bookmark.pro.service.tree.component.BookmarkTreeNode;
import org.bookmark.pro.utils.BookmarkNoticeUtil;
import org.bookmark.pro.windows.mark.handler.BookmarkCreateHandler;
import org.bookmark.pro.windows.mark.handler.BookmarkUpdateHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

/**
 * 书签编辑对话框
 *
 * @author Nonoas
 * @date 2023/6/1
 */
public class BookmarkEditDialog extends DialogWrapper {
    // 编辑名称
    private final EditorTextField tfName = new EditorTextField();

    // 描述
    private final EditorTextField tfDesc = new EditorTextField();

    // 书签标记行
    private final JSpinner bookmarkLineNum;

    // 创建容器面板
    JPanel panel = new JPanel(new GridBagLayout());

    // 创建 GridBagConstraints 对象
    GridBagConstraints constraints = new GridBagConstraints();

    // 完成按钮定义
    private OnOKAction oKAction;

    /**
     * 书签编辑服务
     */
    private BookmarkEditService bookmarkEditService;

    private Project openProject;

    public BookmarkEditDialog(Project openProject, boolean createBookmark) {
        super(true);
        this.openProject = openProject;
        this.bookmarkLineNum = new JSpinner();
        if (createBookmark) {
            setTitle(I18N.get("bookmark.windows.create"));
            this.bookmarkEditService = new BookmarkCreateHandler(openProject);
        } else {
            setTitle(I18N.get("bookmark.windows.edit"));
            this.bookmarkEditService = new BookmarkUpdateHandler(openProject);
        }
        init();
    }

    @Override
    protected void init() {
        super.init();
    }

    /**
     * 默认节点
     *
     * @param nodeModel       节点模型
     * @param documentMaxLine 文档最大行数
     * @return {@link BookmarkEditDialog}
     */
    public BookmarkEditDialog defaultNode(BookmarkNodeModel nodeModel, Integer documentMaxLine, boolean showMaxLine) {
        this.tfName.setText(nodeModel.getName());
        this.tfDesc.setText(nodeModel.getDesc());
        // 显示行数最大限制
        this.bookmarkEditService.lineNumInspect(panel, constraints, this.bookmarkLineNum, nodeModel.getLine() + 1, documentMaxLine, showMaxLine);
        // 显示书签是否可以做分组
        this.bookmarkEditService.showBookmarkEnable(panel, constraints, nodeModel);
        // 显示书签父级菜单
        this.bookmarkEditService.showBookmarkParent(openProject, panel, constraints, nodeModel);
        return this;
    }

    public BookmarkEditDialog defaultWarning(String message) {
        this.bookmarkEditService.setLapseWarning(panel, constraints, message);
        return this;
    }

    @Override
    protected JComponent createCenterPanel() {
        tfName.setPlaceholder(I18N.get("bookmark.windows.input.place.text"));
        tfDesc.setPlaceholder(I18N.get("bookmark.windows.input.place.desc"));
        tfDesc.setOneLineMode(false);
        tfDesc.setBorder(JBUI.Borders.empty());
        // 设置必输项检查
        setInputInspect(openProject, tfName);

        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets = JBUI.insets(5);

        // 第一行第一列
        JLabel lbName = new JLabel(I18N.get("bookmark.windows.name"));
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 0;
        constraints.weighty = 0;
        panel.add(lbName, constraints);

        // 第一行第二列
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.weightx = 1;
        constraints.weighty = 0;
        panel.add(tfName, constraints);

        // 第二行第一列
        JLabel lbDesc = new JLabel(I18N.get("bookmark.windows.desc"));
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.weightx = 0;
        constraints.weighty = 1;
        panel.add(lbDesc, constraints);

        // 第二行第二列
        JBScrollPane scrollPane = new JBScrollPane(tfDesc);
        scrollPane.setPreferredSize(new JBDimension(400, 150));
        scrollPane.setBorder(JBUI.Borders.empty());
        // 设置平滑窗体 滚动条
        scrollPane.getVerticalScrollBar().setUnitIncrement(14);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(14);
        scrollPane.getVerticalScrollBar().setDoubleBuffered(true);
        scrollPane.getHorizontalScrollBar().setDoubleBuffered(true);

        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.weightx = 1;
        constraints.weighty = 1;
        panel.add(scrollPane, constraints);

        // 第三行 第一列
        JLabel lineNumber = new JLabel(I18N.get("bookmark.windows.lineNum"));
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.weightx = 0;
        constraints.weighty = 2;
        panel.add(lineNumber, constraints);

        // 第三行 第二列
        constraints.gridx = 1;
        constraints.gridy = 2;
        constraints.weightx = 1;
        constraints.weighty = 2;
        panel.add(bookmarkLineNum, constraints);

        pack();

        return panel;
    }

    /**
     * 设置输入检查
     */
    private void setInputInspect(Project project, EditorTextField textField) {
        new ComponentValidator(project).withValidator(() -> {
            if (textField.getText().isBlank()) {
                return new ValidationInfo("Bookmark name is not empty.", textField);
            }
            return null;
        }).installOn(textField);

        textField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void documentChanged(@NotNull DocumentEvent event) {
                ComponentValidator.getInstance(textField).ifPresent(ComponentValidator::revalidate);
                setOKActionEnabled(!textField.getText().isBlank());
            }
        });
    }

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {
        return tfName;
    }

    @Override
    protected void doOKAction() {
        super.doOKAction();
        String name = tfName.getText();
        // 书签描述信息
        String desc = tfDesc.getText();
        // 书签所在行号
        int lineNum = (int) bookmarkLineNum.getValue() - 1;
        this.bookmarkEditService.getSelectorMessage((parentNode, enableGroup) -> {
            if ((parentNode == null || parentNode.isGroup()) && null != oKAction) {
                oKAction.onAction(name, desc, lineNum, parentNode, enableGroup);
            } else {
                BookmarkNoticeUtil.errorMessages(openProject, "Bookmark parent is not group, Please select again.");
            }
        });
    }

    public void showAndCallback(OnOKAction onOKAction) {
        this.oKAction = onOKAction;
        show();
    }

    public interface OnOKAction {
        void onAction(String name, String desc, Integer lineNum, BookmarkTreeNode parentNode, boolean enableGroup);
    }

}
