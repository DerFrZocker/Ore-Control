package de.derfrzocker.ore.control.traverser;

import de.derfrzocker.feature.api.util.traverser.message.StringFormatter;
import de.derfrzocker.feature.api.util.traverser.message.TraversKey;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BasicStringFormatter implements StringFormatter {

    private final String keyPrefix;

    public BasicStringFormatter(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }

    @Override
    public @Nullable String format(int depth, @NotNull TraversKey key, @Nullable Object value) {
        StringBuilder builder = new StringBuilder();

        builder.append(" ".repeat(Math.max(0, depth)));

        if (depth == 4) {
            return builder.append("§r§7[...]").toString();
        }

        if (depth > 4) {
            return null;
        }

        builder.append("§r§7>§r§f ");
        builder.append(keyPrefix);

        switch (key.keyType()) {
            case SETTING -> {
                builder.append(getTranslationSettingKey((String) key.key()));
            }
            case VALUE_TYPE -> {
                builder.append(getTranslationValueTypeKey((NamespacedKey) key.key()));
            }
            case VALUE_SETTING -> {
                builder.append(getTranslationValueSettingKey((String) key.key()));
            }
            case RULE_TEST -> {
                builder.append(getTranslationRuleTest((NamespacedKey) key.key()));
            }
        }

        builder.append(":§r§f");
        if (value != null) {
            builder.append(" ");
            builder.append(value);
        }

        return builder.toString();
    }

    private String getTranslationSettingKey(String value) {
        return "%%translation:[" + "settings." + value + ".name]%";
    }

    private String getTranslationValueTypeKey(NamespacedKey value) {
        return "%%translation:[" + "value-types." + value.getNamespace() + "." + value.getKey() + ".name]%";
    }

    private String getTranslationValueSettingKey(String value) {
        return "%%translation:[" + "value-settings." + value + ".name]%";
    }

    private String getTranslationRuleTest(NamespacedKey value) {
        return "%%translation:[" + "rule-tests." + value.getNamespace() + "." + value.getKey() + ".name]%";
    }
}
