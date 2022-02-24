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

package de.derfrzocker.ore.control.api.config.dao;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.derfrzocker.feature.api.FeatureGenerator;
import de.derfrzocker.feature.api.FeatureGeneratorConfiguration;
import de.derfrzocker.feature.api.FeaturePlacementModifier;
import de.derfrzocker.feature.api.PlacementModifierConfiguration;
import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.OreControlRegistries;
import de.derfrzocker.ore.control.api.config.Config;
import de.derfrzocker.ore.control.api.config.ConfigInfo;
import org.bukkit.NamespacedKey;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

public class ConfigDao {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Codec<Config> configCodec;

    public ConfigDao(OreControlRegistries registries) {
        this.configCodec = RecordCodecBuilder.create((builder) -> builder.group(
                Codec.list(registries.getPlacementModifierRegistry().
                                dispatch("placement_modifier_type", PlacementModifierConfiguration::getOwner, FeaturePlacementModifier::getCodec)).
                        optionalFieldOf("placement_modifier_configurations").
                        forGetter(config -> Optional.ofNullable(config.getPlacements())),
                registries.getFeatureGeneratorRegistry().dispatch("feature_generator_type", FeatureGeneratorConfiguration::getOwner, FeatureGenerator::getCodec).
                        optionalFieldOf("feature_generator_configuration").
                        forGetter(config -> Optional.ofNullable(config.getFeature()))
        ).apply(builder, (placementModifierConfigurations, featureGeneratorConfiguration) -> new Config(placementModifierConfigurations.orElse(null), featureGeneratorConfiguration.orElse(null))));
    }

    public Codec<Config> getConfigCodec() {
        return this.configCodec;
    }

    public Optional<Config> getConfig(ConfigInfo configInfo, NamespacedKey key) {
        return load(getConfigFile(configInfo, key));
    }

    public Optional<Config> getConfig(ConfigInfo configInfo, Biome biome, NamespacedKey key) {
        return load(getConfigFile(configInfo, biome, key));
    }

    public void saveConfig(ConfigInfo configInfo, NamespacedKey key, Config config) {
        if (config == null) {
            configInfo.getDataDirectory().delete();
            return;
        }

        save(getConfigFile(configInfo, key), config);
    }

    public void saveConfig(ConfigInfo configInfo, Biome biome, NamespacedKey key, Config config) {
        if (config == null) {
            configInfo.getDataDirectory().delete();
            return;
        }

        save(getConfigFile(configInfo, biome, key), config);
    }

    private File getConfigFile(ConfigInfo configInfo, NamespacedKey key) {
        return new File(configInfo.getDataDirectory(), "world/" + key.getNamespace() + "/" + key.getKey() + ".json");
    }

    private File getConfigFile(ConfigInfo configInfo, Biome biome, NamespacedKey key) {
        return new File(configInfo.getDataDirectory(), "biome/" + biome.getKey().getNamespace() + "/" + biome.getKey().getKey() + "/" + key.getNamespace() + "/" + key.getKey() + ".json");
    }

    private Optional<Config> load(File file) {
        if (!file.exists()) {
            return Optional.empty();
        }

        try {
            JsonElement json = JsonParser.parseString(Files.readString(file.toPath()));
            return Optional.of(configCodec.decode(JsonOps.INSTANCE, json).get().left().get().getFirst());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void save(File file, Config config) {
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        DataResult<JsonElement> result = configCodec.encodeStart(JsonOps.INSTANCE, config);
        JsonElement element = result.get().left().get();
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(GSON.toJson(element));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
