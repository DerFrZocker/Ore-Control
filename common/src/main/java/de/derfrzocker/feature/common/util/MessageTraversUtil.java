package de.derfrzocker.feature.common.util;

import de.derfrzocker.feature.api.util.traverser.message.MessageTraversAble;
import de.derfrzocker.feature.api.util.traverser.message.StringFormatter;
import de.derfrzocker.feature.api.util.traverser.message.TraversKey;
import de.derfrzocker.spigot.utils.Pair;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public final class MessageTraversUtil {

    private MessageTraversUtil() {
    }

    public static MessageTraversAble asTraversAble(@NotNull Object value) {
        return (formatter, depth, key) -> single(formatter, depth, key, value);
    }

    @NotNull
    public static List<@NotNull String> single(@NotNull StringFormatter formatter, int depth, @NotNull TraversKey key, @Nullable Object value) {
        String result = formatter.format(depth, key, value);

        if (result == null) {
            return Collections.emptyList();
        }

        return Collections.singletonList(result);
    }

    @NotNull
    @SafeVarargs
    public static List<@NotNull String> multiple(@NotNull StringFormatter formatter, int depth, @NotNull TraversKey key, @NotNull TraversKey valueTypeKey, @NotNull Pair<@NotNull String, @NotNull MessageTraversAble>... values) {
        List<String> result = new LinkedList<>();

        addIfNotNull(result, formatter.format(depth, key, null));
        addIfNotNull(result, formatter.format(depth + 1, valueTypeKey, null));

        for (Pair<String, MessageTraversAble> pair : values) {
            int nextDepth = depth + 2;
            result.addAll(pair.getSecond().traverse(formatter, nextDepth, TraversKey.ofValueSetting(pair.getFirst())));
        }

        return result;
    }


    public static void addIfNotNull(List<String> list, @Nullable String element) {
        if (element == null) {
            return;
        }

        list.add(element);
    }
}
