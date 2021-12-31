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
import de.derfrzocker.feature.impl.v1_18_R1.placement.configuration.HeightRangeModifierConfiguration;
import de.derfrzocker.feature.impl.v1_18_R1.value.heightprovider.HeightProviderType;
import de.derfrzocker.feature.impl.v1_18_R1.value.heightprovider.HeightProviderValue;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.heightproviders.ConstantHeight;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Random;

public class HeightRangeModifier extends MinecraftPlacementModifier<HeightRangePlacement, HeightRangeModifierConfiguration> {

    public HeightRangeModifier(@NotNull Registries registries) {
        super(registries, "height_range");
    }

    @Override
    public HeightRangeModifierConfiguration mergeConfig(HeightRangeModifierConfiguration first, HeightRangeModifierConfiguration second) {
        return new HeightRangeModifierConfiguration(this,
                first.getHeight() != null ? first.getHeight() : second.getHeight());
    }

    @Override
    public Codec<HeightRangeModifierConfiguration> createCodec(Registries registries) {
        return RecordCodecBuilder.create((builder) -> builder.group(
                registries.getValueTypeRegistry(HeightProviderType.class).dispatch("height_range_type", HeightProviderValue::getValueType, HeightProviderType::getCodec).
                        optionalFieldOf("height").forGetter(config -> Optional.ofNullable(config.getHeight()))
        ).apply(builder, (count) -> new HeightRangeModifierConfiguration(this, count.orElse(null))));
    }

    @Override
    public HeightRangePlacement createPlacementModifier(@NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull BlockVector position, @NotNull LimitedRegion limitedRegion, @NotNull HeightRangeModifierConfiguration configuration) {
        HeightProvider height;
        if (configuration.getHeight() != null) {
            height = configuration.getHeight().getValue(worldInfo, random, position, limitedRegion);
        } else {
            height = ConstantHeight.of(VerticalAnchor.bottom());
        }

        return HeightRangePlacement.of(height);
    }
}
