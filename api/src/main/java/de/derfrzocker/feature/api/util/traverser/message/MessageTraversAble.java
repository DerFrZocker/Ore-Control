package de.derfrzocker.feature.api.util.traverser.message;

import org.jetbrains.annotations.NotNull;

import java.util.List;

@FunctionalInterface
public interface MessageTraversAble {

    @NotNull
    List<@NotNull String> traverse(@NotNull StringFormatter formatter, int depth, @NotNull TraversKey key);
}
