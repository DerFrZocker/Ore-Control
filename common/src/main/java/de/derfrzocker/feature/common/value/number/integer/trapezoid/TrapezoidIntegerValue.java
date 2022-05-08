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

package de.derfrzocker.feature.common.value.number.integer.trapezoid;

import de.derfrzocker.feature.common.value.number.IntegerValue;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;

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

    public IntegerValue getPlateau() {
        return plateau;
    }

    public void setPlateau(IntegerValue integerValue) {
        this.plateau = integerValue;
        dirty = true;
    }

    @Override
    public TrapezoidIntegerValue clone() {
        return new TrapezoidIntegerValue(minInclusive == null ? null : minInclusive.clone(), maxInclusive == null ? null : maxInclusive.clone(), plateau == null ? null : plateau.clone());
    }
}
