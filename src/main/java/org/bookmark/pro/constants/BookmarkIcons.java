package org.bookmark.pro.constants;

import com.intellij.openapi.util.IconLoader;
import org.bookmark.pro.base.I18N;

import javax.swing.*;

/**
 * 书签图标常量
 *
 * @author Lyon
 * @date 2024/04/02
 */
public interface BookmarkIcons {
    /**
     * 书签徽标图标
     */
    Icon BOOKMARK_LOGO_ICON = IconLoader.getIcon("icons/bookmark.svg", BookmarkIcons.class);

    /**
     * 文件夹 图标
     */
    Icon FOLDER_ICON = IconLoader.getIcon("icons/folder.svg", BookmarkIcons.class);

    /**
     * 纯分组 图标
     */
    Icon GROUP_ICON = IconLoader.getIcon("icons/group.svg", BookmarkIcons.class);

    /**
     * 书签分组 图标
     */
    Icon BOOKMARK_GROUP_ICON = IconLoader.getIcon("icons/group_mark.svg", BookmarkIcons.class);

    /**
     * 添加猫点
     */
    Icon BOOKMARK_CAT_POINT = IconLoader.getIcon("icons/point.svg", BookmarkIcons.class);

    /**
     * 书签 图标
     */
    Icon BOOKMARK_ICON = IconLoader.getIcon("icons/mark.svg", BookmarkIcons.class);

    /**
     * 书签失效图标
     */
    Icon BOOKMARK_INVALID_ICON = IconLoader.getIcon("icons/invalid.svg", BookmarkIcons.class);

    /**
     * 文件丢失图标
     */
    Icon FILE_LOSE_ICON = IconLoader.getIcon("icons/lose.svg", BookmarkIcons.class);

    /**
     * 眼标志
     */
    String EYE_SIGN = "👀 ";

    /**
     * 赞标志
     */
    String SUPPORT_SIGN = "👍 ";

    /**
     * 问题
     */
    String ISSUE_SIGN = "😥 ";

    /**
     * 无效书签标记
     */
    String INVALID_SIGN = "(❌ " + I18N.get("bookmark.windows.invalid") + ")";


    /**
     * 书签默认前缀
     */
    String DEFAULT_PREFIX_SIGN = "✈ ";

    /**
     * 字符书签标志
     */
    String BOOKMARK_NOTICE_SIGN = "🔖 ";



    /**
     * 分组标记-公交车
     */
    String GROUP_SIGN = "🚌";

    /**
     * 书签标志 - 小汽车
     */
    String BOOKMARK_SIGN = "🚗";

    /**
     * 群组书签标志
     */
    String GROUP_BOOKMARK_SIGN = "[" + BookmarkIcons.BOOKMARK_SIGN + "|" + BookmarkIcons.GROUP_SIGN + "]";

    /**
     * 书签警告标志
     */
    String BOOKMARK_WANING_SIGN = "⚠ ";


    /**
     * 警告
     */
    String WARNING_ERROR_SIGN = "❌ ";
}
