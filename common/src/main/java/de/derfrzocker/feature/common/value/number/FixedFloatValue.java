package de.derfrzocker.feature.common.value.number;

import de.derfrzocker.feature.api.util.traverser.message.StringFormatter;
import de.derfrzocker.feature.api.util.traverser.message.TraversKey;
import de.derfrzocker.feature.common.util.MessageTraversUtil;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

public class FixedFloatValue extends FloatValue {

    private float value;
    private boolean dirty = false;

    public FixedFloatValue(float value) {
        this.value = value;
    }

    @Override
    public FixedFloatType getValueType() {
        return FixedFloatType.INSTANCE;
    }

    @Override
    public Float getValue(@NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull BlockVector position, @NotNull LimitedRegion limitedRegion) {
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

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
        dirty = true;
    }

    @Override
    public FixedFloatValue clone() {
        return new FixedFloatValue(value);
    }

    @Override
    public @NotNull List<@NotNull String> traverse(@NotNull StringFormatter formatter, int depth, @NotNull TraversKey key) {
        return MessageTraversUtil.single(formatter, depth, key, getValue());
    }
}
