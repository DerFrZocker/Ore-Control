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

package de.derfrzocker.ore.control.impl.v1_20_R1.placement;

import de.derfrzocker.feature.api.FeaturePlacementModifier;
import de.derfrzocker.feature.common.feature.placement.configuration.RarityModifierConfiguration;
import de.derfrzocker.feature.common.value.number.integer.FixedDoubleToIntegerValue;
import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.OreControlManager;
import de.derfrzocker.ore.control.impl.v1_20_R1.NMSReflectionNames;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import org.bukkit.NamespacedKey;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.BlockVector;
import org.bukkit.util.NumberConversions;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Random;

public class RarityModifierHook extends MinecraftPlacementModifierHook<RarityFilter, RarityModifierConfiguration> {

    public RarityModifierHook(@NotNull OreControlManager oreControlManager, @NotNull Biome biome, @NotNull NamespacedKey namespacedKey, @NotNull RarityFilter defaultModifier) {
        super(oreControlManager, "rarity_filter", defaultModifier, biome, namespacedKey);
    }

    public static RarityModifierConfiguration createDefaultConfiguration(@NotNull RarityFilter defaultModifier, @NotNull FeaturePlacementModifier<?> modifier) {
        try {
            Field chance = RarityFilter.class.getDeclaredField(NMSReflectionNames.RARITY_FILTER_CHANCE);
            chance.setAccessible(true);
            Object value = chance.get(defaultModifier);
            return new RarityModifierConfiguration(modifier, new FixedDoubleToIntegerValue(NumberConversions.toInt(value)));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public RarityModifierConfiguration createDefaultConfiguration(@NotNull RarityFilter defaultModifier) {
        return createDefaultConfiguration(defaultModifier, getPlacementModifier());
    }

    @Override
    public RarityFilter createModifier(@NotNull RarityModifierConfiguration defaultConfiguration, @NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull BlockVector position, @NotNull LimitedRegion limitedRegion, @NotNull RarityModifierConfiguration configuration) {
        int chance;
        if (configuration.getChance() == null) {
            chance = defaultConfiguration.getChance().getValue(worldInfo, random, position, limitedRegion);
        } else {
            chance = configuration.getChance().getValue(worldInfo, random, position, limitedRegion);
        }

        return RarityFilter.onAverageOnceEvery(chance);
    }
}
