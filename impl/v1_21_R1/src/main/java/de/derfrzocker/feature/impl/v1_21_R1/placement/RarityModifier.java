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

package de.derfrzocker.feature.impl.v1_21_R1.placement;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.derfrzocker.feature.api.Registries;
import de.derfrzocker.feature.api.Setting;
import de.derfrzocker.feature.api.util.Parser;
import de.derfrzocker.feature.common.feature.placement.configuration.RarityModifierConfiguration;
import de.derfrzocker.feature.common.value.number.IntegerType;
import de.derfrzocker.feature.common.value.number.IntegerValue;
import java.util.Random;
import java.util.Set;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import org.bukkit.NamespacedKey;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;

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
    public Parser<RarityModifierConfiguration> createParser(Registries registries) {
        return new Parser<>() {
            @Override
            public JsonElement toJson(RarityModifierConfiguration value) {
                JsonObject jsonObject = new JsonObject();
                if (value.getChance() != null) {
                    JsonObject entry = value.getChance().getValueType().getParser().toJson(value.getChance()).getAsJsonObject();
                    entry.addProperty("chance_type", value.getChance().getValueType().getKey().toString());
                    jsonObject.add("chance", entry);
                }
                return jsonObject;
            }

            @Override
            public RarityModifierConfiguration fromJson(JsonElement jsonElement) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();

                IntegerValue count = null;
                if (jsonObject.has("chance")) {
                    JsonObject entry = jsonObject.getAsJsonObject("chance");
                    count = registries.getValueTypeRegistry(IntegerType.class).get(NamespacedKey.fromString(entry.getAsJsonPrimitive("chance_type").getAsString())).get().getParser().fromJson(entry);
                }

                return new RarityModifierConfiguration(RarityModifier.this, count);
            }
        };
    }

    @Override
    public RarityFilter createPlacementModifier(@NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull BlockVector position, @NotNull LimitedRegion limitedRegion, @NotNull RarityModifierConfiguration configuration) {
        int chance = 0;
        if (configuration.getChance() != null) {
            chance = configuration.getChance().getValue(worldInfo, random, position, limitedRegion);
        }
        return RarityFilter.onAverageOnceEvery(chance);
    }

    @NotNull
    @Override
    public Set<Setting> getSettings() {
        return RarityModifierConfiguration.SETTINGS;
    }

    @NotNull
    @Override
    public RarityModifierConfiguration createEmptyConfiguration() {
        return new RarityModifierConfiguration(this, null);
    }
}
