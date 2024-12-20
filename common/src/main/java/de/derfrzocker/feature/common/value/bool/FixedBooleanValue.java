package de.derfrzocker.feature.common.value.bool;

import de.derfrzocker.feature.api.util.traverser.message.StringFormatter;
import de.derfrzocker.feature.api.util.traverser.message.TraversKey;
import de.derfrzocker.feature.common.util.MessageTraversUtil;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

public class FixedBooleanValue extends BooleanValue {

    private boolean value;
    private boolean dirty = false;

    public FixedBooleanValue(boolean value) {
        this.value = value;
    }

    @Override
    public FixedBooleanType getValueType() {
        return FixedBooleanType.INSTANCE;
    }

    @Override
    public Boolean getValue(@NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull BlockVector position, @NotNull LimitedRegion limitedRegion) {
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

    public boolean getValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
        dirty = true;
    }

    @Override
    public FixedBooleanValue clone() {
        return new FixedBooleanValue(value);
    }

    @Override
    public @NotNull List<@NotNull String> traverse(@NotNull StringFormatter formatter, int depth, @NotNull TraversKey key) {
        return MessageTraversUtil.single(formatter, depth, key, getValue());
    }
}
