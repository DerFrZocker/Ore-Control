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

package de.derfrzocker.ore.control.impl.v1_19_R2.placement;

import de.derfrzocker.feature.api.FeaturePlacementModifier;
import de.derfrzocker.feature.common.value.number.integer.FixedDoubleToIntegerValue;
import de.derfrzocker.feature.impl.v1_19_R2.placement.configuration.SurfaceRelativeThresholdModifierConfiguration;
import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.OreControlManager;
import de.derfrzocker.ore.control.impl.v1_19_R2.NMSReflectionNames;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.placement.SurfaceRelativeThresholdFilter;
import org.bukkit.NamespacedKey;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.BlockVector;
import org.bukkit.util.NumberConversions;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Random;

public class SurfaceRelativeThresholdModifierHook extends MinecraftPlacementModifierHook<SurfaceRelativeThresholdFilter, SurfaceRelativeThresholdModifierConfiguration> {

    public SurfaceRelativeThresholdModifierHook(@NotNull OreControlManager oreControlManager, @NotNull Biome biome, @NotNull NamespacedKey namespacedKey, @NotNull SurfaceRelativeThresholdFilter defaultModifier) {
        super(oreControlManager, "surface_relative_threshold_filter", defaultModifier, biome, namespacedKey);
    }

    public static SurfaceRelativeThresholdModifierConfiguration createDefaultConfiguration(@NotNull SurfaceRelativeThresholdFilter defaultModifier, @NotNull FeaturePlacementModifier<?> modifier) {
        try {
            Field heightmap = SurfaceRelativeThresholdFilter.class.getDeclaredField(NMSReflectionNames.SURFACE_RELATIVE_THRESHOLD_FILTER_HEIGHTMAP);
            heightmap.setAccessible(true);

            Field minInclusive = SurfaceRelativeThresholdFilter.class.getDeclaredField(NMSReflectionNames.SURFACE_RELATIVE_THRESHOLD_FILTER_MIN_INCLUSIVE);
            minInclusive.setAccessible(true);

            Field maxInclusive = SurfaceRelativeThresholdFilter.class.getDeclaredField(NMSReflectionNames.SURFACE_RELATIVE_THRESHOLD_FILTER_MAX_INCLUSIVE);
            maxInclusive.setAccessible(true);

            Object heightmapValue = heightmap.get(defaultModifier);
            Object minInclusiveValue = minInclusive.get(defaultModifier);
            Object maxInclusiveValue = maxInclusive.get(defaultModifier);
            return new SurfaceRelativeThresholdModifierConfiguration(modifier, null, new FixedDoubleToIntegerValue(NumberConversions.toInt(minInclusiveValue)), new FixedDoubleToIntegerValue(NumberConversions.toInt(maxInclusiveValue)));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public SurfaceRelativeThresholdFilter createModifier(@NotNull SurfaceRelativeThresholdModifierConfiguration defaultConfiguration, @NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull BlockVector position, @NotNull LimitedRegion limitedRegion, @NotNull SurfaceRelativeThresholdModifierConfiguration configuration) {
        Heightmap.Types heightmap;
        if (configuration.getHeightmap() == null) {
            heightmap = defaultConfiguration.getHeightmap().getValue(worldInfo, random, position, limitedRegion);
        } else {
            heightmap = configuration.getHeightmap().getValue(worldInfo, random, position, limitedRegion);
        }

        int minInclusive;
        if (configuration.getMinInclusive() == null) {
            minInclusive = defaultConfiguration.getMinInclusive().getValue(worldInfo, random, position, limitedRegion);
        } else {
            minInclusive = configuration.getMinInclusive().getValue(worldInfo, random, position, limitedRegion);
        }

        int maxInclusive;
        if (configuration.getMaxInclusive() == null) {
            maxInclusive = defaultConfiguration.getMaxInclusive().getValue(worldInfo, random, position, limitedRegion);
        } else {
            maxInclusive = configuration.getMaxInclusive().getValue(worldInfo, random, position, limitedRegion);
        }

        return SurfaceRelativeThresholdFilter.of(heightmap, minInclusive, maxInclusive);
    }

    @Override
    public SurfaceRelativeThresholdModifierConfiguration createDefaultConfiguration(SurfaceRelativeThresholdFilter defaultModifier) {
        return createDefaultConfiguration(defaultModifier, getPlacementModifier());
    }
}
