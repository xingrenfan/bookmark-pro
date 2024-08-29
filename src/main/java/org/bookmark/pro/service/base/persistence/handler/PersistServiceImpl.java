package org.bookmark.pro.service.base.persistence.handler;

import com.google.gson.Gson;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.bookmark.pro.domain.BookmarkPro;
import org.bookmark.pro.domain.model.AbstractTreeNodeModel;
import org.bookmark.pro.domain.model.BookmarkConverter;
import org.bookmark.pro.service.base.persistence.PersistService;
import org.bookmark.pro.service.tree.component.BookmarkTree;
import org.bookmark.pro.service.tree.component.BookmarkTreeNode;
import org.bookmark.pro.utils.BookmarkNoticeUtil;
import org.bookmark.pro.utils.BookmarkUtil;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * 持久化服务实现程序
 *
 * @author Lyon
 * @date 2024/08/14
 */
public final class PersistServiceImpl implements PersistService {
    private Project openProject;

    public PersistServiceImpl(Project project) {
        this.openProject = project;
    }

    private PersistComponent getPersistComponent() {
        return this.openProject.getService(PersistComponent.class);
    }

    @Override
    public void saveBookmark(BookmarkTree bookmarkTree) {
        if (bookmarkTree == null) {
            return;
        }
        // 保存书签树
        BookmarkTreeNode rootNode = (BookmarkTreeNode) bookmarkTree.getModel().getRoot();
        BookmarkPro bookmark = BookmarkUtil.nodeToBean(rootNode);
        // 获取持久化书签对象
        getPersistComponent().setState(bookmark);
    }

