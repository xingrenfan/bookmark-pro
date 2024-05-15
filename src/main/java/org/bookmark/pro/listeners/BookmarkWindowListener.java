package org.bookmark.pro.listeners;

import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.ex.ToolWindowManagerListener;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

/**
 * 书签窗口侦听器
 *
 * @author Lyon
 * @date 2024/03/21
 */
public class BookmarkWindowListener implements ToolWindowManagerListener {
    @Override
    public void toolWindowsRegistered(@NotNull List<String> ids, @NotNull ToolWindowManager toolWindowManager) {
        ToolWindowManagerListener.super.toolWindowsRegistered(ids, toolWindowManager);
        // 插件窗口首次启动被注册到 IDE 中时，因为只注册一次，因此该方法在 IDE 启动后只会被调用一次
    }

    @Override
    public void toolWindowShown(@NotNull ToolWindow toolWindow) {
        ToolWindowManagerListener.super.toolWindowShown(toolWindow);
        // 插件工具栏窗口每次被点击打开时，都会被调用
    }

    @Override
    public void stateChanged(@NotNull ToolWindowManager toolWindowManager) {
        Set<String> toolWindowIdSet = toolWindowManager.getToolWindowIdSet();
        toolWindowIdSet.forEach(System.out::println);
    }
}