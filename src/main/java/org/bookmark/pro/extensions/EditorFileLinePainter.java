package org.bookmark.pro.extensions;

import com.intellij.openapi.editor.EditorLinePainter;
import com.intellij.openapi.editor.LineExtensionInfo;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.bookmark.pro.constants.BookmarkProConstant;
import org.bookmark.pro.constants.BookmarkProIcon;
import org.bookmark.pro.context.BookmarkRunService;
import org.bookmark.pro.domain.model.BookmarkNodeModel;
import org.bookmark.pro.service.tree.handler.BookmarkTreeNode;
import org.bookmark.pro.utils.CharacterUtil;
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
        List<LineExtensionInfo> result = new ArrayList<>();
        if (BookmarkRunService.getBookmarkSettings().getLineDocument()) {
            // 根据相对路径获取
            BookmarkTreeNode node = BookmarkRunService.getDocumentService(project).getBookmarkNode(project, file, lineNumber);
            if (node != null && node.isBookmark()) {
                // 已经添加书签 则在行末尾追加显示书签内容
                BookmarkNodeModel bookmark = (BookmarkNodeModel) node.getUserObject();
                if (node.getInvalid() || bookmark.getInvalid()) {
                    result.add(new LineExtensionInfo(BookmarkProIcon.INVALID_SIGN, new TextAttributes(null, null, BookmarkProConstant.WARNING_COLOR, null, Font.BOLD)));
                    result.add(new LineExtensionInfo(":", new TextAttributes(null, null, BookmarkRunService.getBookmarkSettings().getSeparatorColor(), null, Font.BOLD)));
                } else {
                    // 书签前缀信息
                    String bookmarkPrefix = BookmarkRunService.getBookmarkSettings().getPrefix();
                    if (CharacterUtil.isNotEmpty(bookmarkPrefix)) {
                        result.add(new LineExtensionInfo(BookmarkRunService.getBookmarkSettings().getPrefix(), new TextAttributes(null, null, BookmarkRunService.getBookmarkSettings().getPrefixColor(), null, Font.BOLD)));
                        result.add(new LineExtensionInfo(":", new TextAttributes(null, null, BookmarkRunService.getBookmarkSettings().getSeparatorColor(), null, Font.BOLD)));
                    }
                }
                // 书签显示内容
                result.add(new LineExtensionInfo(getLineText(bookmark.getName(), bookmark.getDesc()), new TextAttributes(null, null, BookmarkRunService.getBookmarkSettings().getContentColor(), null, Font.BOLD)));
            }
        }
        return result;
    }

    /**
     * 获取行文本
     *
     * @param name        名字
     * @param description 描述
     * @return {@link String}
     */
    private String getLineText(String name, String description) {
        String showMessage = "";
        if (!CharacterUtil.isBlank(name)) {
            showMessage += name;
        }
        if (!CharacterUtil.isBlank(showMessage) && !CharacterUtil.isBlank(description)) {
            showMessage = showMessage + BookmarkProConstant.BOOKMARK_NAME_AND_DESC_SEPARATOR + description;
        }
        return CharacterUtil.abbreviate(showMessage, "...", BookmarkRunService.getBookmarkSettings().getShowNum());
    }
}
