package org.bookmark.pro.service.settings;

import com.intellij.ide.util.PropertiesComponent;
import org.apache.commons.lang3.StringUtils;
import org.bookmark.pro.base.I18N;
import org.bookmark.pro.constants.BookmarkProConstant;
import org.bookmark.pro.constants.BookmarkProIcon;
import org.bookmark.pro.utils.BookmarkUtil;

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
        return Objects.toString(properties.getValue(BookmarkProConstant.BOOKMARK_BACKUP), "");
    }

    public String getBackUpTime() {
        // 默认12个小时备份一次
        return Objects.toString(properties.getValue(BookmarkProConstant.BOOKMARK_BACKUP_TIME), "12");
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
        return BookmarkUtil.getFontColor(Objects.toString(properties.getValue(BookmarkProConstant.BOOKMARK_PREFIX_COLOR), "128,128,128,255"));
    }

    public void setPrefixColor(Color color) {
        if (color != null) {
            properties.setValue(BookmarkProConstant.BOOKMARK_PREFIX_COLOR, byColor(color));
        }
    }

    public Color getSeparatorColor() {
        return BookmarkUtil.getFontColor(Objects.toString(properties.getValue(BookmarkProConstant.BOOKMARK_SEPARATOR_COLOR), "128,128,128,255"));
    }

    public void setSeparatorColor(Color color) {
        if (color != null) {
            properties.setValue(BookmarkProConstant.BOOKMARK_SEPARATOR_COLOR, byColor(color));
        }
    }

    public Color getContentColor() {
        return BookmarkUtil.getFontColor(Objects.toString(properties.getValue(BookmarkProConstant.BOOKMARK_CONTENT_COLOR), "153,153,255,255"));
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
        if (StringUtils.isBlank(lineDocument)) {
            return true;
        }
        return Boolean.valueOf(lineDocument);
    }

    public boolean getAutoBackup() {
        String autoBackup = properties.getValue(BookmarkProConstant.BOOKMARK_AUTO_BACKUP);
        if (StringUtils.isEmpty(autoBackup)) {
            return false;
        }
        return Boolean.valueOf(autoBackup);
    }

    public void setAutoBackup(boolean autoBackup) {
        properties.setValue(BookmarkProConstant.BOOKMARK_AUTO_BACKUP, autoBackup);
    }

    public void setTipType(String tipsType) {
        properties.setValue(BookmarkProConstant.BOOKMARK_TIPS_TYPE, tipsType);
    }

    public String getTipType() {
        return properties.getValue(BookmarkProConstant.BOOKMARK_TIPS_TYPE, I18N.get("setting.general.tipItem1"));
    }
}
