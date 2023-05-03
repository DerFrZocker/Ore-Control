package de.derfrzocker.feature.api.util.traverser.message;

import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

public record TraversKey(@NotNull KeyType keyType, @NotNull Object key) {

    @NotNull
    public static TraversKey ofSetting(@NotNull String key) {
        return new TraversKey(KeyType.SETTING, key);
    }

    @NotNull
    public static TraversKey ofValueType(@NotNull NamespacedKey key) {
        return new TraversKey(KeyType.VALUE_TYPE, key);
    }

    @NotNull
    public static TraversKey ofValueSetting(@NotNull String key) {
        return new TraversKey(KeyType.VALUE_SETTING, key);
    }

    @NotNull
    public static TraversKey ofRuleTest(@NotNull NamespacedKey key) {
        return new TraversKey(KeyType.RULE_TEST, key);
    }
}
