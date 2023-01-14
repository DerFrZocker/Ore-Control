/*
 * MIT License
 *
 * Copyright (c) 2019 - 2022 Marvin (DerFrZocker)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package de.derfrzocker.feature.common.value.number.integer.biased;

import de.derfrzocker.feature.api.ValueLocation;
import de.derfrzocker.feature.common.util.MessageTraversUtil;
import de.derfrzocker.feature.common.value.number.IntegerValue;
import de.derfrzocker.spigot.utils.Pair;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

public class BiasedToBottomIntegerValue extends IntegerValue {

    private IntegerValue minInclusive;
    private IntegerValue maxInclusive;
    private boolean dirty = false;

    public BiasedToBottomIntegerValue(IntegerValue minInclusive, IntegerValue maxInclusive) {
        this.minInclusive = minInclusive;
        this.maxInclusive = maxInclusive;
    }

    @Override
    public BiasedToBottomIntegerType getValueType() {
        return BiasedToBottomIntegerType.type();
    }

    @Override
    public Integer getValue(@NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull BlockVector position, @NotNull LimitedRegion limitedRegion) {
        int min = minInclusive == null ? 0 : minInclusive.getValue(worldInfo, random, position, limitedRegion);
        int max = maxInclusive == null ? 0 : maxInclusive.getValue(worldInfo, random, position, limitedRegion);

        return min + random.nextInt(random.nextInt(max - min + 1) + 1);
    }

    @Override
    public boolean isDirty() {
        if (dirty) {
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

        if (minInclusive != null) {
            minInclusive.saved();
        }

        if (maxInclusive != null) {
            maxInclusive.saved();
        }
    }

    @Override
    public BiasedToBottomIntegerValue clone() {
        return new BiasedToBottomIntegerValue(minInclusive == null ? null : minInclusive.clone(), maxInclusive == null ? null : maxInclusive.clone());
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
    public void setValueLocation(@NotNull ValueLocation valueLocation) {
        super.setValueLocation(valueLocation);
        getMinInclusive().setValueLocation(valueLocation);
        getMaxInclusive().setValueLocation(valueLocation);
    }

    @Override
    public List<String> traverse(StringFormatter formatter, int depth, String key) {
        return MessageTraversUtil.multiple(formatter, depth, key,
                new Pair<>("min-inclusive", getMinInclusive()), new Pair<>("max-inclusive", getMaxInclusive()));
    }
}
