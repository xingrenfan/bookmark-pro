package org.bookmark.pro.service.document.handler;

import com.intellij.openapi.vfs.VirtualFile;
import org.bookmark.pro.service.tree.handler.BookmarkTreeNode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

class NodeCacheManage {
    // 书签缓存{commitHash: BookmarkTreeNode}
    private final Map<String, BookmarkTreeNode> bookmarkHashCache = new ConcurrentHashMap<>(64);

    // 分组节点缓存{bookmarkName:BookmarkTreeNode}
    private final Map<String, BookmarkTreeNode> bookmarkNameCache = new ConcurrentHashMap<>(64);

    // BookmarkTreeNode 缓存: 通过 虚拟文件名 直接取到节点引用 {VirtualFileHash: [UUID]}
    private final Map<Integer, Set<BookmarkTreeNode>> virtualFileCache = new HashMap<>(64);

    protected Set<BookmarkTreeNode> nodeForVirtualFile(VirtualFile virtualFile) {
        Integer hash = Objects.hashCode(virtualFile);
        return virtualFileCache.get(hash);
    }

    protected void setNodeForVirtualFile(VirtualFile virtualFile, BookmarkTreeNode node) {
        Integer hash = Objects.hashCode(virtualFile);
        if (virtualFileCache.containsKey(hash)) {
            Set<BookmarkTreeNode> treeNodes = virtualFileCache.get(hash);
            treeNodes.add(node);
            return;
        }
        Set<BookmarkTreeNode> treeNodes = new HashSet<>(16);
        treeNodes.add(node);
        virtualFileCache.put(hash, treeNodes);
    }

    protected BookmarkTreeNode nodeForName(String bookmarkName) {
        return bookmarkNameCache.get(bookmarkName);
    }

    protected void setNodeForName(String bookmarkName, BookmarkTreeNode node) {
        bookmarkNameCache.put(bookmarkName, node);
    }

    protected BookmarkTreeNode nodeForHash(String commitHash) {
        return bookmarkHashCache.get(commitHash);
    }

    protected void setNodeForHash(String commitHash, BookmarkTreeNode node) {
        bookmarkHashCache.put(commitHash, node);
    }
}
