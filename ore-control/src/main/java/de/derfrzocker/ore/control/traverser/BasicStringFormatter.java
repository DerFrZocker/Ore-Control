package de.derfrzocker.ore.control.traverser;

import de.derfrzocker.feature.api.util.MessageTraversAble;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BasicStringFormatter implements MessageTraversAble.StringFormatter {

    private final String keyPrefix;

    public BasicStringFormatter(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }

    @Override
    public @NotNull String format(int depth, @NotNull String key, @Nullable Object value) {
        StringBuilder builder = new StringBuilder();

        builder.append(" ".repeat(Math.max(0, depth)));
        builder.append("§r§7>§r§f ");
        builder.append(keyPrefix);

        if (key.startsWith("%%")) {
            builder.append(key);
        } else {
            builder.append(getTranslationValueSettingKey(key));
        }
        builder.append(":§r§f");
        if (value != null) {
            builder.append(" ");
            builder.append(value);
        }

        return builder.toString();
    }

    private String getTranslationValueSettingKey(String value) {
        return "%%translation:[" + "value-settings." + value + ".name]%";
    }
}
