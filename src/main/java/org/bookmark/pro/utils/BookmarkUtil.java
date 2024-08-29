package org.bookmark.pro.utils;

import com.google.gson.Gson;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.bookmark.pro.domain.BookmarkPro;
import org.bookmark.pro.domain.model.AbstractTreeNodeModel;
import org.bookmark.pro.domain.model.BookmarkConverter;
import org.bookmark.pro.service.tree.component.BookmarkTreeNode;

import java.awt.*;
import java.nio.file.FileSystemNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * 字符串处理工具
 *
 * @author Lyon
 * @date 2024/04/09
 */
public class BookmarkUtil {
    private BookmarkUtil() {
    }

    /**
     * 获取字体颜色
     *
     * @param color 颜色
     * @return {@link Color}
     */
    public static Color getFontColor(final String color) {
        final String[] rgb = color.split(",");
        return new Color(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2]), Integer.parseInt(rgb[3]));
    }

    /**
     * 虚拟文件名
     *
     * @param project 项目
     * @param file    文件
     * @return {@link String}
     */
    public static String virtualFileName(Project project, VirtualFile file) {
        if (project == null) {
            throw new RuntimeException("Project initialization not completion, please use initProjectData function initialization data.");
        }
        if (file != null && file.exists()) {
            return file.getCanonicalPath().replace(project.getBasePath(), "");
        }
        throw new FileSystemNotFoundException("VirtualFile not found");
    }

    /**
     * 虚拟文件存在
     *
     * @param project 项目
     * @param file    文件
     * @return boolean
     */
    public static boolean virtualFileExist(Project project, VirtualFile file) {
        if (project == null) {
            throw new RuntimeException("Project initialization not completion, please use initProjectData function initialization data.");
        }
        if (file != null && file.exists()) {
            // 文件存在 返回
            return true;
        }
        // 根据路径重新构建文件
        VirtualFile pathFile = LocalFileSystem.getInstance().findFileByPath(file.getPath());
        if (pathFile != null && pathFile.exists()) {
            // 文件存在 返回
            return true;
        }
        // 文件不存在 返回
        return false;
    }

    /**
     * 自动获取标记行的描述
     *
     * @param editor    编辑器
     * @param lineIndex 行索引
     * @return {@link String}
     */
    public static String getAutoDescription(final Editor editor, final int lineIndex) {
        String autoDescription = editor.getSelectionModel().getSelectedText();
        if (autoDescription == null) {
            Document document = editor.getDocument();
            autoDescription = document.getCharsSequence().subSequence(document.getLineStartOffset(lineIndex), document.getLineEndOffset(lineIndex)).toString().trim();
        }
        return autoDescription;
    }


    /**
     * 复制对象 - 可以为 null
     *
     * @param object 待复制对象
     * @param clazz  目标对象类型
     * @return {@link T}
     */
    public static <T> T copyObject(T object, Class<T> clazz) {
        if (null == object) {
            return null;
        }
        Gson gson = new Gson();
        String json = gson.toJson(object, clazz);
        return gson.fromJson(json, clazz);
    }

    /**
     * 书签树节点转对象
     *
     * @param node 节点
     * @return {@link BookmarkPro}
     */
    public static BookmarkPro nodeToBean(BookmarkTreeNode node) {
        int childCount = node.getChildCount();
        AbstractTreeNodeModel model = (AbstractTreeNodeModel) node.getUserObject();
        BookmarkPro po = BookmarkConverter.modelToBean(model);

        if (0 == childCount) {
            return po;
        }

        List<BookmarkPro> children = new ArrayList<>();
        BookmarkTreeNode child;
        for (int i = 0; i < childCount; i++) {
            child = (BookmarkTreeNode) node.getChildAt(i);
            children.add(BookmarkUtil.nodeToBean(child));
        }
        po.setChildren(children);
        return po;
    }
}
