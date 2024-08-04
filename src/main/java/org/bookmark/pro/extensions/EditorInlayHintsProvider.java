package org.bookmark.pro.extensions;

import com.intellij.codeInsight.hints.declarative.InlayHintsCollector;
import com.intellij.codeInsight.hints.declarative.InlayHintsProvider;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EditorInlayHintsProvider implements InlayHintsProvider {
    @Nullable
    @Override
    public InlayHintsCollector createCollector(@NotNull PsiFile psiFile, @NotNull Editor editor) {
        return null;
    }
}
