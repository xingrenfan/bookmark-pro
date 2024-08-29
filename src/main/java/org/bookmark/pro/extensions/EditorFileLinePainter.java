package org.bookmark.pro.extensions;

import com.intellij.openapi.editor.EditorLinePainter;
import com.intellij.openapi.editor.LineExtensionInfo;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.commons.lang3.StringUtils;
import org.bookmark.pro.constants.BookmarkConstants;
import org.bookmark.pro.constants.BookmarkIcons;
import org.bookmark.pro.domain.model.BookmarkNodeModel;
import org.bookmark.pro.service.base.document.DocumentService;
import org.bookmark.pro.service.base.settings.GlobalSettings;
import org.bookmark.pro.service.tree.component.BookmarkTreeNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 编辑器行尾拓展
 *
 * @author Lyon
 * @date 2024/03/27
 */
public class EditorFileLinePainter extends EditorLinePainter {
    @Override
    public @Nullable Collection<LineExtensionInfo> getLineExtensions(@NotNull Project project, @NotNull VirtualFile file, int lineNumber) {
        GlobalSettings globalSettings = GlobalSettings.getInstance();

        List<LineExtensionInfo> result = new ArrayList<>();
        if (globalSettings.getLineDocument()) {
            // 根据相对路径获取
            BookmarkTreeNode node = DocumentService.getInstance(project).getBookmarkNode(file, lineNumber);
            if (node != null && node.isBookmark()) {
                // 已经添加书签 则在行末尾追加显示书签内容
                BookmarkNodeModel bookmark = (BookmarkNodeModel) node.getUserObject();
                if (node.getInvalid() || bookmark.getInvalid()) {
                    result.add(new LineExtensionInfo(BookmarkIcons.INVALID_SIGN, new TextAttributes(null, null, BookmarkConstants.WARNING_COLOR, null, Font.BOLD)));
                    result.add(new LineExtensionInfo(":", new TextAttributes(null, null, globalSettings.getSeparatorColor(), null, Font.BOLD)));
                } else {
                    // 书签前缀信息
                    if (StringUtils.isNotBlank(globalSettings.getPrefix())) {
                        result.add(new LineExtensionInfo(globalSettings.getPrefix(), new TextAttributes(null, null, globalSettings.getPrefixColor(), null, Font.BOLD)));
                        result.add(new LineExtensionInfo(":", new TextAttributes(null, null, globalSettings.getSeparatorColor(), null, Font.BOLD)));
                    }
                }
                // 书签显示内容
                result.add(new LineExtensionInfo(getLineText(bookmark.getName(), bookmark.getDesc(), globalSettings), new TextAttributes(null, null, globalSettings.getContentColor(), null, Font.BOLD)));
            }
        }
        return result;
    }

    /**
     * 获取行文本
     *
     * @param name           名字
     * @param description    描述
     * @param globalSettings
     * @return {@link String}
     */
    private String getLineText(String name, String description, GlobalSettings globalSettings) {
        String showMessage = "";
        if (StringUtils.isNotBlank(name)) {
            showMessage += name;
        }
        if (StringUtils.isNotBlank(showMessage) && StringUtils.isNotBlank(description)) {
            showMessage = showMessage + BookmarkConstants.BOOKMARK_NAME_AND_DESC_SEPARATOR + description;
        }
        return StringUtils.abbreviate(showMessage, "...", globalSettings.getShowNum());
    }
}
