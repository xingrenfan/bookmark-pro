package org.bookmark.pro.dialogs.modify.handler;

import org.bookmark.pro.constants.BookmarkProConstant;
import org.bookmark.pro.service.tree.handler.BookmarkTreeNode;

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
        JLabel warningLabel = new JLabel("Bookmark Warning:");
        constraints.gridx = 0;
        constraints.gridy = rowNum;
        constraints.weightx = 0;
        constraints.weighty = rowNum;
        panel.add(warningLabel, constraints);
        warningLabel.setForeground(BookmarkProConstant.WARNING_COLOR);

        // 第四行 第二列 警告展示信息
        JLabel warningMessage = new JLabel(message);
        constraints.gridx = 1;
        constraints.gridy = rowNum;
        constraints.weightx = 1;
        constraints.weighty = rowNum;
        panel.add(warningMessage, constraints);
        warningMessage.setForeground(BookmarkProConstant.WARNING_COLOR);
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
        JLabel lbName = new JLabel("Bookmark Line Limit:");
        constraints.gridx = 0;
        constraints.gridy = rowNum;
        constraints.weightx = 0;
        constraints.weighty = rowNum;
        panel.add(lbName, constraints);
        lbName.setForeground(BookmarkProConstant.CUE_COLOR);
        // 第四行 第二列
        JLabel number = new JLabel("The bookmark Line number max is " + maxValue + ".");
        constraints.gridx = 1;
        constraints.gridy = rowNum;
        constraints.weightx = 1;
        constraints.weighty = rowNum;
        panel.add(number, constraints);
        number.setForeground(BookmarkProConstant.CUE_COLOR);
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
        JLabel lbName = new JLabel("Bookmark Parent:");
        constraints.gridx = 0;
        constraints.gridy = rowNum;
        constraints.weightx = 0;
        constraints.weighty = rowNum;
        panel.add(lbName, constraints);
        lbName.setForeground(BookmarkProConstant.CUE_COLOR);

        // 第四行 第二列
        constraints.gridx = 1;
        constraints.gridy = rowNum;
        constraints.weightx = 1;
        constraints.weighty = rowNum;
        panel.add(bookmarkType, constraints);
        bookmarkType.setForeground(BookmarkProConstant.CUE_COLOR);
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
        JLabel lbName = new JLabel("Bookmark Enable Group:");
        constraints.gridx = 0;
        constraints.gridy = rowNum;
        constraints.weightx = 0;
        constraints.weighty = rowNum;
        panel.add(lbName, constraints);
        lbName.setForeground(BookmarkProConstant.GREEN_COLOR);

        // 第四行 第二列
        constraints.gridx = 1;
        constraints.gridy = rowNum;
        constraints.weightx = 1;
        constraints.weighty = rowNum;
        panel.add(enableGroup, constraints);
        enableGroup.setForeground(BookmarkProConstant.GREEN_COLOR);
    }
}
