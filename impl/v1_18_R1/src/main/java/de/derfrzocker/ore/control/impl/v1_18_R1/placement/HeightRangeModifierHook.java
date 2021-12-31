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

package de.derfrzocker.ore.control.impl.v1_18_R1.placement;

import de.derfrzocker.feature.api.Registries;
import de.derfrzocker.feature.impl.v1_18_R1.placement.configuration.HeightRangeModifierConfiguration;
import de.derfrzocker.feature.impl.v1_18_R1.value.heightprovider.FixedHeightProviderValue;
import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.dao.ConfigDao;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import org.bukkit.NamespacedKey;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Random;

public class HeightRangeModifierHook extends MinecraftPlacementModifierHook<HeightRangePlacement, HeightRangeModifierConfiguration> {

    public HeightRangeModifierHook(@NotNull Registries registries, ConfigDao configDao, @NotNull Biome biome, @NotNull NamespacedKey namespacedKey, @NotNull HeightRangePlacement defaultModifier) {
        super(registries, configDao, "height_range", defaultModifier, biome, namespacedKey);
    }

    @Override
    public HeightRangeModifierConfiguration createDefaultConfiguration(@NotNull HeightRangePlacement defaultModifier) {
        try {
            Field chance = HeightRangePlacement.class.getDeclaredField("c");
            chance.setAccessible(true);
            Object value = chance.get(defaultModifier);
            return new HeightRangeModifierConfiguration(getPlacementModifier(), new FixedHeightProviderValue((HeightProvider) value));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public HeightRangePlacement createModifier(@NotNull HeightRangeModifierConfiguration defaultConfiguration, @NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull BlockVector position, @NotNull LimitedRegion limitedRegion, @NotNull HeightRangeModifierConfiguration configuration) {
        HeightProvider height;
        if (configuration.getHeight() == null) {
            height = defaultConfiguration.getHeight().getValue(worldInfo, random, position, limitedRegion);
        } else {
            height = configuration.getHeight().getValue(worldInfo, random, position, limitedRegion);
        }

        return HeightRangePlacement.of(height);
    }
}
