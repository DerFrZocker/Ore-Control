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

package de.derfrzocker.feature.impl.v1_20_R1.value.heightmap;

import de.derfrzocker.feature.api.util.traverser.message.StringFormatter;
import de.derfrzocker.feature.api.util.traverser.message.TraversKey;
import net.minecraft.world.level.levelgen.Heightmap;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FixedHeightmapValue extends HeightmapValue {

    private final Heightmap.Types value;
    private boolean dirty = false;

    public FixedHeightmapValue(Heightmap.Types value) {
        this.value = value;
    }

    @Override
    public FixedHeightmapType getValueType() {
        return FixedHeightmapType.INSTANCE;
    }

    @Override
    public Heightmap.Types getValue(@NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull BlockVector position, @NotNull LimitedRegion limitedRegion) {
        return value;
    }

    public Heightmap.Types getValue() {
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

    @Override
    public FixedHeightmapValue clone() {
        return new FixedHeightmapValue(value);
    }

    @Override
    public @NotNull List<@NotNull String> traverse(@NotNull StringFormatter formatter, int depth, @NotNull TraversKey key) {
        return new ArrayList<>();
    }
}
