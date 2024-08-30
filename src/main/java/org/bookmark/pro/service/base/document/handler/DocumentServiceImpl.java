package org.bookmark.pro.service.base.document.handler;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.commons.collections.CollectionUtils;
import org.bookmark.pro.domain.model.AbstractTreeNodeModel;
import org.bookmark.pro.domain.model.BookmarkNodeModel;
import org.bookmark.pro.domain.model.GroupNodeModel;
import org.bookmark.pro.service.base.document.DocumentService;
import org.bookmark.pro.service.tree.component.BookmarkTreeNode;

import javax.swing.tree.TreeNode;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 文档缓存服务实现
 *
 * @author Lyon
 * @date 2024/08/14
 */
public final class DocumentServiceImpl implements DocumentService {
    private static final Logger LOG = Logger.getInstance(DocumentServiceImpl.class);
    // 书签缓存{commitHash: BookmarkTreeNode}
    private final Map<String, BookmarkTreeNode> bookmarkHashCache = new ConcurrentHashMap<>(64);

    // BookmarkTreeNode 缓存: 通过 虚拟文件名 直接取到节点引用 {VirtualFileHash: [UUID]}
    private final Map<Integer, Set<BookmarkTreeNode>> virtualFileCache = new HashMap<>(64);

    public DocumentServiceImpl(Project project) {
        LOG.debug("Load bookmark node cache message. project:{}", project.getName());
    }

    private void setVirtualFileCache(VirtualFile virtualFile, BookmarkTreeNode bookmarkNode) {
        Integer hash = Objects.hashCode(virtualFile);
        Set<BookmarkTreeNode> treeNodes = this.virtualFileCache.get(hash);
        if (CollectionUtils.isNotEmpty(treeNodes)) {
            treeNodes.add(bookmarkNode);
        } else {
            treeNodes = new HashSet<>(64);
            treeNodes.add(bookmarkNode);
        }
        this.virtualFileCache.put(hash, treeNodes);
    }

    private void removeVirtualFileCache(VirtualFile virtualFile, BookmarkTreeNode bookmarkNode) {
        Integer hash = Objects.hashCode(virtualFile);
        Set<BookmarkTreeNode> treeNodes = this.virtualFileCache.get(hash);
        if (CollectionUtils.isNotEmpty(treeNodes)) {
            treeNodes.remove(bookmarkNode);
            this.virtualFileCache.put(hash, treeNodes);
        }
    }

    @Override
    public BookmarkTreeNode getBookmarkNode(VirtualFile virtualFile, int line) {
        Integer hash = Objects.hashCode(virtualFile);
        if (this.virtualFileCache.containsKey(hash)) {
            for (BookmarkTreeNode treeNode : this.virtualFileCache.get(hash)) {
                BookmarkNodeModel nodeModel = (BookmarkNodeModel) treeNode.getUserObject();
                if (nodeModel.getLine() == line) {
                    return treeNode;
                }
            }
        }
        return null;
    }

    @Override
    public BookmarkTreeNode getBookmarkNode(String commitHash) {
        return this.bookmarkHashCache.get(commitHash);
    }

    @Override
    public Set<BookmarkTreeNode> getBookmarkNodes(VirtualFile virtualFile) {
        Integer hash = Objects.hashCode(virtualFile);
        return Optional.ofNullable(virtualFileCache.get(hash)).orElse(new HashSet<>());
    }

    @Override
    public void addBookmarkNode(BookmarkTreeNode bookmarkNode) {
        AbstractTreeNodeModel abstractTreeNodeModel = (AbstractTreeNodeModel) bookmarkNode.getUserObject();
        if (bookmarkNode.isBookmark()) {
            BookmarkNodeModel markNodeModel = (BookmarkNodeModel) abstractTreeNodeModel;
            this.bookmarkHashCache.put(markNodeModel.getCommitHash(), bookmarkNode);
            if (markNodeModel.getVirtualFile() != null) {
                this.setVirtualFileCache(markNodeModel.getVirtualFile(), bookmarkNode);
            } else {
                // 书签置为无效
                bookmarkNode.setInvalid(true);
                markNodeModel.setInvalid(true);
            }
        } else {
            GroupNodeModel groupNodeModel = (GroupNodeModel) abstractTreeNodeModel;
            this.bookmarkHashCache.put(groupNodeModel.getCommitHash(), bookmarkNode);
        }
    }

    @Override
    public void removeBookmarkNode(BookmarkTreeNode bookmarkNode) {
        AbstractTreeNodeModel abstractTreeNodeModel = (AbstractTreeNodeModel) bookmarkNode.getUserObject();
        if (bookmarkNode.isBookmark()) {
            BookmarkNodeModel markNodeModel = (BookmarkNodeModel) abstractTreeNodeModel;
            this.bookmarkHashCache.remove(markNodeModel.getCommitHash());
            if (markNodeModel.getVirtualFile() != null) {
                this.removeVirtualFileCache(markNodeModel.getVirtualFile(), bookmarkNode);
            }
        } else {
            GroupNodeModel groupNodeModel = (GroupNodeModel) abstractTreeNodeModel;
            this.bookmarkHashCache.remove(groupNodeModel.getCommitHash(), bookmarkNode);
        }
    }

    @Override
    public void setBookmarkInvalid(String commitHash) {
        BookmarkTreeNode markNode = this.bookmarkHashCache.get(commitHash);
        if (markNode != null) {
            markNode.setInvalid(true);
        }
    }

    @Override
    public void reloadingCacheNode(TreeNode treeNode) {
        if (treeNode instanceof BookmarkTreeNode) {
            BookmarkTreeNode bookmarkNode = (BookmarkTreeNode) treeNode;
            addBookmarkNode(bookmarkNode);
            if (bookmarkNode.isBookmark() && !bookmarkNode.isGroup()) {
                // 纯书签-没有子节点 跳过后续处理
                return;
            }
            Enumeration<TreeNode> children = bookmarkNode.children();
            if (children == null) {
                // 子节点为空 跳过后续处理
                return;
            }
            children.asIterator().forEachRemaining(dto -> reloadingCacheNode(dto));
        }
    }

    @Override
    public List<BookmarkTreeNode> getBookmarkGroup() {
        return this.bookmarkHashCache.entrySet().stream().map(
                dto -> dto.getValue()
        ).filter(BookmarkTreeNode::isGroup).collect(Collectors.toList());
    }
}