    @Override
    public boolean exportBookmark(String savePath) {
        // 获取持久化书签对象
        BookmarkPro bookmarkPro = BookmarkUtil.copyObject(getPersistComponent().getState(), BookmarkPro.class);
        // 替换IDEA编辑器的$PROJECT_DIR$
        String basePath = this.openProject.getBasePath();
        if (StringUtils.isNotBlank(basePath)) {
            String projectDir = FileUtil.toSystemIndependentName(basePath);
            replaceBookmarkPath(bookmarkPro, projectDir, "$PROJECT_DIR$");
        }
        // 导出到文件
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(savePath, Charset.forName("utf-8"));
            Gson gson = new Gson();
            gson.toJson(bookmarkPro, fileWriter);
            return true;
        } catch (IOException e) {
            BookmarkNoticeUtil.errorMessages(this.openProject, "Export bookmark fail, message: " + e.getMessage());
        } finally {
            try {
                if (fileWriter != null) {
                    fileWriter.close();
                }
            } catch (IOException e) {
            }
        }
        return false;
    }

    @Override
    public boolean importBookmark(VirtualFile virtualFile) {
        try {
            String content = new String(virtualFile.contentsToByteArray());
            // 字符串 转 书签对象
            Gson gson = new Gson();
            // TODO 导入备份文件这一行异常
            BookmarkPro bookmark = gson.fromJson(content, BookmarkPro.class);
            String basePath = this.openProject.getBasePath();
            if (StringUtils.isNotBlank(basePath)) {
                String projectDir = FileUtil.toSystemIndependentName(basePath);
                // 修复书签文件路径
                replaceBookmarkPath(bookmark, "$PROJECT_DIR$", projectDir);
            }
            // 重新设置持久化数据
            getPersistComponent().setState(bookmark);
            return true;
        } catch (IOException e) {
            BookmarkNoticeUtil.errorMessages(this.openProject, "Import bookmark fail, message: " + e.getMessage());
        }
        return false;
    }

    /**
     * 替换书签路径
     *
     * @param bookmark    书签
     * @param targetChar  被替换字符
     * @param replacement 要替换成字符
     */
    private void replaceBookmarkPath(BookmarkPro bookmark, String targetChar, String replacement) {
        String virtualFilePath = bookmark.getVirtualFilePath();
        if (StringUtils.isNotEmpty(virtualFilePath)) {
            bookmark.setVirtualFilePath(virtualFilePath.replace(targetChar, replacement));
        }
        if (CollectionUtils.isNotEmpty(bookmark.getChildren())) {
            for (BookmarkPro child : bookmark.getChildren()) {
                replaceBookmarkPath(child, targetChar, replacement);
            }
        }
    }

    /**
     * 生成树节点
     *
     * @param bookmark 书签
     * @return {@link BookmarkTreeNode}
     */
    private BookmarkTreeNode generateTreeNode(BookmarkPro bookmark) {
        AbstractTreeNodeModel model = BookmarkConverter.beanToModel(bookmark);
        if (bookmark.isBookmark() && !bookmark.isGroup()) {
            // 纯书签不支持分组
            return new BookmarkTreeNode(model);
        }
        // 是纯分组或者书签分组 支持分组
        BookmarkTreeNode treeNode = new BookmarkTreeNode(model, true);

        List<BookmarkPro> childrenBookmarks = bookmark.getChildren();
        if (CollectionUtils.isEmpty(childrenBookmarks)) {
            return treeNode;
        }
        for (BookmarkPro childrenBookmark : childrenBookmarks) {
            treeNode.add(generateTreeNode(childrenBookmark));
        }
        return treeNode;
    }


    /**
     * 生成树节点
     *
     * @param bookmark   书签
     * @param searchText 搜索文本（正则表达式）
     * @return {@link BookmarkTreeNode}
     */
    private BookmarkTreeNode generateTreeNodeSearch(BookmarkPro bookmark, String searchText) {
        AbstractTreeNodeModel model = BookmarkConverter.beanToModel(bookmark);
        BookmarkTreeNode treeNode = new BookmarkTreeNode(model, !bookmark.isBookmark());

        // 编译正则表达式
        Pattern pattern;
        try {
            pattern = Pattern.compile(searchText, Pattern.CASE_INSENSITIVE);
        } catch (PatternSyntaxException e) {
            // 如果正则表达式无效，返回空节点
            return null;
        }

        // 判断当前节点是否匹配搜索文本
        boolean isParentMatched = pattern.matcher(bookmark.getName()).find() || (bookmark.getDesc() != null && pattern.matcher(bookmark.getDesc()).find());

        List<BookmarkPro> childrenBookmarks = bookmark.getChildren();
        if (CollectionUtils.isEmpty(childrenBookmarks)) {
            return isParentMatched ? treeNode : null;
        }

        // 如果父节点匹配，则添加所有子节点
        if (isParentMatched) {
            for (BookmarkPro childrenBookmark : childrenBookmarks) {
                BookmarkTreeNode childNode = new BookmarkTreeNode(BookmarkConverter.beanToModel(childrenBookmark));
                treeNode.add(childNode);
                addAllChildren(childNode, childrenBookmark);
            }
            return treeNode;
        }

        // 递归处理子节点
        for (BookmarkPro childrenBookmark : childrenBookmarks) {
            BookmarkTreeNode childNode = generateTreeNodeSearch(childrenBookmark, searchText);
            if (childNode != null) {
                treeNode.add(childNode);
            }
        }

        return treeNode.getChildCount() > 0 ? treeNode : null;
    }

    /**
     * 递归添加所有子节点
     *
     * @param parentNode 父节点
     * @param bookmark   书签
     */
    private void addAllChildren(BookmarkTreeNode parentNode, BookmarkPro bookmark) {
        List<BookmarkPro> childrenBookmarks = bookmark.getChildren();
        if (CollectionUtils.isNotEmpty(childrenBookmarks)) {
            for (BookmarkPro child : childrenBookmarks) {
                BookmarkTreeNode childNode = new BookmarkTreeNode(BookmarkConverter.beanToModel(child));
                parentNode.add(childNode);
                addAllChildren(childNode, child);
            }
        }
    }


    @Override
    public void addOneBookmark(BookmarkPro bookmark) {
        PersistComponent service = getPersistComponent();
        // 添加书签对象
        service.getState().getChildren().add(bookmark);
    }

    @Override
    public BookmarkTreeNode getBookmarkNode() {
        PersistComponent service = getPersistComponent();
        return generateTreeNode(service.getState());
    }


    @Override
    public BookmarkTreeNode getBookmarkNodeSearch(String searchText) {
        PersistComponent service = getPersistComponent();
        return generateTreeNodeSearch(service.getState(), searchText);
    }
}
