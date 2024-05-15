package org.bookmark.pro.domain.model;

import org.bookmark.pro.constants.BookmarkProIcon;

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
    private String uuid;

    private String name;

    public GroupNodeModel() {

    }

    public GroupNodeModel(String name, String uuid) {
        this.name = name;
        this.uuid = uuid;
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
        return name + "[" + BookmarkProIcon.GROUP_SIGN + "]";
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
