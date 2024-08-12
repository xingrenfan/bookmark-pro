package org.bookmark.pro.service.persistence.handler;

import com.google.gson.Gson;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.bookmark.pro.context.BookmarkRunService;
import org.bookmark.pro.domain.BookmarkPro;
import org.bookmark.pro.domain.model.AbstractTreeNodeModel;
import org.bookmark.pro.domain.model.BookmarkConverter;
import org.bookmark.pro.service.persistence.PersistenceService;
import org.bookmark.pro.service.tree.BookmarkTreeManage;
import org.bookmark.pro.service.tree.handler.BookmarkTree;
import org.bookmark.pro.service.tree.handler.BookmarkTreeNode;
import org.bookmark.pro.utils.BookmarkNoticeUtil;
import org.bookmark.pro.utils.BookmarkUtil;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class PersistenceServiceHandler implements PersistenceService {
    private Project openProject;

    public PersistenceServiceHandler(Project openProject) {
        this.openProject = openProject;
    }

    private <T> T getPersistentService(Project project, Class<T> clazz) {
        return project.getService(clazz);
    }

    @Override
    public void saveBookmark(Project project) {
        BookmarkTreeManage bookmarkTreeManage = BookmarkRunService.getBookmarkManage(project);
        // 获取书签树
        BookmarkTree bookmarkTree = bookmarkTreeManage.getBookmarkTree();
        if (bookmarkTree == null) {
            return;
        }
        // 保存书签树
        BookmarkTreeNode rootNode = (BookmarkTreeNode) bookmarkTree.getModel().getRoot();
        BookmarkPro bookmark = BookmarkUtil.nodeToBean(rootNode);
        // 获取持久化书签对象
        PersistentService service = getPersistentService(project, PersistentService.class);
        service.setState(bookmark);
    }

    @Override
    public boolean exportBookmark(Project project, String savePath) {
        // 获取持久化书签对象
        PersistentService service = getPersistentService(project, PersistentService.class);
        BookmarkPro bookmarkPro = BookmarkUtil.copyObject(service.getState(), BookmarkPro.class);
        // 替换IDEA编辑器的$PROJECT_DIR$
        String basePath = project.getBasePath();
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
            BookmarkNoticeUtil.errorMessages(project, "Export bookmark fail, message: " + e.getMessage());
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
    public boolean importBookmark(Project project, VirtualFile virtualFile) {
        try {
            String content = new String(virtualFile.contentsToByteArray());
            // 字符串 转 书签对象
            Gson gson = new Gson();
            // TODO 导入备份文件这一行异常
            BookmarkPro bookmark = gson.fromJson(content, BookmarkPro.class);
            String basePath = project.getBasePath();
            if (StringUtils.isNotBlank(basePath)) {
                String projectDir = FileUtil.toSystemIndependentName(basePath);
                // 修复书签文件路径
                replaceBookmarkPath(bookmark, "$PROJECT_DIR$", projectDir);
            }
            // 获取持久化书签对象
            PersistentService service = getPersistentService(project, PersistentService.class);
            service.setState(bookmark);
            // 重新加载标签书
            BookmarkRunService.getBookmarkManagerPanel(project).reloadBookmarkTree(project, BookmarkRunService.getBookmarkManage(project).getBookmarkTree());
            return true;
        } catch (IOException e) {
            BookmarkNoticeUtil.errorMessages(project, "Import bookmark fail, message: " + e.getMessage());
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
     * @param project  项目
     * @param bookmark 书签
     * @return {@link BookmarkTreeNode}
     */
    private BookmarkTreeNode generateTreeNode(Project project, BookmarkPro bookmark) {
        AbstractTreeNodeModel model = BookmarkConverter.beanToModel(project, bookmark);
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
            treeNode.add(generateTreeNode(project, childrenBookmark));
        }
        return treeNode;
    }


    /**
     * 生成树节点
     *
     * @param project    项目
     * @param bookmark   书签
     * @param searchText 搜索文本（正则表达式）
     * @return {@link BookmarkTreeNode}
     */
    private BookmarkTreeNode generateTreeNodeSearch(Project project, BookmarkPro bookmark, String searchText) {
        AbstractTreeNodeModel model = BookmarkConverter.beanToModel(project, bookmark);
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
        boolean isParentMatched = pattern.matcher(bookmark.getName()).find() ||
                (bookmark.getDesc() != null && pattern.matcher(bookmark.getDesc()).find());

        List<BookmarkPro> childrenBookmarks = bookmark.getChildren();
        if (CollectionUtils.isEmpty(childrenBookmarks)) {
            return isParentMatched ? treeNode : null;
        }

        // 如果父节点匹配，则添加所有子节点
        if (isParentMatched) {
            for (BookmarkPro childrenBookmark : childrenBookmarks) {
                BookmarkTreeNode childNode = new BookmarkTreeNode(BookmarkConverter.beanToModel(project, childrenBookmark));
                treeNode.add(childNode);
                addAllChildren(childNode, childrenBookmark, project);
            }
            return treeNode;
        }

        // 递归处理子节点
        for (BookmarkPro childrenBookmark : childrenBookmarks) {
            BookmarkTreeNode childNode = generateTreeNodeSearch(project, childrenBookmark, searchText);
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
     * @param project    项目
     */
    private void addAllChildren(BookmarkTreeNode parentNode, BookmarkPro bookmark, Project project) {
        List<BookmarkPro> childrenBookmarks = bookmark.getChildren();
        if (CollectionUtils.isNotEmpty(childrenBookmarks)) {
            for (BookmarkPro child : childrenBookmarks) {
                BookmarkTreeNode childNode = new BookmarkTreeNode(BookmarkConverter.beanToModel(project, child));
                parentNode.add(childNode);
                addAllChildren(childNode, child, project);
            }
        }
    }


    @Override
    public void addOneBookmark(Project project, BookmarkPro bookmark) {
        PersistentService service = getPersistentService(project, PersistentService.class);
        // 添加书签对象
        service.getState().getChildren().add(bookmark);
    }

    @Override
    public BookmarkTreeNode getBookmarkNode(Project project) {
        PersistentService service = getPersistentService(project, PersistentService.class);
        return generateTreeNode(project, service.getState());
    }


    @Override
    public BookmarkTreeNode getBookmarkNodeSearch(Project project, String searchText) {
        PersistentService service = getPersistentService(project, PersistentService.class);
        return generateTreeNodeSearch(project, service.getState(), searchText);
    }
}
