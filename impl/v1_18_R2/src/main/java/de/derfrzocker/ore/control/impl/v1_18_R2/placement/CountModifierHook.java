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

package de.derfrzocker.ore.control.impl.v1_18_R2.placement;

import de.derfrzocker.feature.api.Registries;
import de.derfrzocker.feature.common.value.number.IntegerValue;
import de.derfrzocker.feature.common.value.number.integer.FixedDoubleToIntegerValue;
import de.derfrzocker.feature.common.value.number.integer.uniform.UniformIntegerValue;
import de.derfrzocker.feature.impl.v1_18_R2.placement.configuration.CountModifierConfiguration;
import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.config.ConfigManager;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviderType;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import org.bukkit.NamespacedKey;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Random;

public class CountModifierHook extends MinecraftPlacementModifierHook<CountPlacement, CountModifierConfiguration> {

    public CountModifierHook(@NotNull Registries registries, ConfigManager configManager, @NotNull Biome biome, @NotNull NamespacedKey namespacedKey, @NotNull CountPlacement defaultModifier) {
        super(registries, configManager, "count", defaultModifier, biome, namespacedKey);
    }

    @Override
    public CountModifierConfiguration createDefaultConfiguration(@NotNull CountPlacement defaultModifier) {
        try {
            Field chance = CountPlacement.class.getDeclaredField("c");
            chance.setAccessible(true);
            IntProvider value = (IntProvider) chance.get(defaultModifier);
            IntegerValue integerValue;
            if (value.getType() == IntProviderType.CONSTANT) {
                integerValue = new FixedDoubleToIntegerValue(value.getMinValue());
            } else if (value.getType() == IntProviderType.UNIFORM) {
                integerValue = new UniformIntegerValue(new FixedDoubleToIntegerValue(value.getMinValue()), new FixedDoubleToIntegerValue(value.getMaxValue()));
            } else { // TODO add rest of IntProvider types
                throw new UnsupportedOperationException(String.format("No integer value equivalent for IntProvider '%s'", value));
            }
            return new CountModifierConfiguration(getPlacementModifier(), integerValue);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CountPlacement createModifier(@NotNull CountModifierConfiguration defaultConfiguration, @NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull BlockVector position, @NotNull LimitedRegion limitedRegion, @NotNull CountModifierConfiguration configuration) {
        int count;
        if (configuration.getCount() == null) {
            count = defaultConfiguration.getCount().getValue(worldInfo, random, position, limitedRegion);
        } else {
            count = configuration.getCount().getValue(worldInfo, random, position, limitedRegion);
        }

        return CountPlacement.of(count);
    }
}