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

package de.derfrzocker.ore.control.api.dao;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.derfrzocker.feature.api.FeatureGenerator;
import de.derfrzocker.feature.api.FeatureGeneratorConfiguration;
import de.derfrzocker.feature.api.FeaturePlacementModifier;
import de.derfrzocker.feature.api.PlacementModifierConfiguration;
import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.Config;
import de.derfrzocker.ore.control.api.OreControlRegistries;
import org.bukkit.NamespacedKey;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigDao {

    private final Map<String, Map<Biome, Map<NamespacedKey, Config>>> cache = new ConcurrentHashMap<>();
    private final OreControlRegistries registries;
    private final File directory;
    private final Codec<Config> configCodec;

    public ConfigDao(OreControlRegistries registries, File directory) {
        this.registries = registries;
        this.directory = directory;
        this.configCodec = RecordCodecBuilder.create((builder) -> builder.group(
                Codec.list(registries.getPlacementModifierRegistry().
                                dispatch("placement_modifier_type", PlacementModifierConfiguration::getPlacementModifier, FeaturePlacementModifier::getCodec)).
                        optionalFieldOf("placement_modifier_configurations").
                        forGetter(config -> Optional.ofNullable(config.getPlacements())),
                registries.getFeatureGeneratorRegistry().dispatch("feature_generator_type", FeatureGeneratorConfiguration::getFeatureGenerator, FeatureGenerator::getCodec).
                        optionalFieldOf("feature_generator_configuration").
                        forGetter(config -> Optional.ofNullable(config.getFeature()))
        ).apply(builder, (placementModifierConfigurations, featureGeneratorConfiguration) -> new Config(placementModifierConfigurations.orElse(null), featureGeneratorConfiguration.orElse(null))));
    }

    public Codec<Config> getConfigCodec() {
        return this.configCodec;
    }

    public Optional<Config> getConfig(String worldName, Biome biome, NamespacedKey key) {
        return Optional.ofNullable(cache.computeIfAbsent(worldName, name -> new ConcurrentHashMap<>()).computeIfAbsent(biome, bio -> new ConcurrentHashMap<>()).computeIfAbsent(key, namespacedKey -> load(worldName, biome, key)));
    }

    private Config load(String worldName, Biome biome, NamespacedKey key) {
        File worldFolder = new File(directory, worldName);
        if (!worldFolder.exists()) {
            return null;
        }

        File biomeValues = new File(worldFolder, "biome/" + biome.getKey().getNamespace() + "/" + biome.getKey().getKey() + "/" + key.getNamespace() + "/" + key.getKey() + ".json");
        File worldValues = new File(worldFolder, "world/" + key.getNamespace() + "/" + key.getKey() + ".json");

        if (!biomeValues.exists() && !worldValues.exists()) {
            return null;
        }

        Config biomeConfig = null;
        if (biomeValues.exists()) {
            try {
                JsonElement json = JsonParser.parseString(Files.readString(biomeValues.toPath()));
                biomeConfig = configCodec.decode(JsonOps.INSTANCE, json).get().left().get().getFirst();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        Config worldConfig = null;
        if (worldValues.exists()) {
            try {
                JsonElement json = JsonParser.parseString(Files.readString(worldValues.toPath()));
                worldConfig = configCodec.decode(JsonOps.INSTANCE, json).get().left().get().getFirst();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if (worldConfig == null) {
            return biomeConfig;
        }

        if (biomeConfig == null) {
            return worldConfig;
        }


        List<PlacementModifierConfiguration> biomePlacements = new ArrayList<>();
        List<PlacementModifierConfiguration> worldPlacements = new ArrayList<>();
        List<PlacementModifierConfiguration> placements = new ArrayList<>();

        if (biomeConfig.getPlacements() != null) {
            biomePlacements.addAll(biomeConfig.getPlacements());
        }

        if (worldConfig.getPlacements() != null) {
            worldPlacements.addAll(worldConfig.getPlacements());
        }

        for (PlacementModifierConfiguration first : biomePlacements) {
            PlacementModifierConfiguration second = null;
            for (PlacementModifierConfiguration temp : worldPlacements) {
                if (first.getPlacementModifier() == temp.getPlacementModifier()) {
                    second = temp;
                    break;
                }
            }

            if (second != null) {
                placements.add(first.getPlacementModifier().merge(first, second));
                worldPlacements.remove(second);
            } else {
                placements.add(first);
            }
        }

        placements.addAll(worldPlacements);

        FeatureGeneratorConfiguration configuration;
        if (biomeConfig.getFeature() != null && worldConfig.getFeature() != null) {
            configuration = biomeConfig.getFeature().getFeatureGenerator().merge(biomeConfig.getFeature(), worldConfig.getFeature());
        } else if (biomeConfig.getFeature() != null) {
            configuration = biomeConfig.getFeature();
        } else {
            configuration = worldConfig.getFeature();
        }

        return new Config(placements, configuration);
    }
}
