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

package de.derfrzocker.ore.control.api.config;

import com.mojang.serialization.Codec;
import de.derfrzocker.feature.api.ExtraValues;
import de.derfrzocker.feature.api.FeatureGeneratorConfiguration;
import de.derfrzocker.feature.api.PlacementModifierConfiguration;
import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.config.dao.ConfigDao;
import de.derfrzocker.ore.control.api.config.dao.ConfigInfoDao;
import de.derfrzocker.ore.control.api.config.dao.ExtraValueDao;
import de.derfrzocker.ore.control.api.util.Reloadable;
import org.bukkit.NamespacedKey;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigManager implements Reloadable {

    private final ConfigDao configDao;
    private final ConfigInfoDao configInfoDao;
    private final ExtraValueDao extraValueDao;
    private final Set<ConfigInfo> configInfos = new HashSet<>();
    private final Map<String, ConfigInfo> worldNames = new ConcurrentHashMap<>();
    // TODO clean up
    // Generation
    private final Map<ConfigInfo, Optional<ExtraValues>> extraValuesCache = new ConcurrentHashMap<>();
    private final Map<ConfigInfo, Map<NamespacedKey, Optional<Config>>> configCache = new ConcurrentHashMap<>();
    private final Map<ConfigInfo, Map<Biome, Map<NamespacedKey, Optional<Config>>>> biomeConfigCache = new ConcurrentHashMap<>();
    // GUI
    private final Map<ConfigInfo, Optional<ExtraValues>> guiExtraValuesCache = new ConcurrentHashMap<>();
    private final Map<ConfigInfo, Map<NamespacedKey, Optional<Config>>> guiConfigCache = new ConcurrentHashMap<>();
    private final Map<ConfigInfo, Map<Biome, Map<NamespacedKey, Optional<Config>>>> guiBiomeConfigCache = new ConcurrentHashMap<>();
    // Default
    private final Map<NamespacedKey, Optional<Config>> defaultConfigCache = new ConcurrentHashMap<>();
    private final Map<Biome, Map<NamespacedKey, Optional<Config>>> defaultBiomeConfigCache = new ConcurrentHashMap<>();
    private final Map<String, Map<Biome, Map<NamespacedKey, Optional<Config>>>> generationConfigCache = new ConcurrentHashMap<>();
    private ConfigInfo globalConfigInfo;

    public ConfigManager(ConfigDao configDao, ConfigInfoDao configInfoDao, ExtraValueDao extraValueDao) {
        this.configDao = configDao;
        this.configInfoDao = configInfoDao;
        this.extraValueDao = extraValueDao;
    }

    @Override
    public void reload() {
        globalConfigInfo = configInfoDao.getGlobalConfig();

        worldNames.clear();
        configInfos.clear();
        for (ConfigInfo configInfo : configInfoDao.getConfigInfos()) {
            worldNames.put(configInfo.getWorldName(), configInfo);
            configInfos.add(configInfo);
        }

        configCache.clear();
        biomeConfigCache.clear();
        guiConfigCache.clear();
        guiBiomeConfigCache.clear();
        generationConfigCache.clear();
    }

    public void save() {
        // saving config infos
        for (ConfigInfo configInfo : configInfos) {
            if (configInfo.isDirty()) {
                configInfoDao.save(configInfo);
                configInfo.saved();
            }
        }

        // saving world specific values
        for (Map.Entry<ConfigInfo, Map<NamespacedKey, Optional<Config>>> configInfoEntry : configCache.entrySet()) {
            ConfigInfo configInfo = configInfoEntry.getKey();
            for (Map.Entry<NamespacedKey, Optional<Config>> featureEntry : configInfoEntry.getValue().entrySet()) {
                if (featureEntry.getValue().isEmpty()) {
                    continue;
                }

                Config config = featureEntry.getValue().get();

                if (config.isDirty()) {
                    configDao.saveConfig(configInfo, featureEntry.getKey(), config);
                    config.saved();
                }
            }
        }

        // saving biome specific values
        for (Map.Entry<ConfigInfo, Map<Biome, Map<NamespacedKey, Optional<Config>>>> configInfoEntry : biomeConfigCache.entrySet()) {
            ConfigInfo configInfo = configInfoEntry.getKey();
            for (Map.Entry<Biome, Map<NamespacedKey, Optional<Config>>> biomeEntry : configInfoEntry.getValue().entrySet()) {
                Biome biome = biomeEntry.getKey();
                for (Map.Entry<NamespacedKey, Optional<Config>> featureEntry : biomeEntry.getValue().entrySet()) {
                    if (featureEntry.getValue().isEmpty()) {
                        continue;
                    }

                    Config config = featureEntry.getValue().get();

                    if (config.isDirty()) {
                        configDao.saveConfig(configInfo, biome, featureEntry.getKey(), config);
                        config.saved();
                    }
                }
            }
        }
    }

    public void saveAndReload() {
        save();
        reload();
    }

    public Set<ConfigInfo> getConfigInfos() {
        Set<ConfigInfo> configInfos = new LinkedHashSet<>();
        Set<ConfigInfo> tmpConfigInfos = new LinkedHashSet<>();
        configInfos.add(globalConfigInfo);

        for (ConfigInfo configInfo : this.configInfos) {
            if (configInfo.getConfigType() == ConfigType.WORLD) {
                configInfos.add(configInfo);
            } else {
                tmpConfigInfos.add(configInfo);
            }
        }

        configInfos.addAll(tmpConfigInfos);

        return configInfos;
    }

    public ConfigInfo getOrCreateConfigInfo(String worldName) {
        ConfigInfo configInfo = worldNames.get(worldName);
        if (configInfo == null) {
            configInfo = configInfoDao.createConfigInfo(worldName);
            configInfos.add(configInfo);
            worldNames.put(worldName, configInfo);
        }

        return configInfo;
    }

    public Optional<ExtraValues> getExtraValues(ConfigInfo configInfo) {
        return extraValuesCache.computeIfAbsent(configInfo, configInfo1 -> extraValueDao.getExtraValues(configInfo));
    }

    public Optional<Config> getConfig(ConfigInfo configInfo, NamespacedKey key) {
        return configCache.
                computeIfAbsent(configInfo, info -> new ConcurrentHashMap<>()).
                computeIfAbsent(key, namespacedKey -> configDao.getConfig(configInfo, key));
    }

    public Optional<Config> getConfig(ConfigInfo configInfo, Biome biome, NamespacedKey key) {
        return biomeConfigCache.
                computeIfAbsent(configInfo, info -> new ConcurrentHashMap<>()).
                computeIfAbsent(biome, biome1 -> new ConcurrentHashMap<>()).
                computeIfAbsent(key, namespacedKey -> configDao.getConfig(configInfo, biome, key));
    }

    public Config getOrCreateConfig(ConfigInfo configInfo, NamespacedKey key) {
        Optional<Config> optionalConfig = getConfig(configInfo, key);

        if (optionalConfig.isPresent()) {
            return optionalConfig.get();
        }

        Config config = new Config();
        configCache.
                computeIfAbsent(configInfo, info -> new ConcurrentHashMap<>()).
                put(key, Optional.of(config));

        return config;
    }

    public Config getOrCreateConfig(ConfigInfo configInfo, Biome biome, NamespacedKey key) {
        Optional<Config> optionalConfig = getConfig(configInfo, biome, key);

        if (!optionalConfig.isEmpty()) {
            return optionalConfig.get();
        }

        Config config = new Config();
        biomeConfigCache.
                computeIfAbsent(configInfo, info -> new ConcurrentHashMap<>()).
                computeIfAbsent(biome, biome1 -> new ConcurrentHashMap<>()).
                put(key, Optional.of(config));

        return config;
    }

    public Optional<Config> getDefaultConfig(NamespacedKey key) {
        return defaultConfigCache.getOrDefault(key, Optional.empty());
    }

    public Optional<Config> getDefaultConfig(Biome biome, NamespacedKey key) {
        Map<NamespacedKey, Optional<Config>> biomes = defaultBiomeConfigCache.get(biome);

        if (biomes == null) {
            return Optional.empty();
        }

        return biomes.getOrDefault(key, Optional.empty());
    }

    public Optional<Config> getGuiConfig(ConfigInfo configInfo, NamespacedKey key) {
        return guiConfigCache.
                computeIfAbsent(configInfo, info -> new ConcurrentHashMap<>()).
                computeIfAbsent(key, namespaceKey -> loadGuiConfig(configInfo, key));
    }

    public Optional<Config> getGuiConfig(ConfigInfo configInfo, Biome biome, NamespacedKey key) {
        return guiBiomeConfigCache.
                computeIfAbsent(configInfo, info -> new ConcurrentHashMap<>()).
                computeIfAbsent(biome, bio -> new ConcurrentHashMap<>()).
                computeIfAbsent(key, namespaceKey -> loadGuiConfig(configInfo, biome, key));
    }

    private Optional<Config> loadGuiConfig(ConfigInfo configInfo, Biome biome, NamespacedKey key) {
        Optional<Config> defaultWorldConfig = getDefaultConfig(key);
        Optional<Config> defaultBiomeConfig = getDefaultConfig(biome, key);
        Optional<Config> globalWorldConfig = getConfig(globalConfigInfo, key);
        Optional<Config> globalBiomeConfig = getConfig(globalConfigInfo, biome, key);
        Optional<Config> worldConfig = getConfig(configInfo, key);
        Optional<Config> biomeConfig = getConfig(configInfo, biome, key);

        return combineConfig(combineConfig(combineConfig(combineConfig(combineConfig(biomeConfig, worldConfig), globalBiomeConfig), globalWorldConfig), defaultBiomeConfig), defaultWorldConfig);
    }

    private Optional<Config> loadGuiConfig(ConfigInfo configInfo, NamespacedKey key) {
        Optional<Config> defaultWorldConfig = getDefaultConfig(key);
        Optional<Config> globalWorldConfig = getConfig(globalConfigInfo, key);
        Optional<Config> worldConfig = getConfig(configInfo, key);

        return combineConfig(combineConfig(worldConfig, globalWorldConfig), defaultWorldConfig);
    }

    public Optional<Config> getGenerationConfig(String worldName, Biome biome, NamespacedKey key) {
        return generationConfigCache.
                computeIfAbsent(worldName, name -> new ConcurrentHashMap<>()).
                computeIfAbsent(biome, bio -> new ConcurrentHashMap<>()).
                computeIfAbsent(key, namespacedKey -> loadGenerationConfig(worldName, biome, key));
    }

    private Optional<Config> loadGenerationConfig(String worldName, Biome biome, NamespacedKey key) {
        ConfigInfo configInfo = worldNames.get(worldName);

        Optional<Config> globalWorldConfig = getConfig(globalConfigInfo, key);
        Optional<Config> globalBiomeConfig = getConfig(globalConfigInfo, biome, key);

        if (configInfo == null) {
            return combineConfig(globalBiomeConfig, globalWorldConfig);
        }

        Optional<Config> worldConfig = getConfig(configInfo, key);
        Optional<Config> biomeConfig = getConfig(configInfo, biome, key);

        return combineConfig(combineConfig(combineConfig(biomeConfig, worldConfig), globalBiomeConfig), globalWorldConfig);
    }

    private Optional<Config> combineConfig(Optional<Config> primaryConfig, Optional<Config> secondaryConfig) {
        if (primaryConfig.isEmpty() && secondaryConfig.isEmpty()) {
            return primaryConfig;
        }

        if (secondaryConfig.isEmpty()) {
            return primaryConfig;
        }

        if (primaryConfig.isEmpty()) {
            return secondaryConfig;
        }

        Config primary = primaryConfig.get();
        Config secondary = secondaryConfig.get();
        List<PlacementModifierConfiguration> primaryPlacements = new ArrayList<>();
        List<PlacementModifierConfiguration> secondaryPlacements = new ArrayList<>();
        List<PlacementModifierConfiguration> placements = new ArrayList<>();

        if (primary.getPlacements() != null) {
            primaryPlacements.addAll(primary.getPlacements().values());
        }

        if (secondary.getPlacements() != null) {
            secondaryPlacements.addAll(secondary.getPlacements().values());
        }

        for (PlacementModifierConfiguration first : primaryPlacements) {
            PlacementModifierConfiguration second = null;
            for (PlacementModifierConfiguration temp : secondaryPlacements) {
                if (first.getOwner() == temp.getOwner()) {
                    second = temp;
                    break;
                }
            }

            if (second != null) {
                placements.add(first.getOwner().merge(first, second));
                secondaryPlacements.remove(second);
            } else {
                placements.add(first);
            }
        }

        placements.addAll(secondaryPlacements);

        FeatureGeneratorConfiguration configuration;
        if (primary.getFeature() != null && secondary.getFeature() != null) {
            configuration = primary.getFeature().getOwner().merge(primary.getFeature(), secondary.getFeature());
        } else if (primary.getFeature() != null) {
            configuration = primary.getFeature();
        } else {
            configuration = secondary.getFeature();
        }

        return Optional.of(new Config(placements, configuration));
    }

    @Deprecated
    public Codec<Config> getConfigCodec() {
        return configDao.getConfigCodec();
    }

    public void setDefaultConfig(NamespacedKey feature, Config config) {
        defaultConfigCache.put(feature, Optional.ofNullable(config));
    }

    public void setDefaultConfig(Biome biome, NamespacedKey feature, Config config) {
        defaultBiomeConfigCache.
                computeIfAbsent(biome, bio -> new ConcurrentHashMap<>()).
                put(feature, Optional.ofNullable(config));
    }

    public void clearGuiConfigCache(ConfigInfo configInfo, NamespacedKey key) {
        guiConfigCache.get(configInfo).remove(key);
    }

    public void clearGuiConfigCache(ConfigInfo configInfo, Biome biome, NamespacedKey key) {
        guiBiomeConfigCache.get(configInfo).get(biome).remove(key);
    }
}
