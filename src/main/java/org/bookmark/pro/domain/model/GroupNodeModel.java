package org.bookmark.pro.domain.model;

import org.bookmark.pro.constants.BookmarkIcons;

import java.util.Objects;

/**
 * 书签分组模型
 *
 * @author Nonoas
 * @date 2023/6/6
 */
public class GroupNodeModel implements AbstractTreeNodeModel {

    /**
     * 分组唯一标识UUID
     */
    private String commitHash;

    private String name;

    public GroupNodeModel() {

    }

    public GroupNodeModel(String name, String commitHash) {
        this.name = name;
        this.commitHash = commitHash;
    }

    @Override
    public String getDesc() {
        return "";
    }

    @Override
    public boolean isBookmark() {
        return false;
    }

    @Override
    public boolean isGroup() {
        return true;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name + "[" + BookmarkIcons.GROUP_SIGN + "]";
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCommitHash() {
        return Objects.toString(commitHash, "");
    }

    public void setCommitHash(String commitHash) {
        this.commitHash = commitHash;
    }
}
