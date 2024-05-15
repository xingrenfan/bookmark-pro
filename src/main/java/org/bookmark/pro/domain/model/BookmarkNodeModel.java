package org.bookmark.pro.domain.model;

import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.bookmark.pro.constants.BookmarkProConstant;
import org.bookmark.pro.constants.BookmarkProIcon;
import org.bookmark.pro.domain.model.handler.OpenChangeFile;
import org.bookmark.pro.utils.BookmarkNoticeUtil;

/**
 * 书签数据模型
 *
 * @author Nonoas
 * @date 2023/6/4
 */
public class BookmarkNodeModel implements AbstractTreeNodeModel {

    /**
     * 书签唯一标识UUID
     */
    private String uuid;

    /**
     * 索引
     */
    private int index;

    /**
     * 书签所在行
     */
    private int line;

    /**
     * 标记行内容 MD5值
     */
    private String markLineMd5;

    /**
     * 书签已失效
     */
    private Boolean invalid;

    /**
     * 书签所在列
     */
    private int column;

    /**
     * 书签名
     */
    private String name;

    /**
     * 书签详细说明
     */
    private String desc;

    /**
     * 虚拟文件
     */
    private VirtualFile virtualFile;

    /**
     * 是分组
     */
    private boolean group;

    /**
     * 是书签
     */
    private boolean bookmark;

    public BookmarkNodeModel() {
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isBookmark() {
        return bookmark;
    }

    public void setBookmark(boolean bookmark) {
        this.bookmark = bookmark;
    }

    @Override
    public boolean isGroup() {
        return group;
    }

    @Override
    public String toString() {
        if (isBookmark() && isGroup()) {
            return name + BookmarkProIcon.GROUP_BOOKMARK_SIGN;
        } else {
            return name + "[" + BookmarkProIcon.BOOKMARK_SIGN + "]";
        }
    }

    public void openFileDescriptor(Project project) {
        if (virtualFile != null) {
            if (virtualFile.exists()) {
                // 文件跳转器
                new OpenFileDescriptor(project, virtualFile, line, column).navigate(true);
                return;
            }
            VirtualFile pathFile = LocalFileSystem.getInstance().findFileByPath(virtualFile.getPath());
            if (pathFile != null && pathFile.exists() && pathFile.isValid()) {
                // 变更的文件（通过编辑器删除并且重新恢复的）打开
                new OpenChangeFile(project, virtualFile, line, column).navigate(true);
                return;
            }
        }
        BookmarkNoticeUtil.errorMessages(project, "Bookmark [" + name + BookmarkProConstant.BOOKMARK_NAME_AND_DESC_SEPARATOR + desc + "] file does not exist");
    }

    public Boolean getInvalid() {
        if (invalid == null) {
            return false;
        }
        return invalid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public String getMarkLineMd5() {
        return markLineMd5;
    }

    public void setMarkLineMd5(String markLineMd5) {
        this.markLineMd5 = markLineMd5;
    }

    public void setInvalid(Boolean invalid) {
        this.invalid = invalid;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public VirtualFile getVirtualFile() {
        return virtualFile;
    }

    public void setGroup(boolean group) {
        this.group = group;
    }

    public void setVirtualFile(VirtualFile virtualFile) {
        this.virtualFile = virtualFile;
    }
}
