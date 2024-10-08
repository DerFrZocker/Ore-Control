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

package de.derfrzocker.feature.impl.v1_21_R1.value.offset;

import de.derfrzocker.feature.common.value.number.IntegerValue;
import de.derfrzocker.feature.common.value.offset.AboveBottomOffsetIntegerValue;
import java.util.Random;
import net.minecraft.world.level.WorldGenLevel;
import org.bukkit.craftbukkit.v1_21_R1.generator.CraftLimitedRegion;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;

public class NMSAboveBottomOffsetIntegerValue extends AboveBottomOffsetIntegerValue {

    public NMSAboveBottomOffsetIntegerValue(IntegerValue base) {
        super(base);
    }

    @Override
    public Integer getValue(@NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull BlockVector position, @NotNull LimitedRegion limitedRegion) {
        int baseValue = getBase() == null ? 0 : getBase().getValue(worldInfo, random, position, limitedRegion);

        WorldGenLevel level = ((CraftLimitedRegion) limitedRegion).getHandle();

        return Math.max(level.getMinBuildHeight(), level.getLevel().getChunkSource().getGenerator().getMinY()) + baseValue;
    }

    @Override
    public AboveBottomOffsetIntegerValue clone() {
        return new NMSAboveBottomOffsetIntegerValue(getBase() == null ? null : getBase().clone());
    }
}
