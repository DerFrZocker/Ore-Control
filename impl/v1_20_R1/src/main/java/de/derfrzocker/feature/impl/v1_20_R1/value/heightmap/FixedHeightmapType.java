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

import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.Heightmap;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

public class FixedHeightmapType extends HeightmapType {
    public static final NamespacedKey KEY = NamespacedKey.fromString("feature:fixed_heightmap_type");
    public static final FixedHeightmapType INSTANCE = new FixedHeightmapType();
    public static final Codec<FixedHeightmapValue> CODEC = Heightmap.Types.CODEC.xmap(FixedHeightmapValue::new, FixedHeightmapValue::getValue);

    private FixedHeightmapType() {
    }

    @Override
    public Codec<HeightmapValue> getCodec() {
        return CODEC.xmap(value -> value, value -> (FixedHeightmapValue) value);
    }

    @Override
    public HeightmapValue createNewValue() {
        throw new UnsupportedOperationException("Not supported");
    }

    @NotNull
    @Override
    public NamespacedKey getKey() {
        return KEY;
    }
}
