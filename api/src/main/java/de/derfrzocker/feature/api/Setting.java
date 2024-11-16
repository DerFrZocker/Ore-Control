package de.derfrzocker.feature.api;

import org.jetbrains.annotations.NotNull;

public record Setting(@NotNull String name, @NotNull Class<?> valueType) {
}
