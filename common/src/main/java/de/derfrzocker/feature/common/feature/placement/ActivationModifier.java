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

package de.derfrzocker.feature.common.feature.placement;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.derfrzocker.feature.api.Configuration;
import de.derfrzocker.feature.api.FeaturePlacementModifier;
import de.derfrzocker.feature.api.PlacementModifierConfiguration;
import de.derfrzocker.feature.api.Registries;
import de.derfrzocker.feature.api.Setting;
import de.derfrzocker.feature.api.util.Parser;
import de.derfrzocker.feature.common.feature.placement.configuration.ActivationConfiguration;
import de.derfrzocker.feature.common.value.bool.BooleanType;
import de.derfrzocker.feature.common.value.bool.BooleanValue;
import org.bukkit.NamespacedKey;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;

public class ActivationModifier implements FeaturePlacementModifier<ActivationConfiguration> {

    public static final NamespacedKey KEY = NamespacedKey.fromString("feature:activation");

    private final Parser<PlacementModifierConfiguration> parser;

    public ActivationModifier(Registries registries) {
        parser = new Parser<>() {
            @Override
            public JsonElement toJson(PlacementModifierConfiguration v) {
                ActivationConfiguration value = (ActivationConfiguration) v;
                JsonObject jsonObject = new JsonObject();

                if (value.getActivate() != null) {
                    JsonObject entry = value.getActivate().getValueType().getParser().toJson(value.getActivate()).getAsJsonObject();
                    entry.addProperty("activate_type", value.getActivate().getValueType().getKey().toString());
                    jsonObject.add("activate", entry);
                }

                return jsonObject;
            }

            @Override
            public ActivationConfiguration fromJson(JsonElement jsonElement) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();

                BooleanValue activate = null;
                if (jsonObject.has("activate")) {
                    JsonObject entry = jsonObject.getAsJsonObject("activate");
                    activate = registries.getValueTypeRegistry(BooleanType.class).get(NamespacedKey.fromString(entry.getAsJsonPrimitive("activate_type").getAsString())).get().getParser().fromJson(entry);
                }

                return new ActivationConfiguration(ActivationModifier.this, activate);
            }
        };
    }

    @NotNull
    @Override
    public Set<Setting> getSettings() {
        return ActivationConfiguration.SETTINGS;
    }

    @NotNull
    @Override
    public Configuration createEmptyConfiguration() {
        return new ActivationConfiguration(this, null);
    }

    @NotNull
    @Override
    public Parser<PlacementModifierConfiguration> getParser() {
        return parser;
    }

    @NotNull
    @Override
    public ActivationConfiguration merge(@NotNull PlacementModifierConfiguration first, @NotNull PlacementModifierConfiguration second) {
        return new ActivationConfiguration(this,
                ((ActivationConfiguration) first).getActivate() != null ? ((ActivationConfiguration) first).getActivate() : ((ActivationConfiguration) second).getActivate());
    }

    @NotNull
    @Override
    public Stream<BlockVector> getPositions(@NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull BlockVector position, @NotNull LimitedRegion limitedRegion, @NotNull ActivationConfiguration configuration) {
        if (configuration.getActivate() == null || configuration.getActivate().getValue(worldInfo, random, position, limitedRegion)) {
            return Stream.of(position);
        }

        return Stream.empty();
    }

    @NotNull
    @Override
    public NamespacedKey getKey() {
        return KEY;
    }
}
