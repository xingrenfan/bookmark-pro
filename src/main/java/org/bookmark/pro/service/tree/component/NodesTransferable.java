package org.bookmark.pro.service.tree.component;

import org.bookmark.pro.constants.BookmarkConstants;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

/**
 * 可转移节点  自定义传输对象
 *
 * @author Lyon
 * @date 2024/03/27
 */
class NodesTransferable implements Transferable {
    private final int[] rows;

    protected NodesTransferable(int[] rows) {
        this.rows = rows;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{
                BookmarkConstants.NODES_FLAVOR
        };
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.equals(BookmarkConstants.NODES_FLAVOR);
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
        if (isDataFlavorSupported(flavor)) {
            return rows;
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }
}
