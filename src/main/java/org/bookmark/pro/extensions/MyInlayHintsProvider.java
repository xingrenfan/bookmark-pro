package org.bookmark.pro.extensions;

import com.intellij.codeInsight.hints.InlayParameterHintsProvider;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class MyInlayHintsProvider implements InlayParameterHintsProvider {
    @Override
    public @NotNull Set<String> getDefaultBlackList() {
        return Set.of();
    }
}
