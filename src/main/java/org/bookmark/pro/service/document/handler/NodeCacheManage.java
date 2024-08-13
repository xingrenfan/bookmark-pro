package org.bookmark.pro.service.document.handler;

import com.intellij.openapi.vfs.VirtualFile;
import org.bookmark.pro.service.tree.handler.BookmarkTreeNode;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

class NodeCacheManage {
    // 书签缓存{UUID: BookmarkTreeNode}
    private final Map<String, BookmarkTreeNode> uuidMarkCache = new ConcurrentHashMap<>(64);

    // 分组节点缓存{bookmarkName:BookmarkTreeNode}
    protected final Map<String, BookmarkTreeNode> groupNodeCache = new ConcurrentHashMap<>(64);

    // BookmarkTreeNode 缓存: 通过 虚拟文件名 直接取到节点引用 {VirtualFileHash: [UUID]}
    protected final Map<Integer, Set<BookmarkTreeNode>> bookmarkFileName = new HashMap<>(64);

    protected Set<BookmarkTreeNode> nodeForVirtualFileHash(VirtualFile virtualFile) {
        Integer hash = Objects.hashCode(virtualFile);
        return bookmarkFileName.get(hash);
    }

    protected void setNodeForVirtualFileHash(VirtualFile virtualFile, BookmarkTreeNode node) {
        Integer hash = Objects.hashCode(virtualFile);
        return bookmarkFileName.get(hash);
    }


    private int hash(Object key1, Object key2) {
        int h = 0;
        if (key1 != null) {
            h ^= key1.hashCode();
        }

        if (key2 != null) {
            h ^= key2.hashCode();
        }

        h += ~(h << 9);
        h ^= h >>> 14;
        h += h << 4;
        h ^= h >>> 10;
        return h;
    }
}
