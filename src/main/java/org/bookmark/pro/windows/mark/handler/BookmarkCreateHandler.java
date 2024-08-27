package org.bookmark.pro.windows.mark.handler;

import com.intellij.openapi.project.Project;
import org.bookmark.pro.base.I18N;
import org.bookmark.pro.domain.model.BookmarkNodeModel;
import org.bookmark.pro.service.base.document.DocumentService;
import org.bookmark.pro.service.tree.TreeService;
import org.bookmark.pro.service.tree.component.BookmarkTree;
import org.bookmark.pro.service.tree.component.BookmarkTreeNode;
import org.bookmark.pro.utils.BookmarkEditorUtil;
import org.bookmark.pro.windows.mark.BookmarkEditService;
import org.bookmark.pro.windows.mark.BookmarkSelector;

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
    private Project openProject;

    // 行号
    private int nowLineNumber = 3;

    // 父节点
    private BookmarkTreeNode parentNode;

    /**
     * 书签启用分组
     */
    private JCheckBox enableGroup;

    public BookmarkCreateHandler(Project project) {
        super();
        this.openProject = project;
    }

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
        BookmarkTree bookmarkTree = TreeService.getInstance(this.openProject).getBookmarkTree();
        TreePath path = bookmarkTree.getSelectionPath();
        BookmarkTreeNode selectedNode = null;
        if (null == path) {
            selectedNode = (BookmarkTreeNode) bookmarkTree.getModel().getRoot();
        } else {
            selectedNode = (BookmarkTreeNode) path.getLastPathComponent();
        }

        if (selectedNode != null) {
            // 获取父级书签下拉选项书签
            JComboBox<BookmarkTreeNode> bookmarkType = new JComboBox<>();
            DocumentService.getInstance(this.openProject).getBookmarkGroup().stream().forEach(dto -> bookmarkType.addItem(dto));
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
        this.enableGroup = new JCheckBox(I18N.get("bookmark.windows.mark.as.group"));
        bookmarkEnableGroup(panel, constraints, this.nowLineNumber, this.enableGroup);
        this.nowLineNumber = this.nowLineNumber + 1;
    }

    @Override
    public void getSelectorMessage(BookmarkSelector selector) {
        selector.selector(this.parentNode, this.enableGroup.isSelected());
    }

}
