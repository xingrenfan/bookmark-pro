package org.bookmark.pro.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 签名工具类
 *
 * @author Lyon
 * @date 2024/04/22
 */
public class SignatureUtil {
    private SignatureUtil() {
    }

    private static final char[] HEX_CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * 获取 MD5 摘要
     *
     * @param message 消息
     * @return {@link String}
     */
    public static String getMd5Digest(String message) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] hexDigest = messageDigest.digest(message.getBytes(StandardCharsets.UTF_8));
            char[] chars = new char[32];
            for (int i = 0; i < chars.length; i = i + 2) {
                byte b = hexDigest[i / 2];
                chars[i] = HEX_CHARS[(b >>> 0x4) & 0xf];
                chars[i + 1] = HEX_CHARS[b & 0xf];
            }
            return new String(chars);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("Could not find MessageDigest with algorithm MD5", ex);
        }
    }
}
