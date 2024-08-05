package org.bookmark.pro.base;

import com.intellij.AbstractBundle;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * 国际化文本获取
 *
 * @author Nonoas
 * @date 2023/6/3
 */
public class I18N extends AbstractBundle {
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("i18n.i18n");

    public I18N(@NonNls @NotNull String pathToBundle) {
        super(pathToBundle);
    }

    public static @NotNull String get(String key) {
        return BUNDLE.getString(key);
    }

    public static @NotNull String get(String key, Object... param) {
        String string = BUNDLE.getString(key);
        return MessageFormat.format(string, param);
    }

}

