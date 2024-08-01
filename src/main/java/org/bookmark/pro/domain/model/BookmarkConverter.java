package org.bookmark.pro.domain.model;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.commons.lang3.StringUtils;
import org.bookmark.pro.domain.BookmarkPro;

/**
 * 书签转换器
 *
 * @author Nonoas
 * @date 2023/6/5
 */
public class BookmarkConverter {
    /**
     * 模型到持久化对象
     *
     * @param model 模型
     * @return {@link BookmarkPro}
     */
    public static BookmarkPro modelToBean(AbstractTreeNodeModel model) {
        if (model instanceof BookmarkNodeModel) {
            // 书签模型
            BookmarkNodeModel nodeModel = (BookmarkNodeModel) model;
            BookmarkPro bookmarkPro = getBookmarkPro(model, nodeModel);
            return bookmarkPro;
        } else {
            // 书签分组模型
            GroupNodeModel nodeModel = (GroupNodeModel) model;
            BookmarkPro po = new BookmarkPro();
            po.setUuid(nodeModel.getUuid());
            po.setName(nodeModel.getName());
            po.setBookmark(false);
            return po;
        }
    }

    private static BookmarkPro getBookmarkPro(AbstractTreeNodeModel model, BookmarkNodeModel nodeModel) {
        BookmarkPro bookmarkPro = new BookmarkPro();
        bookmarkPro.setUuid(nodeModel.getUuid());
        bookmarkPro.setIndex(nodeModel.getIndex());
        bookmarkPro.setLine(nodeModel.getLine());
        bookmarkPro.setColumn(nodeModel.getColumn());
        bookmarkPro.setName(model.getName());
        bookmarkPro.setDesc(nodeModel.getDesc());
        bookmarkPro.setBookmark(true);
        bookmarkPro.setGroup(nodeModel.isGroup());
        bookmarkPro.setMarkLineMd5(nodeModel.getMarkLineMd5());
        bookmarkPro.setInvalid(nodeModel.getInvalid());
        if (nodeModel.getVirtualFile() != null) {
            bookmarkPro.setVirtualFilePath(nodeModel.getVirtualFile().getPath());
        }
        return bookmarkPro;
    }

    /**
     * 持久化对象转书签模型
     *
     * @param project     项目
     * @param bookmarkPro 持久化对象
     * @return {@link AbstractTreeNodeModel}
     */
    public static AbstractTreeNodeModel beanToModel(Project project, BookmarkPro bookmarkPro) {
        if (bookmarkPro.isBookmark()) {
            // 书签模型
            BookmarkNodeModel model = new BookmarkNodeModel();
            model.setUuid(bookmarkPro.getUuid());
            model.setIndex(bookmarkPro.getIndex());
            model.setLine(bookmarkPro.getLine());
            model.setColumn(bookmarkPro.getColumn());
            model.setName(bookmarkPro.getName());
            model.setDesc(bookmarkPro.getDesc());
            model.setMarkLineMd5(bookmarkPro.getMarkLineMd5());
            model.setInvalid(bookmarkPro.getInvalid());
            model.setBookmark(bookmarkPro.isBookmark());
            model.setGroup(bookmarkPro.isGroup());
            if (StringUtils.isNotBlank(bookmarkPro.getVirtualFilePath())) {
                VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByPath(bookmarkPro.getVirtualFilePath());
                if (null == virtualFile) {
                    return model;
                }
                model.setVirtualFile(virtualFile);
            }
            return model;
        } else {
            // 分组模型
            GroupNodeModel model = new GroupNodeModel();
            model.setUuid(bookmarkPro.getUuid());
            model.setName(bookmarkPro.getName());
            return model;
        }
    }
}
