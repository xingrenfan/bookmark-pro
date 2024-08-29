package org.bookmark.pro.service.base.settings;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.application.ApplicationManager;
import org.apache.commons.lang3.StringUtils;
import org.bookmark.pro.constants.BookmarkConstants;

import java.util.Objects;

/**
 * 应用备份设置
 *
 * @author Lyon
 * @date 2024/08/14
 */
public final class BackupSettings extends BaseSetting {
    public BackupSettings() {
        super(PropertiesComponent.getInstance());
    }

    public static BackupSettings getInstance() {
        return ApplicationManager.getApplication().getService(BackupSettings.class);
    }


    public String getBackUp() {
        return Objects.toString(properties.getValue(BookmarkConstants.BOOKMARK_BACKUP), "");
    }

    public void setBackUP(String text) {
        if (text != null) {
            properties.setValue(BookmarkConstants.BOOKMARK_BACKUP, text);
        }
    }

    public String getBackUpTime() {
        // 默认12个小时备份一次
        return Objects.toString(properties.getValue(BookmarkConstants.BOOKMARK_BACKUP_TIME), "12");
    }

    public void setBackUpTime(String text) {
        if (text != null) {
            properties.setValue(BookmarkConstants.BOOKMARK_BACKUP_TIME, text);
        }
    }

    public boolean getAutoBackup() {
        String autoBackup = properties.getValue(BookmarkConstants.BOOKMARK_AUTO_BACKUP);
        if (StringUtils.isEmpty(autoBackup)) {
            return false;
        }
        return Boolean.valueOf(autoBackup);
    }

    public void setAutoBackup(boolean autoBackup) {
        properties.setValue(BookmarkConstants.BOOKMARK_AUTO_BACKUP, autoBackup);
    }
}
