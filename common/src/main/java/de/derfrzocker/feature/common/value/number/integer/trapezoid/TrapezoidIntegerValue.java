package de.derfrzocker.feature.common.value.number.integer.trapezoid;

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

public class TrapezoidIntegerValue extends IntegerValue {

    private IntegerValue minInclusive;
    private IntegerValue maxInclusive;
    private IntegerValue plateau;
    private boolean dirty = false;

    public TrapezoidIntegerValue(IntegerValue minInclusive, IntegerValue maxInclusive, IntegerValue plateau) {
        this.minInclusive = minInclusive;
        this.maxInclusive = maxInclusive;
        this.plateau = plateau;
    }

    @Override
    public TrapezoidIntegerType getValueType() {
        return TrapezoidIntegerType.type();
    }

    @Override
    public Integer getValue(@NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull BlockVector position, @NotNull LimitedRegion limitedRegion) {
        int min = minInclusive == null ? 0 : minInclusive.getValue(worldInfo, random, position, limitedRegion);
        int max = maxInclusive == null ? 0 : maxInclusive.getValue(worldInfo, random, position, limitedRegion);
        int plateau = this.plateau == null ? 0 : this.plateau.getValue(worldInfo, random, position, limitedRegion);

        if (min > max) {
            return min;
        } else {
            int length = max - min;
            if (plateau >= length) {
                return random.nextInt(max - min + 1) + min;
            } else {
                int halfBase = (length - plateau) / 2;
                int restBase = length - halfBase;
                return min + random.nextInt(restBase + 1) + random.nextInt(halfBase + 1);
            }
        }
    }

    @Override
    public boolean isDirty() {
        if (dirty) {
            return true;
        }

        if (minInclusive != null && minInclusive.isDirty()) {
            return true;
        }

        if (maxInclusive != null && maxInclusive.isDirty()) {
            return true;
        }

        return plateau != null && plateau.isDirty();
    }

    @Override
    public void saved() {
        dirty = false;

        if (minInclusive != null) {
            minInclusive.saved();
        }

        if (maxInclusive != null) {
            maxInclusive.saved();
        }

        if (plateau != null) {
            plateau.saved();
        }
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

    public IntegerValue getPlateau() {
        return plateau;
    }

    public void setPlateau(IntegerValue integerValue) {
        integerValue.setValueLocation(getValueLocation());
        this.plateau = integerValue;
        dirty = true;
    }

    @Override
    public TrapezoidIntegerValue clone() {
        return new TrapezoidIntegerValue(minInclusive == null ? null : minInclusive.clone(), maxInclusive == null ? null : maxInclusive.clone(), plateau == null ? null : plateau.clone());
    }

    @Override
    public void setValueLocation(@NotNull ValueLocation valueLocation) {
        super.setValueLocation(valueLocation);
        getPlateau().setValueLocation(valueLocation);
        getMinInclusive().setValueLocation(valueLocation);
        getMaxInclusive().setValueLocation(valueLocation);
    }

    @Override
    public @NotNull List<@NotNull String> traverse(@NotNull StringFormatter formatter, int depth, @NotNull TraversKey key) {
        return MessageTraversUtil.multiple(formatter, depth, key, TraversKey.ofValueType(getValueType().getKey()),
                new Pair<>("plateau", getPlateau()), new Pair<>("min-inclusive", getMinInclusive()),
                new Pair<>("max-inclusive", getMaxInclusive()));
    }
}
