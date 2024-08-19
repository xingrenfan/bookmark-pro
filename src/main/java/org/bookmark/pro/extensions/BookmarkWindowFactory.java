package org.bookmark.pro.extensions;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.bookmark.pro.actions.BookmarkExportAction;
import org.bookmark.pro.actions.BookmarkHelpAction;
import org.bookmark.pro.actions.BookmarkImportAction;
import org.bookmark.pro.actions.BookmarkIssueAction;
import org.bookmark.pro.windows.BookmarkPanel;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;


/**
 * 书签窗口工厂
 *
 * @author Lyon
 * @date 2024/03/21
 */
public class BookmarkWindowFactory implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        initTopMenus(toolWindow);
        ContentFactory contentFactory = ContentFactory.getInstance();
        Content regularRetention = contentFactory.createContent(BookmarkPanel.getInstance(project), null, false);
        toolWindow.getContentManager().addContent(regularRetention);
    }

    /**
     * 初始化顶部菜单
     * 增加：导入、导出、帮助
     *
     * @param toolWindow 工具窗口
     */
    private void initTopMenus(ToolWindow toolWindow) {
        BookmarkExportAction exportAction = new BookmarkExportAction();
        BookmarkImportAction importAction = new BookmarkImportAction();
        BookmarkIssueAction issueAction = new BookmarkIssueAction();
        BookmarkHelpAction helpAction = new BookmarkHelpAction();
        // 在 ToolWindow 的标题栏中添加自定义动作按钮
        toolWindow.setTitleActions(Arrays.asList(importAction, exportAction, issueAction, helpAction));
    }

}
