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

package de.derfrzocker.feature.impl.v1_18_R1.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.derfrzocker.feature.api.Registries;
import de.derfrzocker.feature.common.value.number.IntegerType;
import de.derfrzocker.feature.common.value.number.IntegerValue;
import de.derfrzocker.feature.impl.v1_18_R1.placement.configuration.SurfaceRelativeThresholdModifierConfiguration;
import de.derfrzocker.feature.impl.v1_18_R1.value.heightmap.HeightmapType;
import de.derfrzocker.feature.impl.v1_18_R1.value.heightmap.HeightmapValue;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.placement.SurfaceRelativeThresholdFilter;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Random;

public class SurfaceRelativeThresholdModifier extends MinecraftPlacementModifier<SurfaceRelativeThresholdFilter, SurfaceRelativeThresholdModifierConfiguration> {
    public SurfaceRelativeThresholdModifier(@NotNull Registries registries) {
        super(registries, "surface_relative_threshold_filter");
    }

    @Override
    public SurfaceRelativeThresholdModifierConfiguration mergeConfig(SurfaceRelativeThresholdModifierConfiguration first, SurfaceRelativeThresholdModifierConfiguration second) {
        return new SurfaceRelativeThresholdModifierConfiguration(this,
                first.getHeightmap() != null ? first.getHeightmap() : second.getHeightmap(),
                first.getMinInclusive() != null ? first.getMinInclusive() : second.getMinInclusive(),
                first.getMaxInclusive() != null ? first.getMaxInclusive() : second.getMaxInclusive());
    }

    @Override
    public Codec<SurfaceRelativeThresholdModifierConfiguration> createCodec(Registries registries) {
        return RecordCodecBuilder.create((builder) -> builder.group(
                registries.getValueTypeRegistry(HeightmapType.class).dispatch("heightmap_type", HeightmapValue::getValueType, HeightmapType::getCodec).
                        optionalFieldOf("heightmap").forGetter(config -> Optional.ofNullable(config.getHeightmap())),
                registries.getValueTypeRegistry(IntegerType.class).dispatch("min_inclusive_type", IntegerValue::getValueType, IntegerType::getCodec).
                        optionalFieldOf("min_inclusive").forGetter(config -> Optional.ofNullable(config.getMinInclusive())),
                registries.getValueTypeRegistry(IntegerType.class).dispatch("max_inclusive_type", IntegerValue::getValueType, IntegerType::getCodec).
                        optionalFieldOf("max_inclusive").forGetter(config -> Optional.ofNullable(config.getMaxInclusive()))
        ).apply(builder, (heightmap, minInclusive, maxInclusive) -> new SurfaceRelativeThresholdModifierConfiguration(this, heightmap.orElse(null), minInclusive.orElse(null), maxInclusive.orElse(null))));
    }

    @Override
    public SurfaceRelativeThresholdFilter createPlacementModifier(@NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull BlockVector position, @NotNull LimitedRegion limitedRegion, @NotNull SurfaceRelativeThresholdModifierConfiguration configuration) {
        Heightmap.Types heightmap = Heightmap.Types.MOTION_BLOCKING;
        if (configuration.getHeightmap() != null) {
            heightmap = configuration.getHeightmap().getValue(worldInfo, random, position, limitedRegion);
        }

        int minInclusive = 0;
        if (configuration.getMinInclusive() != null) {
            minInclusive = configuration.getMinInclusive().getValue(worldInfo, random, position, limitedRegion);
        }

        int maxInclusive = 0;
        if (configuration.getMaxInclusive() != null) {
            maxInclusive = configuration.getMaxInclusive().getValue(worldInfo, random, position, limitedRegion);
        }
        return SurfaceRelativeThresholdFilter.of(heightmap, minInclusive, maxInclusive);
    }
}
