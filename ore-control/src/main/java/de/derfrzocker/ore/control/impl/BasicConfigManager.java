package de.derfrzocker.ore.control.impl;

import com.mojang.serialization.Codec;
import de.derfrzocker.feature.api.ExtraValues;
import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.config.Config;
import de.derfrzocker.ore.control.api.config.ConfigInfo;
import de.derfrzocker.ore.control.api.config.ConfigManager;
import de.derfrzocker.ore.control.cache.config.ConfigCache;
import de.derfrzocker.ore.control.cache.extra.ExtraValueCache;
import de.derfrzocker.ore.control.cache.info.ConfigInfoCache;
import org.bukkit.NamespacedKey;

import java.util.Optional;
import java.util.Set;

public class BasicConfigManager implements ConfigManager {

    private final ConfigInfoCache configInfoCache;
    private final ExtraValueCache extraValueCache;
    private final ConfigCache configCache;

    public BasicConfigManager(ConfigInfoCache configInfoCache, ExtraValueCache extraValueCache, ConfigCache configCache) {
        this.configInfoCache = configInfoCache;
        this.extraValueCache = extraValueCache;
        this.configCache = configCache;
    }

    @Override
    public void save() {
        configInfoCache.save();
        configCache.save();
    }

    @Override
    public void reload() {
        configInfoCache.reload();
        configCache.clear();
    }

    @Override
    public void saveAndReload() {
        save();
        reload();
    }

    @Override
    public Set<ConfigInfo> getConfigInfos() {
        return configInfoCache.getConfigInfos();
    }

    @Override
    public ConfigInfo getOrCreateConfigInfo(String worldName) {
        return configInfoCache.getOrCreateConfigInfo(worldName);
    }

    @Override
    public Optional<ExtraValues> getExtraValues(ConfigInfo configInfo) {
        return extraValueCache.getExtraValues(configInfo);
    }

    @Override
    public Optional<ExtraValues> getGuiExtraValues(ConfigInfo configInfo) {
        return extraValueCache.getGuiExtraValues(configInfo);
    }

    @Override
    public Optional<ExtraValues> getGenerationExtraValues(ConfigInfo configInfo) {
        return extraValueCache.getGenerationExtraValues(configInfo);
    }

    @Override
    public Optional<Config> getConfig(ConfigInfo configInfo, NamespacedKey featureKey) {
        return configCache.getConfig(configInfo, featureKey);
    }

    @Override
    public Optional<Config> getConfig(ConfigInfo configInfo, Biome biome, NamespacedKey featureKey) {
        return configCache.getConfig(configInfo, biome, featureKey);
    }

    @Override
    public Config getOrCreateConfig(ConfigInfo configInfo, NamespacedKey featureKey) {
        return configCache.getOrCreateConfig(configInfo, featureKey);
    }

    @Override
    public Config getOrCreateConfig(ConfigInfo configInfo, Biome biome, NamespacedKey featureKey) {
        return configCache.getOrCreateConfig(configInfo, biome, featureKey);
    }

    @Override
    public Optional<Config> getGuiConfig(ConfigInfo configInfo, NamespacedKey featureKey) {
        return configCache.getGuiConfig(configInfo, featureKey);
    }

    @Override
    public Optional<Config> getGuiConfig(ConfigInfo configInfo, Biome biome, NamespacedKey featureKey) {
        return configCache.getGuiConfig(configInfo, biome, featureKey);
    }

    @Override
    public void clearGuiConfigCache(ConfigInfo configInfo, NamespacedKey featureKey) {
        configCache.clearGuiConfigCache(configInfo, featureKey);
    }

    @Override
    public void clearGuiConfigCache(ConfigInfo configInfo, Biome biome, NamespacedKey featureKey) {
        configCache.clearGuiConfigCache(configInfo, biome, featureKey);
    }

    @Override
    public Optional<Config> getGenerationConfig(ConfigInfo configInfo, Biome biome, NamespacedKey featureKey) {
        return configCache.getGenerationConfig(configInfo, biome, featureKey);
    }

    @Override
    public Optional<Config> getDefaultConfig(NamespacedKey featureKey) {
        return configCache.getDefaultConfig(featureKey);
    }

    @Override
    public Optional<Config> getDefaultConfig(Biome biome, NamespacedKey featureKey) {
        return configCache.getDefaultConfig(biome, featureKey);
    }

    @Override
    public void setDefaultConfig(NamespacedKey featureKey, Config config) {
        configCache.setDefaultConfig(featureKey, config);
    }

    @Override
    public void setDefaultConfig(Biome biome, NamespacedKey featureKey, Config config) {
        configCache.setDefaultConfig(biome, featureKey, config);
    }
}
