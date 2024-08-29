package org.bookmark.pro.service.base.settings;

import com.intellij.ide.util.PropertiesComponent;

/**
 * 书签设置基础
 *
 * @author Lyon
 * @date 2024/04/24
 */
class BaseSetting {
    protected final PropertiesComponent properties;

    protected BaseSetting(PropertiesComponent properties) {
        this.properties = properties;
    }
}
