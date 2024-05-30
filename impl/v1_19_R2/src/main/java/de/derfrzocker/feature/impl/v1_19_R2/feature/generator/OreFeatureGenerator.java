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

package de.derfrzocker.feature.impl.v1_19_R2.feature.generator;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.derfrzocker.feature.api.Registries;
import de.derfrzocker.feature.api.Setting;
import de.derfrzocker.feature.api.util.Parser;
import de.derfrzocker.feature.common.value.number.FloatType;
import de.derfrzocker.feature.common.value.number.FloatValue;
import de.derfrzocker.feature.common.value.number.IntegerType;
import de.derfrzocker.feature.common.value.number.IntegerValue;
import de.derfrzocker.feature.impl.v1_19_R2.feature.generator.configuration.OreFeatureConfiguration;
import de.derfrzocker.feature.impl.v1_19_R2.value.target.TargetValue;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.world.level.levelgen.feature.OreFeature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import org.bukkit.NamespacedKey;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;

public class OreFeatureGenerator extends MinecraftFeatureGenerator<OreConfiguration, OreFeatureConfiguration> {

    public OreFeatureGenerator(Registries registries) {
        super(registries, OreFeature.ORE, "ore");
    }

    @Override
    public Parser<OreFeatureConfiguration> createParser(Registries registries) {
        return new Parser<>() {
            @Override
            public JsonElement toJson(OreFeatureConfiguration value) {
                JsonObject jsonObject = new JsonObject();

                if (value.getSize() != null) {
                    JsonObject size = value.getSize().getValueType().getParser().toJson(value.getSize()).getAsJsonObject();
                    size.addProperty("size_type", value.getSize().getValueType().getKey().toString());
                    jsonObject.add("size", size);
                }

                if (value.getDiscardChanceOnAirExposure() != null) {
                    JsonObject discardChanceOnAirExposure = value.getDiscardChanceOnAirExposure().getValueType().getParser().toJson(value.getDiscardChanceOnAirExposure()).getAsJsonObject();
                    discardChanceOnAirExposure.addProperty("discard_chance_on_air_exposure_type", value.getDiscardChanceOnAirExposure().getValueType().getKey().toString());
                }

                return jsonObject;
            }

            @Override
            public OreFeatureConfiguration fromJson(JsonElement jsonElement) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();

                IntegerValue size = null;
                if (jsonObject.has("size")) {
                    JsonObject sizes = jsonObject.getAsJsonObject("size");
                    size = registries.getValueTypeRegistry(IntegerType.class).get(NamespacedKey.fromString(sizes.getAsJsonPrimitive("size_type").getAsString())).get().getParser().fromJson(sizes);
                }

                FloatValue discardChanceOnAirExposure = null;
                if (jsonObject.has("discard_chance_on_air_exposure")) {
                    JsonObject chance = jsonObject.getAsJsonObject("discard_chance_on_air_exposure");
                    discardChanceOnAirExposure = registries.getValueTypeRegistry(FloatType.class).get(NamespacedKey.fromString(chance.getAsJsonPrimitive("discard_chance_on_air_exposure_type").getAsString())).get().getParser().fromJson(chance);
                }

                return new OreFeatureConfiguration(OreFeatureGenerator.this, null, size, discardChanceOnAirExposure);
            }
        };
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
