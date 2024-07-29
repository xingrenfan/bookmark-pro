package org.bookmark.pro.dialogs.modify;

import com.intellij.openapi.project.Project;
import org.bookmark.pro.domain.model.BookmarkNodeModel;
import org.bookmark.pro.service.tree.handler.BookmarkTreeNode;

import javax.swing.*;
import java.awt.*;

/**
 * 书签编辑服务
 *
 * @author Lyon
 * @date 2024/04/27
 */
public interface BookmarkEditService {
    /**
     * 设置行号检查
     *
     * @param panel           面板
     * @param constraints     约束
     * @param bookmarkLineNum 书签行数
     * @param value           价值
     * @param maxValue        最大值
     * @param showMaxLine     显示最大线
     */
    void lineNumInspect(JPanel panel, GridBagConstraints constraints, JSpinner bookmarkLineNum, int value, Integer maxValue, boolean showMaxLine);

    /**
     * 设置失效警告
     *
     * @param panel       面板
     * @param constraints 约束
     * @param message     消息
     */
    void setLapseWarning(JPanel panel, GridBagConstraints constraints, String message);

    /**
     * 显示书签父级
     *
     * @param project     项目
     * @param panel       面板
     * @param constraints 约束
     * @param node        节点
     */
    void showBookmarkParent(Project project, JPanel panel, GridBagConstraints constraints, BookmarkNodeModel node);

    /**
     * 显示书签启用
     *
     * @param panel       面板
     * @param constraints 约束
     * @param nodeModel
     */
    void showBookmarkEnable(JPanel panel, GridBagConstraints constraints, BookmarkNodeModel nodeModel);

    /**
     * 获取选择器消息
     *
     * @param selector 选择器
     * @return {@link BookmarkTreeNode}
     */
    void getSelectorMessage(BookmarkSelector selector);
}
