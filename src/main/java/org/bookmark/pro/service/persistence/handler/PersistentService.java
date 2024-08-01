package org.bookmark.pro.service.persistence.handler;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import org.bookmark.pro.domain.BookmarkPro;
import org.jetbrains.annotations.NotNull;

/**
 * 书签持久化
 *
 * @author Nonoas
 * @date 2023/6/5
 */
@State(name = "SuperBookmarkState", storages = {@Storage("SuperBookmarkState.xml")})
public class PersistentService implements PersistentStateComponent<BookmarkPro> {
    private BookmarkPro state;

    private final Project project;

    public PersistentService(Project project) {
        this.project = project;
    }

    public void setState(BookmarkPro state) {
        if (state == null) {
            throw new RuntimeException("书签为空,保存异常!");
        }
        if (state.getChildren() == null) {
            return;
        }
        this.state = state;
    }

    @Override
    public @NotNull BookmarkPro getState() {
        if (state == null) {
            state = new BookmarkPro();
            state.setBookmark(false);
        }
        // root节点名称
        state.setName(project.getName());
        return state;
    }

    @Override
    public void loadState(@NotNull BookmarkPro state) {
        if (state == null) {
            throw new RuntimeException("书签为空,保存异常!");
        }
        if (state.getChildren() == null) {
            return;
        }
        // 当持久化组件 PersistentStateComponent 创建的时候或持久化Xml文件发生变化的时候，会调用 loadState() 方法（仅当组件存在一些非默认持久化数据时）
        this.state = state;
    }
}
