package de.derfrzocker.feature.api.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface MessageTraversAble {

    List<String> traverse(StringFormatter formatter, int depth, String key);

    @FunctionalInterface
    interface StringFormatter {

        @NotNull
        String format(int depth, @NotNull String key, @Nullable Object value);
    }
}
