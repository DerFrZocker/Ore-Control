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
import de.derfrzocker.feature.api.Setting;
import de.derfrzocker.feature.common.value.number.IntegerType;
import de.derfrzocker.feature.common.value.number.IntegerValue;
import de.derfrzocker.feature.impl.v1_18_R1.placement.configuration.RarityModifierConfiguration;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Random;
import java.util.Set;

public class RarityModifier extends MinecraftPlacementModifier<RarityFilter, RarityModifierConfiguration> {

    public RarityModifier(@NotNull Registries registries) {
        super(registries, "rarity_filter");
    }


    @Override
    public RarityModifierConfiguration mergeConfig(RarityModifierConfiguration first, RarityModifierConfiguration second) {
        return new RarityModifierConfiguration(this,
                first.getChance() != null ? first.getChance() : second.getChance());
    }

    @Override
    public Codec<RarityModifierConfiguration> createCodec(Registries registries) {
        return RecordCodecBuilder.create((builder) -> builder.group(
                registries.getValueTypeRegistry(IntegerType.class).dispatch("chance_type", IntegerValue::getValueType, IntegerType::getCodec).
                        optionalFieldOf("chance").forGetter(config -> Optional.ofNullable(config.getChance()))
        ).apply(builder, (chance) -> new RarityModifierConfiguration(this, chance.orElse(null))));
    }

    @Override
    public RarityFilter createPlacementModifier(@NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull BlockVector position, @NotNull LimitedRegion limitedRegion, @NotNull RarityModifierConfiguration configuration) {
        int chance = 0;
        if (configuration.getChance() != null) {
            chance = configuration.getChance().getValue(worldInfo, random, position, limitedRegion);
        }
        return RarityFilter.onAverageOnceEvery(chance);
    }

    @Override
    public Set<Setting> getSettings() {
        return RarityModifierConfiguration.SETTINGS;
    }

    @Override
    public RarityModifierConfiguration createEmptyConfiguration() {
        return new RarityModifierConfiguration(this, null);
    }
}
