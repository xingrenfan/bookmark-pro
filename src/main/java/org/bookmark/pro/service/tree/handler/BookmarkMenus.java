package org.bookmark.pro.service.tree.handler;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.InputValidatorEx;
import com.intellij.openapi.ui.JBMenuItem;
import com.intellij.openapi.ui.JBPopupMenu;
import com.intellij.openapi.ui.Messages;
import org.bookmark.pro.constants.BookmarkProIcon;
import org.bookmark.pro.context.BookmarkRunService;
import org.bookmark.pro.dialogs.modify.BookmarkEditDialog;
import org.bookmark.pro.domain.model.BookmarkNodeModel;
import org.bookmark.pro.domain.model.GroupNodeModel;
import org.bookmark.pro.service.tree.BookmarkTreeManage;
import org.bookmark.pro.utils.CharacterUtil;
import org.jsoup.internal.StringUtil;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;
import java.util.UUID;

/**
 * 书签树菜单
 *
 * @author Lyon
 * @date 2024/04/17
 */
class BookmarkMenus {
    protected BookmarkMenus() {
    }


    /**
     * 添加树菜单
     *
     * @param project      项目
     * @param bookmarkTree
     */
    protected void addTreeMenus(Project project, BookmarkTree bookmarkTree) {
        JBPopupMenu addGroupMenu = createGroupMenu(project,bookmarkTree);
        JBPopupMenu treeMenus = createTreeMenus(project, bookmarkTree);
        JBPopupMenu deleteMenus = createDeleteMenus(project, bookmarkTree);

        bookmarkTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (!SwingUtilities.isRightMouseButton(e)) {
                    return;
                }
                int row = bookmarkTree.getClosestRowForLocation(e.getX(), e.getY());
                if (row < 0) {
                    return;
                }
                if (!bookmarkTree.isRowSelected(row)) {
                    bookmarkTree.setSelectionRow(row);
                }

                if (0 == row) {
                    // 点击空白区域
                    addGroupMenu.show(bookmarkTree, e.getX() + 16, e.getY());
                } else {
                    TreePath path = bookmarkTree.getSelectionPath();
                    if (Objects.isNull(path)) {
                        return;
                    }
                    BookmarkTreeNode selectedNode = (BookmarkTreeNode) path.getLastPathComponent();
                    if (selectedNode.isGroup()) {
                        // 分组上点击操作显示全部菜单
                        treeMenus.show(bookmarkTree, e.getX() + 16, e.getY());
                    } else {
                        /// 书签上点击操作 只显示删除菜单
                        deleteMenus.show(bookmarkTree, e.getX() + 16, e.getY());
                    }
                }
            }
        });
    }

    /**
     * 树上点击菜单
     *
     * @param project      项目
     * @param bookmarkTree 书签树
     * @return {@link JBPopupMenu}
     */
    private JBPopupMenu createTreeMenus(final Project project, final BookmarkTree bookmarkTree) {
        JBPopupMenu popupMenu = new JBPopupMenu();
        popupMenu.add(createEditMenu(project, bookmarkTree));
        popupMenu.add(createDeleteMenu(project, bookmarkTree));
        popupMenu.add(new JPopupMenu.Separator());
        popupMenu.add(addGroupMenu(project,bookmarkTree));
        return popupMenu;
    }

    /**
     * 创建分组菜单
     *
     * @param bookmarkTree 书签树
     * @return {@link JBPopupMenu}
     */
    private JBPopupMenu createGroupMenu(Project project,final BookmarkTree bookmarkTree) {
        JBPopupMenu popupMenuRoot = new JBPopupMenu();
        JBMenuItem imAddGroupRoot = new JBMenuItem("Add Group");
        popupMenuRoot.add(imAddGroupRoot);
        // 增加书签分组
        addActionListener(project, imAddGroupRoot, bookmarkTree);
        return popupMenuRoot;
    }

    /**
     * 创建分组组菜单
     *
     * @param bookmarkTree 书签树
     * @return {@link JBMenuItem}
     */
    private JBMenuItem addGroupMenu(Project project,final BookmarkTree bookmarkTree) {
        JBMenuItem addGroupMenu = new JBMenuItem("AddGroup");
        // 增加书签分组
        addActionListener(project, addGroupMenu, bookmarkTree);
        return addGroupMenu;
    }

    /**
     * 创建删除菜单
     *
     * @param project      项目
     * @param bookmarkTree 书签树
     * @return {@link JBPopupMenu}
     */
    private JBPopupMenu createDeleteMenus(final Project project, final BookmarkTree bookmarkTree) {
        JBPopupMenu popupMenu = new JBPopupMenu();
        popupMenu.add(createEditMenu(project, bookmarkTree));
        popupMenu.add(createDeleteMenu(project, bookmarkTree));
        return popupMenu;
    }

    /**
     * 创建编辑菜单
     *
     * @param project      项目
     * @param bookmarkTree 书签树
     * @return {@link JBMenuItem}
     */
    private JBMenuItem createEditMenu(final Project project, final BookmarkTree bookmarkTree) {
        JBMenuItem editMenu = new JBMenuItem("Update");
        // 书签编辑操作
        editMenu.addActionListener(e -> {
            TreePath path = bookmarkTree.getSelectionPath();
            if (null == path) {
                return;
            }
            BookmarkTreeNode selectedNode = (BookmarkTreeNode) path.getLastPathComponent();
            if (selectedNode.isGroup()) {
                if (selectedNode.isBookmark()) {
                    // 书签
                    BookmarkNodeModel nodeModel = (BookmarkNodeModel) selectedNode.getUserObject();
                    new BookmarkEditDialog(project, false).defaultNode(nodeModel, null, false).showAndCallback((name, desc, lineNum, parentNode, enableGroup) -> {
                        BookmarkRunService.getDocumentService(project).removeBookmarkNode(project, selectedNode);
                        nodeModel.setName(name);
                        nodeModel.setDesc(desc);
                        nodeModel.setInvalid(false);
                        nodeModel.setGroup(enableGroup);
                        nodeModel.setBookmark(true);
                        selectedNode.setGroup(enableGroup);
                        selectedNode.setBookmark(true);
                        selectedNode.setInvalid(false);
                        BookmarkRunService.getDocumentService(project).addBookmarkNode(project, selectedNode);
                        BookmarkRunService.getBookmarkManage(project).changeBookmarkNode(selectedNode);
                    });
                } else {
                    // 修改书签分组
                    GroupNodeModel nodeModel = (GroupNodeModel) selectedNode.getUserObject();
                    // 校验规则
                    InputValidatorEx validatorEx = inputString -> {
                        if (StringUtil.isBlank(inputString)) return "Group name is not empty";
                        return null;
                    };
                    String groupName = Messages.showInputDialog("name:", "EditGroup", null, nodeModel.getName(), validatorEx);
                    if (StringUtil.isBlank(groupName)) {
                        return;
                    }
                    if (CharacterUtil.isEmpty(nodeModel.getUuid())) {
                        nodeModel.setUuid(UUID.randomUUID().toString());
                    }
                    if (!groupName.equals(nodeModel.getName())) {
                        nodeModel.setName(groupName);
                    }
                    BookmarkRunService.getBookmarkManage(project).getBookmarkTree().getModel().nodeChanged(selectedNode);
                }
            } else {
                // 书签
                BookmarkNodeModel nodeModel = (BookmarkNodeModel) selectedNode.getUserObject();
                new BookmarkEditDialog(project, false).defaultNode(nodeModel, null, false).showAndCallback((name, desc, lineNum, parentNode, enableGroup) -> {
                    BookmarkRunService.getDocumentService(project).removeBookmarkNode(project, selectedNode);
                    nodeModel.setName(name);
                    nodeModel.setDesc(desc);
                    nodeModel.setInvalid(false);
                    nodeModel.setGroup(enableGroup);
                    nodeModel.setBookmark(true);
                    selectedNode.setGroup(enableGroup);
                    selectedNode.setBookmark(true);
                    selectedNode.setInvalid(false);
                    BookmarkRunService.getDocumentService(project).addBookmarkNode(project, selectedNode);
                    BookmarkRunService.getBookmarkManage(project).changeBookmarkNode(selectedNode);
                });
            }
        });
        return editMenu;
    }

    /**
     * 创建删除菜单
     *
     * @param project      项目
     * @param bookmarkTree 书签树
     * @return {@link JBMenuItem}
     */
    private JBMenuItem createDeleteMenu(final Project project, final BookmarkTree bookmarkTree) {
        JBMenuItem deleteMenu = new JBMenuItem("Delete");
        // 书签删除操作
        deleteMenu.addActionListener(e -> {
            int result = Messages.showOkCancelDialog(project, "Delete Selected Bookmark", "Delete Bookmark", "Delete", "Cancel", Messages.getQuestionIcon());
            if (result == Messages.CANCEL) {
                return;
            }
            // 获取选定的节点
            TreePath[] selectionPaths = bookmarkTree.getSelectionPaths();
            if (selectionPaths == null) {
                return;
            }
            for (TreePath path : selectionPaths) {
                BookmarkTreeNode node = (BookmarkTreeNode) path.getLastPathComponent();
                BookmarkTreeNode parent = (BookmarkTreeNode) node.getParent();
                if (null == parent) {
                    continue;
                }
                bookmarkTree.removeNode(project, node);
            }
        });
        return deleteMenu;
    }

    /**
     * 添加操作侦听器
     *
     * @param item         项目
     * @param bookmarkTree 书签树
     */
    private void addActionListener(Project project,JBMenuItem item, final BookmarkTree bookmarkTree) {
        item.addActionListener(e -> {
            // 获取选定的节点
            BookmarkTreeNode selectedNode = (BookmarkTreeNode) bookmarkTree.getLastSelectedPathComponent();
            if (null == selectedNode) {
                return;
            }

            @SuppressWarnings("all") InputValidatorEx validatorEx = inputString -> {
                if (StringUtil.isBlank(inputString)) return "Group name is not empty";
                return null;
            };

            @SuppressWarnings("all") String groupName = Messages.showInputDialog("name:", "AddGroup", null, null, validatorEx);

            if (StringUtil.isBlank(groupName)) {
                return;
            }

            BookmarkTreeNode parent;
            if (selectedNode.isGroup()) {
                parent = selectedNode;
            } else {
                parent = (BookmarkTreeNode) selectedNode.getParent();
            }

            // 新的分组节点
            BookmarkTreeNode groupNode = new BookmarkTreeNode(new GroupNodeModel(groupName, UUID.randomUUID().toString()), true);
            bookmarkTree.getDefaultModel().insertNodeInto(groupNode, parent, 0);
            BookmarkRunService.getDocumentService(project).addBookmarkNode(project, groupNode);
        });
    }
}
