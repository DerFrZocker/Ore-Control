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

package de.derfrzocker.feature.impl.v1_18_R2.placement;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.derfrzocker.feature.api.Registries;
import de.derfrzocker.feature.api.Setting;
import de.derfrzocker.feature.api.util.Parser;
import de.derfrzocker.feature.common.feature.placement.configuration.SurfaceWaterDepthModifierConfiguration;
import de.derfrzocker.feature.common.value.number.IntegerType;
import de.derfrzocker.feature.common.value.number.IntegerValue;
import net.minecraft.world.level.levelgen.placement.SurfaceWaterDepthFilter;
import org.bukkit.NamespacedKey;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.Set;

public class SurfaceWaterDepthModifier extends MinecraftPlacementModifier<SurfaceWaterDepthFilter, SurfaceWaterDepthModifierConfiguration> {

    public SurfaceWaterDepthModifier(@NotNull Registries registries) {
        super(registries, "surface_water_depth_filter");
    }

    @Override
    public SurfaceWaterDepthModifierConfiguration mergeConfig(SurfaceWaterDepthModifierConfiguration first, SurfaceWaterDepthModifierConfiguration second) {
        return new SurfaceWaterDepthModifierConfiguration(this,
                first.getMaxWaterDepth() != null ? first.getMaxWaterDepth() : second.getMaxWaterDepth());
    }

    @Override
    public Parser<SurfaceWaterDepthModifierConfiguration> createParser(Registries registries) {
        return new Parser<>() {
            @Override
            public JsonElement toJson(SurfaceWaterDepthModifierConfiguration value) {
                JsonObject jsonObject = new JsonObject();
                if (value.getMaxWaterDepth() != null) {
                    JsonObject entry = value.getMaxWaterDepth().getValueType().getParser().toJson(value.getMaxWaterDepth()).getAsJsonObject();
                    entry.addProperty("max_water_depth_type", value.getMaxWaterDepth().getValueType().getKey().toString());
                    jsonObject.add("max_water_depth", entry);
                }
                return jsonObject;
            }

            @Override
            public SurfaceWaterDepthModifierConfiguration fromJson(JsonElement jsonElement) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();

                IntegerValue count = null;
                if (jsonObject.has("max_water_depth")) {
                    JsonObject entry = jsonObject.getAsJsonObject("max_water_depth");
                    count = registries.getValueTypeRegistry(IntegerType.class).get(NamespacedKey.fromString(entry.getAsJsonPrimitive("max_water_depth_type").getAsString())).get().getParser().fromJson(entry);
                }

                return new SurfaceWaterDepthModifierConfiguration(SurfaceWaterDepthModifier.this, count);
            }
        };
    }

    @Override
    public SurfaceWaterDepthFilter createPlacementModifier(@NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull BlockVector position, @NotNull LimitedRegion limitedRegion, @NotNull SurfaceWaterDepthModifierConfiguration configuration) {
        int maxWaterDepth = 0;
        if (configuration.getMaxWaterDepth() != null) {
            maxWaterDepth = configuration.getMaxWaterDepth().getValue(worldInfo, random, position, limitedRegion);
        }
        return SurfaceWaterDepthFilter.forMaxDepth(maxWaterDepth);
    }

    @NotNull
    @Override
    public Set<Setting> getSettings() {
        return SurfaceWaterDepthModifierConfiguration.SETTINGS;
    }

    @NotNull
    @Override
    public SurfaceWaterDepthModifierConfiguration createEmptyConfiguration() {
        return new SurfaceWaterDepthModifierConfiguration(this, null);
    }
}