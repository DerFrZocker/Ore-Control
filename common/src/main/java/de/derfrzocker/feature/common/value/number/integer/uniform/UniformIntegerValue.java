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

package de.derfrzocker.feature.common.value.number.integer.uniform;

import de.derfrzocker.feature.common.value.number.IntegerValue;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class UniformIntegerValue extends IntegerValue {

    private IntegerValue minInclusive;
    private IntegerValue maxInclusive;
    private boolean dirty = false;

    public UniformIntegerValue(IntegerValue minInclusive, IntegerValue maxInclusive) {
        this.minInclusive = minInclusive;
        this.maxInclusive = maxInclusive;
    }

    @Override
    public UniformIntegerType getValueType() {
        return UniformIntegerType.type();
    }

    @Override
    public Integer getValue(@NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull BlockVector position, @NotNull LimitedRegion limitedRegion) {
        int min = minInclusive == null ? 0 : minInclusive.getValue(worldInfo, random, position, limitedRegion);
        int max = maxInclusive == null ? 0 : maxInclusive.getValue(worldInfo, random, position, limitedRegion);

        return random.nextInt(max - min + 1) + min;
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
    public UniformIntegerValue clone() {
        return new UniformIntegerValue(minInclusive == null ? null : minInclusive.clone(), maxInclusive == null ? null : maxInclusive.clone());
    }

    public IntegerValue getMinInclusive() {
        return minInclusive;
    }

    public void setMinInclusive(IntegerValue integerValue) {
        this.minInclusive = integerValue;
        dirty = true;
    }

    public IntegerValue getMaxInclusive() {
        return maxInclusive;
    }

    public void setMaxInclusive(IntegerValue integerValue) {
        this.maxInclusive = integerValue;
        dirty = true;
    }
}
