package org.bookmark.pro.listeners;

import com.intellij.diff.util.LineRange;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.editor.impl.EditorFactoryImpl;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.commons.collections.CollectionUtils;
import org.bookmark.pro.domain.model.BookmarkNodeModel;
import org.bookmark.pro.service.base.document.DocumentService;
import org.bookmark.pro.service.base.persistence.PersistService;
import org.bookmark.pro.service.tree.TreeService;
import org.bookmark.pro.service.tree.component.BookmarkTree;
import org.bookmark.pro.service.tree.component.BookmarkTreeNode;
import org.bookmark.pro.utils.BookmarkNoticeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * 书签文档变化监听
 *
 * @author: codeleep, Lyon
 * @modifyDate 2024/04/19
 * @createTime: 2024/03/20 19:44
 */
public class BookmarkDocumentListener implements DocumentListener {
    @Override
    public void beforeDocumentChange(@NotNull DocumentEvent event) {
        Document document = event.getDocument();
        Editor editor = getEditor(document);
        if (editor == null) {
            return;
        }
        Project project = editor.getProject();
        if (project == null) {
            return;
        }
        VirtualFile virtualFile = FileDocumentManager.getInstance().getFile(document);
        if (virtualFile == null) {
            return;
        }

        try {
            // 查询文件中的书签
            Set<BookmarkTreeNode> bookmarkNodes = DocumentService.getInstance(project).getBookmarkNodes(virtualFile);
            if (CollectionUtils.isEmpty(bookmarkNodes)) {
                // 空的直接返回
                return;
            }

            // 获取变化内容
            CharSequence newFragment = event.getNewFragment();
            CharSequence oldFragment = event.getOldFragment();
            // 获取行变化内容
            String newStr = String.valueOf(newFragment);
            String oldStr = String.valueOf(oldFragment);
            int newLineCount = StringUtil.countChars(newStr, '\n');
            int oldLineCount = StringUtil.countChars(oldStr, '\n');
            // 当前行修改
            if ((newLineCount <= 0 && oldLineCount <= 0) || newLineCount == oldLineCount) {
                return;
            }
            int lineGap = newLineCount - oldLineCount;
            boolean isAdd = lineGap > 0;
            // 计算行变化
            int offset = event.getOffset();
            // 变化所在行
            int startLineNumber = document.getLineNumber(offset);
            int endLineNumber = startLineNumber + (isAdd ? lineGap : -lineGap);
            LineRange lineRange = new LineRange(startLineNumber, endLineNumber);
            // 修复剩余书签
            perceivedLineChange(project, document, virtualFile, bookmarkNodes, lineRange, isAdd, offset, getStartOffset(document, startLineNumber));
        } catch (Exception e) {
            BookmarkNoticeUtil.errorMessages(project, "Change bookmark mark line fail[" + e.getMessage() + "]");
        }
    }

    private int getStartOffset(Document document, int lineNumber) {
        int lineStartOffset = document.getLineStartOffset(lineNumber); // 当前行起始偏移量
        int lineEndOffset = document.getLineEndOffset(lineNumber); // 当前行结束偏移量

        for (int i = lineStartOffset; i < lineEndOffset; i++) {
            char c = document.getCharsSequence().charAt(i);
            if (!Character.isWhitespace(c)) {
                return i;
            }
        }
        return 0;
    }

    /**
     * 感知到线路变化
     *
     * @param project       项目
     * @param virtualFile   虚拟文件
     * @param bookmarkNodes 为节点添加书签
     * @param lineRange     线范围
     * @param isAddLine     是添加
     */
    private void perceivedLineChange(Project project, Document document, VirtualFile virtualFile, Set<BookmarkTreeNode> bookmarkNodes, LineRange lineRange, boolean isAddLine, int offset, int startOffset) {
        for (BookmarkTreeNode node : bookmarkNodes) {
            BookmarkNodeModel nodeModel = (BookmarkNodeModel) node.getUserObject();
            int bookmarkPositionLine = nodeModel.getLine();
            if (bookmarkPositionLine < lineRange.start) continue;

            int _startOffset = isAddLine ? startOffset : getStartOffset(document, bookmarkPositionLine);
            if (bookmarkPositionLine == lineRange.start && offset > _startOffset) continue;

            if (!isAddLine && (bookmarkPositionLine == lineRange.start || bookmarkPositionLine < lineRange.end)) {
                // 管理器中删除书签
                TreeService.getInstance(project).removeBookmarkNode(node);
                continue;
            }
            DocumentService documentService = DocumentService.getInstance(project);
            // 移除缓存的旧书签
            documentService.removeBookmarkNode(node);
            int lineGap = lineRange.end - lineRange.start;
            nodeModel.setLine(bookmarkPositionLine + (isAddLine ? lineGap : -lineGap));
            nodeModel.setVirtualFile(virtualFile);
            node.setUserObject(nodeModel);
            documentService.addBookmarkNode(node);
            TreeService.getInstance(project).getBookmarkTree().getModel().nodeChanged(node);
            // 获取书签树
            BookmarkTree bookmarkTree = TreeService.getInstance(project).getBookmarkTree();
            PersistService.getInstance(project).saveBookmark(bookmarkTree);
        }
    }

    private Editor getEditor(Document document) {
        Editor[] editors = EditorFactoryImpl.getInstance().getEditors(document);
        if (editors.length >= 1) {
            return editors[0];
        }
        return null;
    }
}
