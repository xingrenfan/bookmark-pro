package org.bookmark.pro.utils;

import org.apache.commons.lang3.ArrayUtils;

import java.util.Objects;

/**
 * 字符工具类
 *
 * @author Lyon
 * @date 2024/04/22
 */
public class CharacterUtil {
    private CharacterUtil() {
    }


    /**
     * <p>Checks if a CharSequence is empty (""), null or whitespace only.</p>
     *
     * <p>Whitespace is defined by {@link Character#isWhitespace(char)}.</p>
     *
     * <pre>
     * StringUtils.isBlank(null)      = true
     * StringUtils.isBlank("")        = true
     * StringUtils.isBlank(" ")       = true
     * StringUtils.isBlank("bob")     = false
     * StringUtils.isBlank("  bob  ") = false
     * </pre>
     *
     * @param cs the CharSequence to check, may be null
     * @return {@code true} if the CharSequence is null, empty or whitespace only
     * @since 2.0
     * @since 3.0 Changed signature from isBlank(String) to isBlank(CharSequence)
     */
    public static boolean isBlank(final CharSequence cs) {
        final int strLen = length(cs);
        if (strLen == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Gets a CharSequence length or {@code 0} if the CharSequence is
     * {@code null}.
     *
     * @param cs a CharSequence or {@code null}
     * @return CharSequence length or {@code 0} if the CharSequence is
     * {@code null}.
     * @since 2.4
     * @since 3.0 Changed signature from length(String) to length(CharSequence)
     */
    public static int length(final CharSequence cs) {
        return cs == null ? 0 : cs.length();
    }

    /**
     * 字符序列为空
     *
     * @param cs 字符
     * @return boolean
     */
    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    /**
     * 字符序列不为空
     *
     * @param cs 字符
     * @return boolean
     */
    public static boolean isNotEmpty(CharSequence cs) {
        return !isEmpty(cs);
    }

    /**
     * 截取字符序列
     *
     * @param message 消息
     * @param start   开始
     * @param end     结束
     * @return {@link String}
     */
    public static String substring(String message, int start, int end) {
        if (message == null) {
            return null;
        } else {
            if (end < 0) {
                end += message.length();
            }

            if (start < 0) {
                start += message.length();
            }

            if (end > message.length()) {
                end = message.length();
            }

            if (start > end) {
                return "";
            } else {
                if (start < 0) {
                    start = 0;
                }

                if (end < 0) {
                    end = 0;
                }

                return message.substring(start, end);
            }
        }
    }

    /**
     * 字符序列为空
     *
     * @param css 字符序列
     * @return boolean
     */
    public static boolean isAnyEmpty(CharSequence... css) {
        if (!ArrayUtils.isEmpty(css)) {
            CharSequence[] var1 = css;
            int var2 = css.length;
            for (int var3 = 0; var3 < var2; ++var3) {
                CharSequence cs = var1[var3];
                if (isEmpty(cs)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取缩略消息
     *
     * @param message      消息
     * @param abbrevMarker 缩写标记
     * @param maxWidth     除缩略标记外最大长度
     * @return {@link String}
     */
    public static String abbreviate(String message, String abbrevMarker, int maxWidth) {
        if (Objects.toString(message, "").length() <= maxWidth) {
            return message + abbrevMarker;
        }
        return abbreviate(message, abbrevMarker, 0, maxWidth + abbrevMarker.length());
    }

    /**
     * 获取缩略消息
     *
     * @param message      消息
     * @param abbrevMarker 缩写标记
     * @param offset       开始位置
     * @param maxWidth     显示消息的最大长度
     * @return {@link String}
     */
    public static String abbreviate(String message, String abbrevMarker, int offset, int maxWidth) {
        if (isNotEmpty(message) && "".equals(abbrevMarker) && maxWidth > 0) {
            return substring(message, 0, maxWidth);
        } else if (isAnyEmpty(message, abbrevMarker)) {
            return message;
        } else {
            int abbrevMarkerLength = abbrevMarker.length();
            int minAbbrevWidth = abbrevMarkerLength + 1;
            int minAbbrevWidthOffset = abbrevMarkerLength + abbrevMarkerLength + 1;
            if (maxWidth < minAbbrevWidth) {
                throw new IllegalArgumentException(String.format("Minimum abbreviation width is %d", minAbbrevWidth));
            } else {
                int strLen = message.length();
                if (strLen <= maxWidth) {
                    return message;
                } else {
                    if (offset > strLen) {
                        offset = strLen;
                    }
                    if (strLen - offset < maxWidth - abbrevMarkerLength) {
                        offset = strLen - (maxWidth - abbrevMarkerLength);
                    }
                    if (offset <= abbrevMarkerLength + 1) {
                        return message.substring(0, maxWidth - abbrevMarkerLength) + abbrevMarker;
                    } else if (maxWidth < minAbbrevWidthOffset) {
                        throw new IllegalArgumentException(String.format("Minimum abbreviation width with offset is %d", minAbbrevWidthOffset));
                    } else {
                        return offset + maxWidth - abbrevMarkerLength < strLen ? abbrevMarker + abbreviate(message.substring(offset), abbrevMarker, maxWidth - abbrevMarkerLength) : abbrevMarker + message.substring(strLen - (maxWidth - abbrevMarkerLength));
                    }
                }
            }
        }
    }
}
