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

package de.derfrzocker.ore.control.impl.v1_18_R1.feature.generator;

import de.derfrzocker.feature.api.Registries;
import de.derfrzocker.feature.impl.v1_18_R1.feature.generator.configuration.OreFeatureConfiguration;
import de.derfrzocker.feature.impl.v1_18_R1.value.target.TargetValue;
import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.dao.ConfigDao;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import org.bukkit.NamespacedKey;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ScatteredOreFeatureGeneratorHook extends MinecraftFeatureGeneratorHook<OreConfiguration, OreFeatureConfiguration> {

    public ScatteredOreFeatureGeneratorHook(@NotNull Registries registries, ConfigDao configDao, @NotNull NamespacedKey namespacedKey, @NotNull Biome biome) {
        super(OreConfiguration.CODEC, Feature.SCATTERED_ORE, registries, configDao, "scattered_ore", biome, namespacedKey);
    }

    @Override
    public OreConfiguration createConfig(@NotNull OreConfiguration defaultConfiguration, @NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull BlockVector position, @NotNull LimitedRegion limitedRegion, @NotNull OreFeatureConfiguration configuration) {
        List<OreConfiguration.TargetBlockState> blockStates;
        if (configuration.getTargets() == null) {
            blockStates = defaultConfiguration.targetStates;
        } else {
            blockStates = new ArrayList<>();

            for (TargetValue targetValue : configuration.getTargets()) {
                blockStates.add(targetValue.getValue(worldInfo, random, position, limitedRegion));
            }
        }

        int size;
        if (configuration.getSize() == null) {
            size = defaultConfiguration.size;
        } else {
            size = configuration.getSize().getValue(worldInfo, random, position, limitedRegion);
        }

        float discardChanceOnAirExposure;
        if (configuration.getDiscardChanceOnAirExposure() == null) {
            discardChanceOnAirExposure = defaultConfiguration.discardChanceOnAirExposure;
        } else {
            discardChanceOnAirExposure = configuration.getDiscardChanceOnAirExposure().getValue(worldInfo, random, position, limitedRegion);
        }

        return new OreConfiguration(blockStates, size, discardChanceOnAirExposure);
    }
}
