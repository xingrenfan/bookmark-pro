package org.bookmark.pro.constants;

import org.bookmark.pro.utils.BookmarkProUtil;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;

/**
 * 书签定义常量
 *
 * @author Lyon
 * @date 2024/03/21
 */
public interface BookmarkProConstant {
    /**
     * 书签自述文件 URI
     */
    String BOOKMARK_README_URI = "https://github.com/xingrenfan/bookmark-pro/issues";

    /**
     * idea 插件下载地址
     */
    String IDEA_PLUGIN_URI = "https://plugins.jetbrains.com/plugin/24441-markbook-pro";

    /**
     * 书签前缀
     */
    String BOOKMARK_PREFIX = "bookmark.pro:prefix";

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
    Color WARNING_COLOR = BookmarkProUtil.getFontColor("255,0,0,255");

    /**
     * 提示颜色 - 黄色
     */
    Color CUE_COLOR = BookmarkProUtil.getFontColor("255,233,150,255");


    /**
     * 提示颜色 - 浅绿色
     */
    Color GREEN_COLOR = BookmarkProUtil.getFontColor("162,198,167,255");
}
