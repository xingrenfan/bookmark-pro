package org.bookmark.pro.domain;

import org.apache.commons.lang3.StringUtils;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.UUID;

/**
 * 书签持久化对象<br/>
 * bookmark：true 为书签<br/>
 * bookmark：false 为书签分组 <br/><br/>
 * <p>需要注意，一些「书签」使用的属性，和「书签分组」</p>
 *
 * @author Nonoas
 * @date 2023/6/5
 */
@XmlRootElement
public class BookmarkPro {
    private String uuid;
    private String commitHash;
    private int index;
    /**
     * 标记所在行
     */
    private int line;
    /**
     * 标记所在列
     */
    private int column;
    /**
     * 标记行内容 MD5值
     */
    private String markLineMd5;
    /**
     * 书签已失效
     */
    private Boolean invalid;
    /**
     * 描述
     */
    private String desc;
    /**
     * 书签名
     */
    private String name;
    /**
     * 是书签
     */
    private boolean bookmark;
    /**
     * 是分组
     */
    private boolean group;
    /**
     * 虚拟文件路径
     */
    private String virtualFilePath;
    /**
     * 子节点书签
     */
    private List<BookmarkPro> children;

    public void setChildren(List<BookmarkPro> children) {
        this.children = children;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getCommitHash() {
        if (StringUtils.isBlank(commitHash)) {
            return StringUtils.isBlank(getUuid()) ? UUID.randomUUID().toString() : getUuid();
        }
        return commitHash;
    }

    public void setCommitHash(String commitHash) {
        this.commitHash = commitHash;
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

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public String getMarkLineMd5() {
        return markLineMd5;
    }

    public void setMarkLineMd5(String markLineMd5) {
        this.markLineMd5 = markLineMd5;
    }

    public Boolean getInvalid() {
        return invalid;
    }

    public void setInvalid(Boolean invalid) {
        this.invalid = invalid;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isBookmark() {
        return bookmark;
    }

    public void setBookmark(boolean bookmark) {
        this.bookmark = bookmark;
    }

    public boolean isGroup() {
        return group;
    }

    public void setGroup(boolean group) {
        this.group = group;
    }

    public String getVirtualFilePath() {
        return virtualFilePath;
    }

    public void setVirtualFilePath(String virtualFilePath) {
        this.virtualFilePath = virtualFilePath;
    }

    public List<BookmarkPro> getChildren() {
        return children;
    }

    @Override
    public String toString() {
        return name;
    }
}
