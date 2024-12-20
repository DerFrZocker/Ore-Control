package de.derfrzocker.feature.common.value.number.integer.clamped;

import de.derfrzocker.feature.api.ValueLocation;
import de.derfrzocker.feature.api.util.traverser.message.StringFormatter;
import de.derfrzocker.feature.api.util.traverser.message.TraversKey;
import de.derfrzocker.feature.common.util.MessageTraversUtil;
import de.derfrzocker.feature.common.value.number.IntegerValue;
import de.derfrzocker.spigot.utils.Pair;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

public class ClampedIntegerValue extends IntegerValue {

    private IntegerValue source;
    private IntegerValue minInclusive;
    private IntegerValue maxInclusive;
    private boolean dirty = false;

    public ClampedIntegerValue(IntegerValue source, IntegerValue minInclusive, IntegerValue maxInclusive) {
        this.source = source;
        this.minInclusive = minInclusive;
        this.maxInclusive = maxInclusive;
    }

    @Override
    public ClampedIntegerType getValueType() {
        return ClampedIntegerType.type();
    }

    @Override
    public Integer getValue(@NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull BlockVector position, @NotNull LimitedRegion limitedRegion) {
        int source = this.source == null ? 0 : this.source.getValue(worldInfo, random, position, limitedRegion);
        int min = minInclusive == null ? 0 : minInclusive.getValue(worldInfo, random, position, limitedRegion);
        int max = maxInclusive == null ? 0 : maxInclusive.getValue(worldInfo, random, position, limitedRegion);

        if (source < min) {
            return min;
        } else {
            return Math.min(source, max);
        }
    }

    @Override
    public boolean isDirty() {
        if (dirty) {
            return true;
        }

        if (source != null && source.isDirty()) {
            return true;
        }

        if (minInclusive != null && minInclusive.isDirty()) {
            return true;
        }

        return maxInclusive != null && maxInclusive.isDirty();
    }

    @Override
    public void saved() {
        dirty = false;

        if (source != null) {
            source.saved();
        }

        if (minInclusive != null) {
            minInclusive.saved();
        }

        if (maxInclusive != null) {
            maxInclusive.saved();
        }
    }

    public IntegerValue getSource() {
        return source;
    }

    public void setSource(IntegerValue source) {
        source.setValueLocation(getValueLocation());
        this.source = source;
        dirty = true;
    }

    public IntegerValue getMinInclusive() {
        return minInclusive;
    }

    public void setMinInclusive(IntegerValue integerValue) {
        integerValue.setValueLocation(getValueLocation());
        this.minInclusive = integerValue;
        dirty = true;
    }

    public IntegerValue getMaxInclusive() {
        return maxInclusive;
    }

    public void setMaxInclusive(IntegerValue integerValue) {
        integerValue.setValueLocation(getValueLocation());
        this.maxInclusive = integerValue;
        dirty = true;
    }

    @Override
    public ClampedIntegerValue clone() {
        return new ClampedIntegerValue(source == null ? null : source.clone(), minInclusive == null ? null : minInclusive.clone(), maxInclusive == null ? null : maxInclusive.clone());
    }

    @Override
    public void setValueLocation(@NotNull ValueLocation valueLocation) {
        super.setValueLocation(valueLocation);
        getSource().setValueLocation(valueLocation);
        getMinInclusive().setValueLocation(valueLocation);
        getMaxInclusive().setValueLocation(valueLocation);
    }

    @Override
    public @NotNull List<@NotNull String> traverse(@NotNull StringFormatter formatter, int depth, @NotNull TraversKey key) {
        return MessageTraversUtil.multiple(formatter, depth, key, TraversKey.ofValueType(getValueType().getKey()),
                new Pair<>("source", getSource()), new Pair<>("min-inclusive", getMinInclusive()),
                new Pair<>("max-inclusive", getMaxInclusive()));
    }
}
