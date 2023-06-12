package de.derfrzocker.feature.api.util.traverser.message;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface StringFormatter {

    @Nullable
    String format(int depth, @NotNull TraversKey key, @Nullable Object value);
}
