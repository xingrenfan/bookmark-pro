package org.bookmark.pro.service.document.handler;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.bookmark.pro.domain.model.AbstractTreeNodeModel;
import org.bookmark.pro.domain.model.BookmarkNodeModel;
import org.bookmark.pro.domain.model.GroupNodeModel;
import org.bookmark.pro.service.document.DocumentService;
import org.bookmark.pro.service.tree.handler.BookmarkTreeNode;
import org.bookmark.pro.utils.BookmarkUtil;

import javax.swing.tree.TreeNode;
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

@Service(Service.Level.PROJECT)
public final class DocumentServiceHandler implements DocumentService {
    private Map<Project, NodeCacheManage> nodeCacheManage = new ConcurrentHashMap<>(16);

    @Override
    public BookmarkTreeNode getBookmarkNode(Project project, VirtualFile virtualFile, int line) {
        if (nodeCacheManage)

        String fileCanonicalPath = BookmarkUtil.virtualFileName(project, virtualFile);
        if (this.bookmarkFileName.containsKey(fileCanonicalPath)) {
            for (String uuid : this.bookmarkFileName.get(fileCanonicalPath)) {
                BookmarkTreeNode bookmarkNode = this.bookmarkNodeCache.get(uuid);
                BookmarkNodeModel nodeModel = (BookmarkNodeModel) bookmarkNode.getUserObject();
                if (nodeModel.getLine() == line) {
                    return bookmarkNode;
                }
            }
        }
        return null;
    }

    @Override
    public BookmarkTreeNode getBookmarkNode(Project project, String uuid) {
        return this.bookmarkNodeCache.get(uuid);
    }

    @Override
    public Set<BookmarkTreeNode> getBookmarkNodes(Project project, VirtualFile virtualFile) {
        String fileCanonicalPath = BookmarkUtil.virtualFileName(project, virtualFile);
        return Optional.ofNullable(this.bookmarkFileName.get(fileCanonicalPath)).orElse(new HashSet<>()).stream().map(this.bookmarkNodeCache::get).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    @Override
    public void addBookmarkNode(Project project, BookmarkTreeNode bookmarkNode) {
        AbstractTreeNodeModel abstractTreeNodeModel = (AbstractTreeNodeModel) bookmarkNode.getUserObject();
        if (bookmarkNode.isBookmark()) {
            BookmarkNodeModel nodeModel = (BookmarkNodeModel) abstractTreeNodeModel;
            if (this.bookmarkNodeCache.containsKey(nodeModel.getUuid())) {
                this.bookmarkNodeCache.remove(nodeModel.getUuid());
            }
            // 添加书签缓存
            this.bookmarkNodeCache.put(nodeModel.getUuid(), bookmarkNode);
            // 获取虚拟文件
            if (nodeModel.getVirtualFile() != null) {
                // 文件名
                String fileName = BookmarkUtil.virtualFileName(project, nodeModel.getVirtualFile());
                Set<String> cacheUuid;
                if (this.bookmarkFileName.containsKey(fileName)) {
                    cacheUuid = this.bookmarkFileName.get(fileName);
                } else {
                    cacheUuid = new HashSet<>(64);
                }
                cacheUuid.add(nodeModel.getUuid());
                this.bookmarkFileName.put(fileName, cacheUuid);
            } else {
                // 书签置为无效
                bookmarkNode.setInvalid(true);
                nodeModel.setInvalid(true);
            }
        }
        if (bookmarkNode.isGroup()) {
            if (bookmarkNode.isBookmark()) {
                BookmarkNodeModel nodeModel = (BookmarkNodeModel) abstractTreeNodeModel;
                this.groupNodeCache.put(nodeModel.getUuid(), bookmarkNode);
            } else {
                GroupNodeModel nodeModel = (GroupNodeModel) abstractTreeNodeModel;
                if (StringUtils.isBlank(nodeModel.getUuid())) {
                    // 添加书签缓存
                    this.groupNodeCache.put(nodeModel.getName(), bookmarkNode);
                } else {
                    this.groupNodeCache.put(nodeModel.getUuid(), bookmarkNode);
                }
            }
        }
    }

    @Override
    public void removeBookmarkNode(Project project, BookmarkTreeNode bookmarkNode) {
        AbstractTreeNodeModel nodeModel = (AbstractTreeNodeModel) bookmarkNode.getUserObject();
        String uuid = nodeModel.getUuid();
        // 删除书签节点缓存
        this.bookmarkNodeCache.remove(uuid);
        if (bookmarkNode.isBookmark()) {
            BookmarkNodeModel bookmarkNodeModel = (BookmarkNodeModel) nodeModel;
            VirtualFile virtualFile = bookmarkNodeModel.getVirtualFile();
            // 获取虚拟文件
            if (virtualFile != null) {
                // 文件名
                String fileName = BookmarkUtil.virtualFileName(project, virtualFile);
                if (this.bookmarkFileName.containsKey(fileName)) {
                    // 根据文件名获取所有书签
                    Set<String> cacheUuid = this.bookmarkFileName.get(fileName);
                    cacheUuid.remove(uuid);
                    if (CollectionUtils.isEmpty(cacheUuid)) {
                        this.bookmarkFileName.remove(fileName);
                    } else {
                        this.bookmarkFileName.put(fileName, cacheUuid);
                    }
                }
            }
        }
        if (bookmarkNode.isGroup()) {
            bookmarkNode.children().asIterator().forEachRemaining(treeNode -> {
                if (treeNode instanceof BookmarkTreeNode bookmarkTreeNode) {
                    removeBookmarkNode(project, bookmarkTreeNode);
                }
            });
            this.groupNodeCache.remove(uuid);
        }
    }

    @Override
    public void setBookmarkInvalid(Project project, String uuid) {
        BookmarkTreeNode bookmarkExist = this.bookmarkNodeCache.get(uuid);
        if (bookmarkExist != null) {
            bookmarkExist.setInvalid(true);
        }
    }

    @Override
    public void reloadingCacheNode(Project project, TreeNode treeNode) {
        if (treeNode instanceof BookmarkTreeNode) {
            BookmarkTreeNode bookmarkNode = (BookmarkTreeNode) treeNode;
            addBookmarkNode(project, bookmarkNode);
            if (bookmarkNode.isBookmark() && !bookmarkNode.isGroup()) {
                return;
            }
            // 判断并加载子集菜单
            Enumeration<TreeNode> children = bookmarkNode.children();
            if (children == null) {
                return;
            }
            children.asIterator().forEachRemaining(dto -> reloadingCacheNode(project, dto));
        }
    }

    @Override
    public List<BookmarkTreeNode> getBookmarkGroup() {
        List<BookmarkTreeNode> treeNodes = this.groupNodeCache.entrySet().stream().map(dto -> dto.getValue()).collect(Collectors.toList());
//        treeNodes.addAll(this.bookmarkNodeCache.entrySet().stream().map(dto -> dto.getValue()).collect(Collectors.toList()));
        return treeNodes;
    }

    @Override
    public BookmarkTreeNode getGroupNode(String nodeName) {
        return this.groupNodeCache.get(nodeName);
    }
}
