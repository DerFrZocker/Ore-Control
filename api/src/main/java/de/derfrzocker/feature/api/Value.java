package de.derfrzocker.feature.api;

import de.derfrzocker.feature.api.util.traverser.message.MessageTraversAble;
import de.derfrzocker.feature.api.util.SaveAble;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public interface Value<V extends Value<V, T, O>, T extends ValueType<V, T, O>, O> extends Cloneable, SaveAble, MessageTraversAble, LocatedAble {

    T getValueType();

    O getValue(@NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull BlockVector position, @NotNull LimitedRegion limitedRegion);

    Value<?, ?, ?> clone();
}
