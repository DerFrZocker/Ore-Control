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

package de.derfrzocker.feature.impl.v1_18_R1.feature.generator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.derfrzocker.feature.api.Registries;
import de.derfrzocker.feature.api.Setting;
import de.derfrzocker.feature.common.value.number.FloatType;
import de.derfrzocker.feature.common.value.number.FloatValue;
import de.derfrzocker.feature.common.value.number.IntegerType;
import de.derfrzocker.feature.common.value.number.IntegerValue;
import de.derfrzocker.feature.impl.v1_18_R1.feature.generator.configuration.OreFeatureConfiguration;
import de.derfrzocker.feature.impl.v1_18_R1.value.target.TargetType;
import de.derfrzocker.feature.impl.v1_18_R1.value.target.TargetValue;
import net.minecraft.world.level.levelgen.feature.OreFeature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class OreFeatureGenerator extends MinecraftFeatureGenerator<OreConfiguration, OreFeatureConfiguration> {

    public OreFeatureGenerator(Registries registries) {
        super(registries, OreFeature.ORE, "ore");
    }

    public Codec<OreFeatureConfiguration> createCodec(Registries registries) {
        return RecordCodecBuilder.create((builder) -> builder.group(
                Codec.list(registries.getValueTypeRegistry(TargetType.class).
                                dispatch("target_type", TargetValue::getValueType, TargetType::getCodec)).
                        optionalFieldOf("targets").
                        forGetter(config -> Optional.ofNullable(config.getTargets())),
                registries.getValueTypeRegistry(IntegerType.class).dispatch("size_type", IntegerValue::getValueType, IntegerType::getCodec).
                        optionalFieldOf("size").forGetter(config -> Optional.ofNullable(config.getSize())),
                registries.getValueTypeRegistry(FloatType.class).dispatch("discard_chance_on_air_exposure_type", FloatValue::getValueType, FloatType::getCodec).
                        optionalFieldOf("discard_chance_on_air_exposure").forGetter(config -> Optional.ofNullable(config.getDiscardChanceOnAirExposure()))
        ).apply(builder, (targets, size, discardChanceOnAirExposure) -> new OreFeatureConfiguration(this, targets.orElse(null), size.orElse(null), discardChanceOnAirExposure.orElse(null))));
    }

    @Override
    public OreFeatureConfiguration mergeConfig(OreFeatureConfiguration first, OreFeatureConfiguration second) {
        return new OreFeatureConfiguration(this,
                first.getTargets() != null ? first.getTargets() : second.getTargets(),
                first.getSize() != null ? first.getSize() : second.getSize(),
                first.getDiscardChanceOnAirExposure() != null ? first.getDiscardChanceOnAirExposure() : second.getDiscardChanceOnAirExposure());
    }

    @Override
    public OreConfiguration createConfiguration(@NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull BlockVector position, @NotNull LimitedRegion limitedRegion, @NotNull OreFeatureConfiguration configuration) {
        List<OreConfiguration.TargetBlockState> blockStates = new ArrayList<>();
        if (configuration.getTargets() != null) {
            for (TargetValue targetValue : configuration.getTargets()) {
                blockStates.add(targetValue.getValue(worldInfo, random, position, limitedRegion));
            }
        }

        int size = 0;
        if (configuration.getSize() != null) {
            size = configuration.getSize().getValue(worldInfo, random, position, limitedRegion);
        }

        float discardChanceOnAirExposure = 0f;
        if (configuration.getDiscardChanceOnAirExposure() != null) {
            discardChanceOnAirExposure = configuration.getDiscardChanceOnAirExposure().getValue(worldInfo, random, position, limitedRegion);
        }

        return new OreConfiguration(blockStates, size, discardChanceOnAirExposure);
    }

    @NotNull
    @Override
    public Set<Setting> getSettings() {
        return OreFeatureConfiguration.SETTINGS;
    }

    @NotNull
    @Override
    public OreFeatureConfiguration createEmptyConfiguration() {
        return new OreFeatureConfiguration(this, null, null, null);
    }
}
