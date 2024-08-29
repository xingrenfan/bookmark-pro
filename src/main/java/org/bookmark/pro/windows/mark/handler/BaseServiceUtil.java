package org.bookmark.pro.windows.mark.handler;

import org.bookmark.pro.base.I18N;
import org.bookmark.pro.constants.BookmarkConstants;
import org.bookmark.pro.service.tree.component.BookmarkTreeNode;

import javax.swing.*;
import java.awt.*;

class BaseServiceUtil {
    /**
     * 设置书签警告
     *
     * @param panel       面板
     * @param constraints 约束
     * @param message     消息
     * @param rowNum      行数
     */
    protected void bookmarkWarning(JPanel panel, GridBagConstraints constraints, String message, int rowNum) {
        // 第四行 第一列 预警信息
        JLabel warningLabel = new JLabel(I18N.get("bookmark.windows.warning"));
        constraints.gridx = 0;
        constraints.gridy = rowNum;
        constraints.weightx = 0;
        constraints.weighty = rowNum;
        panel.add(warningLabel, constraints);
        warningLabel.setForeground(BookmarkConstants.WARNING_COLOR);

        // 第四行 第二列 警告展示信息
        JLabel warningMessage = new JLabel(message);
        constraints.gridx = 1;
        constraints.gridy = rowNum;
        constraints.weightx = 1;
        constraints.weighty = rowNum;
        panel.add(warningMessage, constraints);
        warningMessage.setForeground(BookmarkConstants.WARNING_COLOR);
    }

    /**
     * 设置书签最大行提示
     *
     * @param panel       面板
     * @param constraints 约束
     * @param maxValue    最大值
     * @param rowNum      行数
     */
    protected void bookmarkLineNum(JPanel panel, GridBagConstraints constraints, Integer maxValue, int rowNum) {
        // 第四行 第一列
        JLabel lbName = new JLabel(I18N.get("bookmark.windows.mark.line.limit"));
        constraints.gridx = 0;
        constraints.gridy = rowNum;
        constraints.weightx = 0;
        constraints.weighty = rowNum;
        panel.add(lbName, constraints);
        lbName.setForeground(BookmarkConstants.CUE_COLOR);
        // 第四行 第二列
        JLabel number = new JLabel(I18N.get("bookmark.windows.mark.line.max") + " " + maxValue);
        constraints.gridx = 1;
        constraints.gridy = rowNum;
        constraints.weightx = 1;
        constraints.weighty = rowNum;
        panel.add(number, constraints);
        number.setForeground(BookmarkConstants.CUE_COLOR);
    }

    /**
     * 展示此书签的父级
     *
     * @param panel        面板
     * @param constraints  约束
     * @param rowNum       行数
     * @param bookmarkType 书签类型选择器
     */
    protected void bookmarkParent(JPanel panel, GridBagConstraints constraints, int rowNum, JComboBox<BookmarkTreeNode> bookmarkType) {
        // 第四行 第一列
        JLabel lbName = new JLabel("bookmark.windows.parent");
        constraints.gridx = 0;
        constraints.gridy = rowNum;
        constraints.weightx = 0;
        constraints.weighty = rowNum;
        panel.add(lbName, constraints);
        lbName.setForeground(BookmarkConstants.CUE_COLOR);

        // 第四行 第二列
        constraints.gridx = 1;
        constraints.gridy = rowNum;
        constraints.weightx = 1;
        constraints.weighty = rowNum;
        panel.add(bookmarkType, constraints);
        bookmarkType.setForeground(BookmarkConstants.CUE_COLOR);
    }


    /**
     * 书签启用分组
     *
     * @param panel       面板
     * @param constraints 约束
     * @param rowNum      行数
     * @param enableGroup 启用组
     */
    protected void bookmarkEnableGroup(JPanel panel, GridBagConstraints constraints, int rowNum, JCheckBox enableGroup) {
        // 第四行 第一列
        JLabel lbName = new JLabel(I18N.get("bookmark.windows.mark.enable.group"));
        constraints.gridx = 0;
        constraints.gridy = rowNum;
        constraints.weightx = 0;
        constraints.weighty = rowNum;
        panel.add(lbName, constraints);
        lbName.setForeground(BookmarkConstants.GREEN_COLOR);

        // 第四行 第二列
        constraints.gridx = 1;
        constraints.gridy = rowNum;
        constraints.weightx = 1;
        constraints.weighty = rowNum;
        panel.add(enableGroup, constraints);
        enableGroup.setForeground(BookmarkConstants.GREEN_COLOR);
    }
}
