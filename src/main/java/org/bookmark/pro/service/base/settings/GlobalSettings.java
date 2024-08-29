package org.bookmark.pro.service.base.settings;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.application.ApplicationManager;
import org.apache.commons.lang3.StringUtils;
import org.bookmark.pro.base.I18N;
import org.bookmark.pro.constants.BookmarkConstants;
import org.bookmark.pro.constants.BookmarkIcons;
import org.bookmark.pro.utils.BookmarkUtil;

import java.awt.*;
import java.util.Objects;

/**
 * 应用全局设置
 *
 * @author Lyon
 * @date 2024/08/14
 */
public final class GlobalSettings extends BaseSetting {
    public GlobalSettings() {
        super(PropertiesComponent.getInstance());
    }

    public static GlobalSettings getInstance() {
        return ApplicationManager.getApplication().getService(GlobalSettings.class);
    }

    public Integer getShowNum() {
        return Integer.valueOf(Objects.toString(properties.getValue(BookmarkConstants.BOOKMARK_SHOW_NUMBER), "30"));
    }

    public void setShowNum(String value) {
        if (value != null) {
            properties.setValue(BookmarkConstants.BOOKMARK_SHOW_NUMBER, value);
        }
    }

    public Integer getTreePanelShowNum() {
        return Integer.valueOf(Objects.toString(properties.getValue(BookmarkConstants.BOOKMARK_TREE_PANEL_SHOW_NUMBER), "120"));
    }

    public void setTreePanelShowNum(String value) {
        if (value != null) {
            properties.setValue(BookmarkConstants.BOOKMARK_TREE_PANEL_SHOW_NUMBER, value);
        }
    }

    public String getPrefix() {
        return Objects.toString(properties.getValue(BookmarkConstants.BOOKMARK_PREFIX), BookmarkIcons.DEFAULT_PREFIX_SIGN);
    }

    public void setPrefix(String text) {
        if (text != null) {
            properties.setValue(BookmarkConstants.BOOKMARK_PREFIX, text);
        }
    }

    public Color getPrefixColor() {
        return BookmarkUtil.getFontColor(Objects.toString(properties.getValue(BookmarkConstants.BOOKMARK_PREFIX_COLOR), "128,128,128,255"));
    }

    public void setPrefixColor(Color color) {
        if (color != null) {
            properties.setValue(BookmarkConstants.BOOKMARK_PREFIX_COLOR, byColor(color));
        }
    }

    public Color getSeparatorColor() {
        return BookmarkUtil.getFontColor(Objects.toString(properties.getValue(BookmarkConstants.BOOKMARK_SEPARATOR_COLOR), "128,128,128,255"));
    }

    public void setSeparatorColor(Color color) {
        if (color != null) {
            properties.setValue(BookmarkConstants.BOOKMARK_SEPARATOR_COLOR, byColor(color));
        }
    }

    public Color getContentColor() {
        return BookmarkUtil.getFontColor(Objects.toString(properties.getValue(BookmarkConstants.BOOKMARK_CONTENT_COLOR), "153,153,255,255"));
    }

    public void setContentColor(Color color) {
        if (color != null) {
            properties.setValue(BookmarkConstants.BOOKMARK_CONTENT_COLOR, byColor(color));
        }
    }

    private String byColor(final Color color) {
        return String.format("%d,%d,%d,%d", color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    public void setLineDocument(boolean lineDocument) {
        properties.setValue(BookmarkConstants.BOOKMARK_LINE_DOCUMENT, lineDocument);
    }

    public boolean getLineDocument() {
        String lineDocument = properties.getValue(BookmarkConstants.BOOKMARK_LINE_DOCUMENT);
        if (StringUtils.isBlank(lineDocument)) {
            return true;
        }
        return Boolean.valueOf(lineDocument);
    }

    public void setTipType(String tipsType) {
        properties.setValue(BookmarkConstants.BOOKMARK_TIPS_TYPE, tipsType);
    }

    public String getTipType() {
        return properties.getValue(BookmarkConstants.BOOKMARK_TIPS_TYPE, BookmarkConstants.TIPS_FOR_TOOL);
    }
}
