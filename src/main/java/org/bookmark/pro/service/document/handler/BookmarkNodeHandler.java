package org.bookmark.pro.service.document.handler;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.commons.collections.map.MultiKeyMap;
import org.bookmark.pro.domain.model.BookmarkNodeModel;
import org.bookmark.pro.service.document.DocumentService;
import org.bookmark.pro.service.tree.handler.BookmarkTreeNode;
import org.bookmark.pro.utils.BookmarkUtil;

import javax.swing.tree.TreeNode;
import java.util.List;
import java.util.Set;

public class BookmarkNodeHandler implements DocumentService {
    // {project, file, line, uuid: BookmarkTreeNode}
    MultiKeyMap nodeCache = new MultiKeyMap();

    @Override
    public BookmarkTreeNode getBookmarkNode(Project project, VirtualFile virtualFile, int line) {
        String fileCanonicalPath = BookmarkUtil.virtualFileName(project, virtualFile);
        if (this.nodeCache.containsKey(project, fileCanonicalPath, line)) {
            BookmarkTreeNode bookmarkNode = (BookmarkTreeNode) nodeCache.get(project, fileCanonicalPath, line);
            BookmarkNodeModel nodeModel = (BookmarkNodeModel) bookmarkNode.getUserObject();
            if (nodeModel.getLine() == line) {
                return bookmarkNode;
            }
        }
        return null;
    }

    @Override
    public BookmarkTreeNode getBookmarkNode(String uuid) {
        return (BookmarkTreeNode) nodeCache.get(uuid);
    }

    @Override
    public Set<BookmarkTreeNode> getBookmarkNodes(Project project, VirtualFile virtualFile) {
        return Set.of();
    }

    @Override
    public void addBookmarkNode(Project project, BookmarkTreeNode bookmarkNode) {

    }

    @Override
    public void removeBookmarkNode(Project project, BookmarkTreeNode bookmarkNode) {

    }

    @Override
    public void setBookmarkInvalid(String uuid) {

    }

    @Override
    public void reloadingCacheNode(Project project, TreeNode treeNode) {

    }

    @Override
    public List<BookmarkTreeNode> getBookmarkGroup() {
        return List.of();
    }

    @Override
    public BookmarkTreeNode getGroupNode(String nodeName) {
        return null;
    }
}
