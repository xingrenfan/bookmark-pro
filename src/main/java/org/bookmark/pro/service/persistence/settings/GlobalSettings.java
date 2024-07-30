package org.bookmark.pro.service.persistence.settings;

import com.intellij.ide.util.PropertiesComponent;
import org.bookmark.pro.constants.BookmarkProConstant;
import org.bookmark.pro.constants.BookmarkProIcon;
import org.bookmark.pro.utils.BookmarkProUtil;
import org.bookmark.pro.utils.CharacterUtil;

import java.awt.*;
import java.util.Objects;

/**
 * 书签全局设置
 *
 * @author Lyon
 * @date 2024/04/24
 */
class GlobalSettings extends BaseSetting {
    protected GlobalSettings(PropertiesComponent properties) {
        super(properties);
    }

    public Integer getShowNum() {
        return Integer.valueOf(Objects.toString(properties.getValue(BookmarkProConstant.BOOKMARK_SHOW_NUMBER), "30"));
    }

    public void setShowNum(String value) {
        if (value != null) {
            properties.setValue(BookmarkProConstant.BOOKMARK_SHOW_NUMBER, value);
        }
    }

    public Integer getTreePanelShowNum() {
        return Integer.valueOf(Objects.toString(properties.getValue(BookmarkProConstant.BOOKMARK_TREE_PANEL_SHOW_NUMBER), "120"));
    }

    public void setTreePanelShowNum(String value) {
        if (value != null) {
            properties.setValue(BookmarkProConstant.BOOKMARK_TREE_PANEL_SHOW_NUMBER, value);
        }
    }

    public String getPrefix() {
        return Objects.toString(properties.getValue(BookmarkProConstant.BOOKMARK_PREFIX), BookmarkProIcon.DEFAULT_PREFIX_SIGN);
    }

    public String getBackUp() {
        return Objects.toString(properties.getValue(BookmarkProConstant.BOOKMARK_BACKUP), "~");
    }

    public String getBackUpTime() {
        return Objects.toString(properties.getValue(BookmarkProConstant.BOOKMARK_BACKUP_TIME), "~");
    }

    public void setPrefix(String text) {
        if (text != null) {
            properties.setValue(BookmarkProConstant.BOOKMARK_PREFIX, text);
        }
    }

    public void setBackUP(String text) {
        if (text != null) {
            properties.setValue(BookmarkProConstant.BOOKMARK_BACKUP, text);
        }
    }

    public void setBackUpTime(String text) {
        if (text != null) {
            properties.setValue(BookmarkProConstant.BOOKMARK_BACKUP_TIME, text);
        }
    }

    public String getContent() {
        return "BookmarkContent";
    }

    public Color getPrefixColor() {
        return BookmarkProUtil.getFontColor(Objects.toString(properties.getValue(BookmarkProConstant.BOOKMARK_PREFIX_COLOR), "128,128,128,255"));
    }

    public void setPrefixColor(Color color) {
        if (color != null) {
            properties.setValue(BookmarkProConstant.BOOKMARK_PREFIX_COLOR, byColor(color));
        }
    }

    public Color getSeparatorColor() {
        return BookmarkProUtil.getFontColor(Objects.toString(properties.getValue(BookmarkProConstant.BOOKMARK_SEPARATOR_COLOR), "128,128,128,255"));
    }

    public void setSeparatorColor(Color color) {
        if (color != null) {
            properties.setValue(BookmarkProConstant.BOOKMARK_SEPARATOR_COLOR, byColor(color));
        }
    }

    public Color getContentColor() {
        return BookmarkProUtil.getFontColor(Objects.toString(properties.getValue(BookmarkProConstant.BOOKMARK_CONTENT_COLOR), "153,153,255,255"));
    }

    public void setContentColor(Color color) {
        if (color != null) {
            properties.setValue(BookmarkProConstant.BOOKMARK_CONTENT_COLOR, byColor(color));
        }
    }

    private String byColor(final Color color) {
        return String.format("%d,%d,%d,%d", color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    public void setLineDocument(boolean lineDocument) {
        properties.setValue(BookmarkProConstant.BOOKMARK_LINE_DOCUMENT, lineDocument);
    }

    public boolean getLineDocument() {
        String lineDocument = properties.getValue(BookmarkProConstant.BOOKMARK_LINE_DOCUMENT);
        if (CharacterUtil.isEmpty(lineDocument)) {
            return false;
        }
        return Boolean.valueOf(lineDocument);
    }
}
