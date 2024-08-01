package org.bookmark.pro.dialogs.modify.handler;

import com.intellij.openapi.project.Project;
import org.bookmark.pro.context.BookmarkRunService;
import org.bookmark.pro.dialogs.modify.BookmarkEditService;
import org.bookmark.pro.dialogs.modify.BookmarkSelector;
import org.bookmark.pro.domain.model.BookmarkNodeModel;
import org.bookmark.pro.service.document.DocumentService;
import org.bookmark.pro.service.tree.handler.BookmarkTree;
import org.bookmark.pro.service.tree.handler.BookmarkTreeNode;
import org.bookmark.pro.utils.BookmarkEditorUtil;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 书签创建处理程序
 *
 * @author Lyon
 * @date 2024/04/26
 */
public class BookmarkCreateHandler extends BaseServiceUtil implements BookmarkEditService {
    // 行号
    private int nowLineNumber = 3;

    // 父节点
    private BookmarkTreeNode parentNode;

    /**
     * 书签启用分组
     */
    private JCheckBox enableGroup;

    @Override
    public void lineNumInspect(JPanel panel, GridBagConstraints constraints, JSpinner bookmarkLineNum, int value, Integer maxValue, boolean showMaxLine) {
        // 通过最大行显示控制是否可以修改书签行
        bookmarkLineNum.setEnabled(showMaxLine);
        SpinnerModel model = new SpinnerNumberModel(value, 1, maxValue.intValue(), 1);
        BookmarkEditorUtil.addNumberFormatter(bookmarkLineNum, model);
        bookmarkLineNum(panel, constraints, maxValue, this.nowLineNumber);
        this.nowLineNumber = this.nowLineNumber + 1;
    }

    @Override
    public void setLapseWarning(JPanel panel, GridBagConstraints constraints, String message) {
        bookmarkWarning(panel, constraints, message, this.nowLineNumber);
        this.nowLineNumber = this.nowLineNumber + 1;
    }

    @Override
    public void showBookmarkParent(Project project, JPanel panel, GridBagConstraints constraints, BookmarkNodeModel node) {
        BookmarkTree bookmarkTree = BookmarkRunService.getBookmarkManage(project).getBookmarkTree();
        TreePath path = bookmarkTree.getSelectionPath();
        BookmarkTreeNode selectedNode = null;
        if (null == path) {
            selectedNode = (BookmarkTreeNode)bookmarkTree.getModel().getRoot();
        }else {
            selectedNode = (BookmarkTreeNode) path.getLastPathComponent();
        }

        if (selectedNode != null) {
            DocumentService documentService = BookmarkRunService.getDocumentService(project);
            // 获取父级书签下拉选项书签
            JComboBox<BookmarkTreeNode> bookmarkType = new JComboBox<>();
            documentService.getBookmarkGroup().stream().forEach(dto -> bookmarkType.addItem(dto));
            if (selectedNode.isGroup()) {
                bookmarkType.setSelectedItem(selectedNode);
                this.parentNode = selectedNode;
            } else {
                BookmarkTreeNode selectedNodeParent = (BookmarkTreeNode) selectedNode.getParent();
                if (selectedNodeParent != null) {
                    bookmarkType.setSelectedItem(selectedNodeParent);
                    this.parentNode = selectedNodeParent;
                }
            }

            if (this.parentNode != null) {
                bookmarkParent(panel, constraints, this.nowLineNumber, bookmarkType);
                bookmarkType.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        parentNode = (BookmarkTreeNode) bookmarkType.getSelectedItem();
                    }
                });
                this.nowLineNumber = this.nowLineNumber + 1;
            }
        }
    }

    @Override
    public void showBookmarkEnable(JPanel panel, GridBagConstraints constraints, BookmarkNodeModel nodeModel) {
        this.enableGroup = new JCheckBox("Bookmark can be used as group");
        bookmarkEnableGroup(panel, constraints, this.nowLineNumber, this.enableGroup);
        this.nowLineNumber = this.nowLineNumber + 1;
    }

    @Override
    public void getSelectorMessage(BookmarkSelector selector) {
        selector.selector(this.parentNode, this.enableGroup.isSelected());
    }

}
