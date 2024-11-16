package de.derfrzocker.feature.common.value.number.integer;

import de.derfrzocker.feature.api.util.traverser.message.StringFormatter;
import de.derfrzocker.feature.api.util.traverser.message.TraversKey;
import de.derfrzocker.feature.common.util.MessageTraversUtil;
import de.derfrzocker.feature.common.value.number.IntegerValue;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

public class FixedIntegerValue extends IntegerValue {

    private int value;
    private boolean dirty = false;

    public FixedIntegerValue(int value) {
        this.value = value;
    }

    @Override
    public FixedIntegerType getValueType() {
        return FixedIntegerType.INSTANCE;
    }

    @Override
    public Integer getValue(@NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull BlockVector position, @NotNull LimitedRegion limitedRegion) {
        return value;
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    @Override
    public void saved() {
        dirty = false;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
        dirty = true;
    }

    @Override
    public FixedIntegerValue clone() {
        return new FixedIntegerValue(value);
    }

    @Override
    public @NotNull List<@NotNull String> traverse(@NotNull StringFormatter formatter, int depth, @NotNull TraversKey key) {
        return MessageTraversUtil.single(formatter, depth, key, getValue());
    }
}
