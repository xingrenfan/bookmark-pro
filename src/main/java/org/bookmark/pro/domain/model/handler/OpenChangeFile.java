package org.bookmark.pro.domain.model.handler;

import com.intellij.ide.FileSelectInContext;
import com.intellij.ide.SelectInContext;
import com.intellij.ide.SelectInManager;
import com.intellij.ide.SelectInTarget;
import com.intellij.openapi.fileEditor.FileNavigator;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.fileTypes.INativeFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.bookmark.pro.utils.BookmarkNoticeUtil;
import org.jetbrains.annotations.NotNull;

/**
 * 打开更改文件（删除又重新恢复的）
 *
 * @author Lyon
 * @date 2024/04/26
 */
public class OpenChangeFile extends OpenFileDescriptor {

    public OpenChangeFile(@NotNull Project project, @NotNull VirtualFile file, int logicalLine, int logicalColumn) {
        super(project, file, logicalLine, logicalColumn);
    }

    @Override
    public void navigate(boolean requestFocus) {
        BookmarkFileNavigator fileNavigator = new BookmarkFileNavigator();
        fileNavigator.navigate(this, requestFocus);
    }

    class BookmarkFileNavigator implements FileNavigator {
        @Override
        public void navigate(@NotNull OpenFileDescriptor descriptor, boolean requestFocus) {
            if (!descriptor.getFile().isDirectory()) {
                if (navigateInEditorOrNativeApp(descriptor.getProject(), descriptor, requestFocus)) return;
            }

            if (navigateInProjectView(descriptor.getProject(), descriptor.getFile(), requestFocus)) return;

            BookmarkNoticeUtil.errorMessages(descriptor.getProject(), "Files of this type cannot be opened");
        }

        @Override
        public boolean navigateInEditor(@NotNull OpenFileDescriptor descriptor, boolean requestFocus) {
            return true;
        }

        private boolean navigateInEditorOrNativeApp(Project project, OpenFileDescriptor descriptor, boolean requestFocus) {
            try {
                if (!descriptor.getFile().isValid()) return false;
                FileType type = FileTypeManager.getInstance().getKnownFileTypeOrAssociate(descriptor.getFile(), descriptor.getProject());
                if (type == null) return false;

                if (type instanceof INativeFileType) {
                    return ((INativeFileType) type).openFileInAssociatedApplication(descriptor.getProject(), descriptor.getFile());
                }

                return navigateInEditor(descriptor, requestFocus);
            } catch (Exception e) {
                BookmarkNoticeUtil.errorMessages(project, "File invalid, reason: File deleted, invalidated VFP during update");
            }
            return false;
        }

        private boolean navigateInProjectView(@NotNull Project project, @NotNull VirtualFile file, boolean requestFocus) {
            SelectInContext context = new FileSelectInContext(project, file, null);
            for (SelectInTarget target : SelectInManager.getInstance(project).getTargetList()) {
                if (context.selectIn(target, requestFocus)) {
                    BookmarkNoticeUtil.warningMessages(project, "Bookmark file changed, You need to restart the IDEA editor to open.");
                    return true;
                }
            }
            return false;
        }
    }
}
