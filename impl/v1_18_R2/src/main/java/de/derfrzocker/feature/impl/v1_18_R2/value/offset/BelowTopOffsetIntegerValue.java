/*
 * MIT License
 *
 * Copyright (c) 2019 - 2021 Marvin (DerFrZocker)
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

package de.derfrzocker.feature.impl.v1_18_R2.value.offset;

import de.derfrzocker.feature.common.value.number.IntegerValue;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import org.bukkit.craftbukkit.v1_18_R2.generator.CraftLimitedRegion;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class BelowTopOffsetIntegerValue extends IntegerValue {

    private IntegerValue base;
    private boolean dirty = false;

    public BelowTopOffsetIntegerValue(IntegerValue base) {
        this.base = base;
    }

    @Override
    public BelowTopOffsetIntegerType getValueType() {
        return BelowTopOffsetIntegerType.type();
    }

    @Override
    public Integer getValue(@NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull BlockVector position, @NotNull LimitedRegion limitedRegion) {
        int baseValue = base == null ? 0 : base.getValue(worldInfo, random, position, limitedRegion);

        WorldGenLevel level = ((CraftLimitedRegion) limitedRegion).getHandle();
        ChunkGenerator chunkGenerator = level.getLevel().getChunkSource().chunkMap.generator;

        return Math.min(level.getHeight(), chunkGenerator.getGenDepth()) - 1 + Math.max(level.getMinBuildHeight(), chunkGenerator.getMinY()) - baseValue;
    }

    @Override
    public boolean isDirty() {
        if (dirty) {
            return true;
        }

        return base != null && base.isDirty();
    }

    @Override
    public void saved() {
        dirty = false;

        if (base != null) {
            base.saved();
        }
    }

    public IntegerValue getBase() {
        return base;
    }

    public void setBase(IntegerValue integerValue) {
        this.base = integerValue;
        dirty = true;
    }

    @Override
    public BelowTopOffsetIntegerValue clone() {
        return new BelowTopOffsetIntegerValue(base == null ? null : base.clone());
    }
}
