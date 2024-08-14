package org.bookmark.pro.constants;

import org.bookmark.pro.utils.BookmarkUtil;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;

/**
 * 书签定义常量
 *
 * @author Lyon
 * @date 2024/03/21
 */
public interface BookmarkSettingConstants {
    /**
     * 书签前缀
     */
    String BOOKMARK_PREFIX = "bookmark.pro:prefix";
    /**
     * 备份
     */
    String BOOKMARK_BACKUP = "bookmark.pro:backUp";

    /**
     * 书签自动备份
     */
    String BOOKMARK_AUTO_BACKUP = "bookmark.pro:autoBackup";

    /**
     * 备份时间
     */
    String BOOKMARK_BACKUP_TIME = "bookmark.pro:backUpTime";

    /**
     * 书签前缀颜色
     */
    String BOOKMARK_PREFIX_COLOR = "bookmark.pro:prefix:color";

    /**
     * 书签分隔符颜色
     */
    String BOOKMARK_SEPARATOR_COLOR = "bookmark.pro:separator:color";

    /**
     * 书签内容颜色
     */
    String BOOKMARK_CONTENT_COLOR = "bookmark.pro:content:color";

    /**
     * 书签显示字符个数
     */
    String BOOKMARK_SHOW_NUMBER = "bookmark.pro:show:number";

    /**
     * 书签树面板显示编号
     */
    String BOOKMARK_TREE_PANEL_SHOW_NUMBER = "bookmark.pro:tree:panel.show:number";

    /**
     * 书签行结束文档
     */
    String BOOKMARK_LINE_DOCUMENT = "bookmark.pro:line:end:document";

    /**
     * 书签选中 提示类型
     */
    String BOOKMARK_TIPS_TYPE = "bookmark.pro.tip.type";

    /**
     * 节点数据风格
     */
    DataFlavor NODES_FLAVOR = new DataFlavor(int[].class, "Tree Rows");

    /**
     * 书签名称和描述分隔符
     */
    String BOOKMARK_NAME_AND_DESC_SEPARATOR = " // ";

    /**
     * 警告颜色 - 大红
     */
    Color WARNING_COLOR = BookmarkUtil.getFontColor("255,0,0,255");

    /**
     * 提示颜色 - 黄色
     */
    Color CUE_COLOR = BookmarkUtil.getFontColor("255,233,150,255");


    /**
     * 提示颜色 - 浅绿色
     */
    Color GREEN_COLOR = BookmarkUtil.getFontColor("162,198,167,255");
}
