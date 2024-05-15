package org.bookmark.pro.utils;

import java.util.Collection;

/**
 * 集合工具类
 *
 * @author Lyon
 * @date 2024/04/22
 */
public class CollectionUtil {
    private CollectionUtil() {
    }


    /**
     * 集合为空
     *
     * @param collection 集合
     * @return boolean
     */
    public static boolean isEmpty(Collection collection) {
        return collection == null ||
                collection.isEmpty();
    }
}
