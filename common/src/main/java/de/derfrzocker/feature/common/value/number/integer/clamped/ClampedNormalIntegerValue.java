package de.derfrzocker.feature.common.value.number.integer.clamped;

import de.derfrzocker.feature.api.ValueLocation;
import de.derfrzocker.feature.api.util.traverser.message.StringFormatter;
import de.derfrzocker.feature.api.util.traverser.message.TraversKey;
import de.derfrzocker.feature.common.util.MessageTraversUtil;
import de.derfrzocker.feature.common.value.number.FloatValue;
import de.derfrzocker.feature.common.value.number.IntegerValue;
import de.derfrzocker.spigot.utils.Pair;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

public class ClampedNormalIntegerValue extends IntegerValue {

    private FloatValue mean;
    private FloatValue deviation;
    private IntegerValue minInclusive;
    private IntegerValue maxInclusive;
    private boolean dirty = false;

    public ClampedNormalIntegerValue(FloatValue mean, FloatValue deviation, IntegerValue minInclusive, IntegerValue maxInclusive) {
        this.mean = mean;
        this.deviation = deviation;
        this.minInclusive = minInclusive;
        this.maxInclusive = maxInclusive;
    }

    @Override
    public ClampedIntegerType getValueType() {
        return ClampedIntegerType.type();
    }

    @Override
    public Integer getValue(@NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull BlockVector position, @NotNull LimitedRegion limitedRegion) {
        float mean = this.mean == null ? 0 : this.mean.getValue(worldInfo, random, position, limitedRegion);
        float deviation = this.deviation == null ? 0 : this.deviation.getValue(worldInfo, random, position, limitedRegion);
        int min = minInclusive == null ? 0 : minInclusive.getValue(worldInfo, random, position, limitedRegion);
        int max = maxInclusive == null ? 0 : maxInclusive.getValue(worldInfo, random, position, limitedRegion);

        float value = mean + (float) random.nextGaussian() * deviation;

        if (value < min) {
            return min;
        } else {
            return (int) Math.min(value, max);
        }
    }

    @Override
    public boolean isDirty() {
        if (dirty) {
            return true;
        }

        if (mean != null && mean.isDirty()) {
            return true;
        }

        if (deviation != null && deviation.isDirty()) {
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

        if (mean != null) {
            mean.saved();
        }

        if (deviation != null) {
            deviation.saved();
        }

        if (minInclusive != null) {
            minInclusive.saved();
        }

        if (maxInclusive != null) {
            maxInclusive.saved();
        }
    }

    public FloatValue getMean() {
        return mean;
    }

    public void setMean(FloatValue mean) {
        mean.setValueLocation(getValueLocation());
        this.mean = mean;
        dirty = true;
    }

    public FloatValue getDeviation() {
        return deviation;
    }

    public void setDeviation(FloatValue deviation) {
        deviation.setValueLocation(getValueLocation());
        this.deviation = deviation;
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
    public ClampedNormalIntegerValue clone() {
        return new ClampedNormalIntegerValue(mean == null ? null : mean.clone(), deviation == null ? null : deviation.clone(), minInclusive == null ? null : minInclusive.clone(), maxInclusive == null ? null : maxInclusive.clone());
    }

    @Override
    public void setValueLocation(@NotNull ValueLocation valueLocation) {
        super.setValueLocation(valueLocation);
        getMean().setValueLocation(valueLocation);
        getDeviation().setValueLocation(valueLocation);
        getMinInclusive().setValueLocation(valueLocation);
        getMaxInclusive().setValueLocation(valueLocation);
    }

    @Override
    public @NotNull List<@NotNull String> traverse(@NotNull StringFormatter formatter, int depth, @NotNull TraversKey key) {
        return MessageTraversUtil.multiple(formatter, depth, key, TraversKey.ofValueType(getValueType().getKey()),
                new Pair<>("mean", getMean()), new Pair<>("deviation", getDeviation()),
                new Pair<>("min-inclusive", getMinInclusive()), new Pair<>("max-inclusive", getMaxInclusive()));
    }
}
