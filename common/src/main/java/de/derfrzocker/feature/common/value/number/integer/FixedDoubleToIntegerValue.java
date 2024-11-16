package de.derfrzocker.feature.common.value.number.integer;

import de.derfrzocker.feature.api.util.traverser.message.StringFormatter;
import de.derfrzocker.feature.api.util.traverser.message.TraversKey;
import de.derfrzocker.feature.common.util.MessageTraversUtil;
import de.derfrzocker.feature.common.value.number.IntegerValue;
import de.derfrzocker.spigot.utils.NumberUtil;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

public class FixedDoubleToIntegerValue extends IntegerValue {

    private double value;
    private boolean dirty = false;

    public FixedDoubleToIntegerValue(double value) {
        this.value = value;
    }

    @Override
    public FixedDoubleToIntegerType getValueType() {
        return FixedDoubleToIntegerType.INSTANCE;
    }

    @Override
    public Integer getValue(@NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull BlockVector position, @NotNull LimitedRegion limitedRegion) {
        return NumberUtil.getInt(value, random);
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    @Override
    public void saved() {
        dirty = false;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
        dirty = true;
    }

    @Override
    public FixedDoubleToIntegerValue clone() {
        return new FixedDoubleToIntegerValue(value);
    }

    @Override
    public @NotNull List<@NotNull String> traverse(@NotNull StringFormatter formatter, int depth, @NotNull TraversKey key) {
        return MessageTraversUtil.single(formatter, depth, key, getValue());
    }
}
