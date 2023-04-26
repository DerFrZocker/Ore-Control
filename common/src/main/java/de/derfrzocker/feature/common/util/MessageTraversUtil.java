package de.derfrzocker.feature.common.util;

import de.derfrzocker.feature.api.Value;
import de.derfrzocker.feature.api.util.MessageTraversAble;
import de.derfrzocker.spigot.utils.Pair;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public final class MessageTraversUtil {

    private MessageTraversUtil() {
    }

    public static List<String> single(MessageTraversAble.StringFormatter formatter, int depth, String key, Object value) {
        return Collections.singletonList(formatter.format(depth, key, value));
    }

    @SafeVarargs
    public static List<String> multiple(MessageTraversAble.StringFormatter formatter, int depth, String key, Pair<String, MessageTraversAble>... values) {
        List<String> result = new LinkedList<>();

        result.add(formatter.format(depth, key, null));

        for (Pair<String, MessageTraversAble> pair : values) {
            int nextDepth = depth + 1;
            result.addAll(pair.getSecond().traverse(formatter, nextDepth, pair.getFirst()));
        }

        return result;
    }
}
