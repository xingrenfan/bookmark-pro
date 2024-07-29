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
import org.bookmark.pro.context.BookmarkRunService;
import org.bookmark.pro.domain.model.BookmarkNodeModel;
import org.bookmark.pro.service.tree.handler.BookmarkTreeNode;
import org.bookmark.pro.utils.BookmarkNoticeUtil;
import org.bookmark.pro.utils.CollectionUtil;
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
            Set<BookmarkTreeNode> bookmarkNodes = BookmarkRunService.getDocumentService(project).getBookmarkNodes(project, virtualFile);
            if (CollectionUtil.isEmpty(bookmarkNodes)) {
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
            if ((newLineCount <= 0 && oldLineCount <= 0) || newLineCount == oldLineCount) {
                return;
            }
            // 计算行变化
            int offset = event.getOffset();
            // 变化所在行
            int startLineNumber = 0;
            int endLineNumber = 0;
            boolean isAdd = true;
            if (newLineCount > oldLineCount) {
                isAdd = true;
                startLineNumber = document.getLineNumber(offset) + 1;
                endLineNumber = startLineNumber + newLineCount - oldLineCount;
            } else {
                isAdd = false;
                endLineNumber = document.getLineNumber(offset) + 1;
                startLineNumber = endLineNumber - oldLineCount + newLineCount;
            }
            LineRange lineRange = new LineRange(startLineNumber, endLineNumber);
            // 修复剩余书签
            perceivedLineChange(project, virtualFile, bookmarkNodes, lineRange, document.getLineNumber(offset), oldLineCount, isAdd);
        } catch (Exception e) {
            BookmarkNoticeUtil.errorMessages(project, "Change bookmark mark line fail[" + e.getMessage() + "]");
        }
    }

    /**
     * 感知到线路变化
     *
     * @param project        项目
     * @param virtualFile    虚拟文件
     * @param bookmarkNodes  为节点添加书签
     * @param lineRange      线范围
     * @param deleteStartNum 删除起始行
     * @param deleteTotalNum 删除内容总行数
     * @param isAddLine      是添加
     */
    private void perceivedLineChange(Project project, VirtualFile virtualFile, Set<BookmarkTreeNode> bookmarkNodes, LineRange lineRange, int deleteStartNum, int deleteTotalNum, boolean isAddLine) {
        for (BookmarkTreeNode node : bookmarkNodes) {
            BookmarkNodeModel nodeModel = (BookmarkNodeModel) node.getUserObject();
            // 书签标记行号
            int bookmarkPositionLine = nodeModel.getLine();
            int rowGap = lineRange.end - lineRange.start;
            int changeLine = lineRange.start;
            if (isAddLine) {
                // 添加行
                if (bookmarkPositionLine + 1 < changeLine) {
                    continue;
                }
                // 移除缓存的旧书签
                BookmarkRunService.getDocumentService(project).removeBookmarkNode(project, node);
                nodeModel.setLine(bookmarkPositionLine + rowGap);
                nodeModel.setVirtualFile(virtualFile);
                // 添加新书签
                BookmarkRunService.getDocumentService(project).addBookmarkNode(project, node);
                node.setUserObject(nodeModel);
                BookmarkRunService.getBookmarkManage(project).getBookmarkTree().getModel().nodeChanged(node);
            } else {
                if (bookmarkPositionLine <= changeLine) {
                    continue;
                }
                // 移除缓存的旧书签
                BookmarkRunService.getDocumentService(project).removeBookmarkNode(project, node);
                if (skipAddBookmark(bookmarkPositionLine, lineRange.start, lineRange.end, deleteStartNum, deleteTotalNum, bookmarkPositionLine)) {
                    if (deleteManageBookmark(deleteStartNum, deleteTotalNum, bookmarkPositionLine)) {
                        // 管理器中删除书签
                        BookmarkRunService.getBookmarkManage(project).removeBookmarkNode(node);
                    }
//                    continue;
                }
                nodeModel.setLine(bookmarkPositionLine - rowGap);
                nodeModel.setVirtualFile(virtualFile);
                // 添加新书签
                BookmarkRunService.getDocumentService(project).addBookmarkNode(project, node);
                node.setUserObject(nodeModel);
                BookmarkRunService.getBookmarkManage(project).addBookmarkNode(node);
                BookmarkRunService.getBookmarkManage(project).getBookmarkTree().getModel().nodeChanged(node);
            }
            BookmarkRunService.getPersistenceService(project).saveBookmark(project);
        }
    }

    /**
     * 跳过添加书签
     *
     * @param positionLine    位置线
     * @param start           开始
     * @param end             结束
     * @param deleteStartNum  删除起始行
     * @param deleteTotalNum  删除总行数
     * @param bookmarkLineNum 书签行号
     * @return boolean
     */
    private boolean skipAddBookmark(int positionLine, int start, int end, int deleteStartNum, int deleteTotalNum, int bookmarkLineNum) {
        int deleteContentEndLineNum = deleteStartNum + deleteTotalNum;
        return (positionLine < end && positionLine > start) || (bookmarkLineNum >= deleteStartNum && bookmarkLineNum <= deleteContentEndLineNum);
    }

    private boolean deleteManageBookmark(int deleteStartNum, int deleteTotalNum, int bookmarkLineNum) {
        int deleteContentEndLineNum = deleteStartNum + deleteTotalNum;
        return bookmarkLineNum >= deleteStartNum && bookmarkLineNum <= deleteContentEndLineNum;
    }

    private Editor getEditor(Document document) {
        Editor[] editors = EditorFactoryImpl.getInstance().getEditors(document);
        if (editors.length >= 1) {
            return editors[0];
        }
        return null;
    }

}
