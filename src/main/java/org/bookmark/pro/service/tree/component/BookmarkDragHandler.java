package org.bookmark.pro.service.tree.component;

import org.bookmark.pro.constants.BookmarkConstants;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.datatransfer.Transferable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 拖动处理程序
 *
 * @author Lyon
 * @date 2024/03/27
 */
public class BookmarkDragHandler extends TransferHandler {
    @Override
    public int getSourceActions(JComponent c) {
        return MOVE;
    }

    @Override
    protected Transferable createTransferable(JComponent treeComponent) {
        BookmarkTree tree = (BookmarkTree) treeComponent;
        int[] paths = tree.getSelectionRows();
        if (paths != null && paths.length > 0) {
            return new NodesTransferable(paths);
        }
        return null;
    }

    @Override
    protected void exportDone(JComponent source, Transferable data, int action) {
        if (action != MOVE) {
            return;
        }
        super.exportDone(source, data, action);
    }

    @Override
    public boolean canImport(TransferSupport support) {
        JTree.DropLocation dl = (JTree.DropLocation) support.getDropLocation();
        TreePath destPath = dl.getPath();
        if (destPath == null) {
            return false;
        }
        BookmarkTreeNode targetNode = (BookmarkTreeNode) destPath.getLastPathComponent();
        return targetNode != null && targetNode.isGroup();
    }

    @Override
    public boolean importData(TransferSupport support) {
        JTree.DropLocation dl = (JTree.DropLocation) support.getDropLocation();
        BookmarkTree tree = (BookmarkTree) support.getComponent();
        TreePath destPath = dl.getPath();
        BookmarkTreeNode targetNode = (BookmarkTreeNode) destPath.getLastPathComponent();

        try {
            Transferable transferable = support.getTransferable();
            int[] rows = (int[]) transferable.getTransferData(BookmarkConstants.NODES_FLAVOR);
            DefaultTreeModel model = tree.getModel();

            List<BookmarkTreeNode> nodes = Arrays.stream(rows).mapToObj(tree::getNodeForRow).collect(Collectors.toList());

            int childIndex = dl.getChildIndex();

            if (-1 == childIndex) {
                for (BookmarkTreeNode node : nodes) {
                    // 目标节点不能是拖动节点的后代，拖动节点不能是目标节点的直接子代
                    if (!targetNode.isNodeAncestor(node) && !targetNode.isNodeChild(node)) {
                        model.removeNodeFromParent(node);
                        model.insertNodeInto(node, targetNode, targetNode.getChildCount());
                    }
                }
            } else {
                Collections.reverse(nodes);
                for (BookmarkTreeNode node : nodes) {
                    // 目标节点不能是拖动节点的后代，拖动节点不能是目标节点的直接子代
                    if (!targetNode.isNodeAncestor(node)) {
                        if (targetNode.isNodeChild(node)) {
                            int index = targetNode.getIndex(node);
                            if (childIndex > index) {
                                childIndex = childIndex - 1;
                            }
                        }
                        model.removeNodeFromParent(node);
                        model.insertNodeInto(node, targetNode, childIndex);
                    }
                }
            }
            tree.expandPath(destPath);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
