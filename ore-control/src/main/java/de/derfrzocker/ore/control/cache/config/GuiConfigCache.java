package de.derfrzocker.ore.control.cache.config;

import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.config.Config;
import de.derfrzocker.ore.control.api.config.ConfigInfo;
import org.bukkit.NamespacedKey;

import java.util.Optional;

import static de.derfrzocker.ore.control.cache.config.ConfigCache.combineConfig;

public class GuiConfigCache {

    private final BiomeConfigCacheSlice biomeCache = new BiomeConfigCacheSlice();
    private final ConfigCacheSlice cache = new ConfigCacheSlice();
    private final DefaultConfigCache defaultConfigCache;
    private final StandardConfigCache standardConfigCache;

    public GuiConfigCache(DefaultConfigCache defaultConfigCache, StandardConfigCache standardConfigCache) {
        this.defaultConfigCache = defaultConfigCache;
        this.standardConfigCache = standardConfigCache;
    }

    public void clear() {
        biomeCache.clear();
        cache.clear();
    }

    public Optional<Config> getGuiConfig(ConfigInfo configInfo, NamespacedKey featureKey) {
        return cache.getOrCreate(configInfo).getOrCompute(featureKey, () -> loadGuiConfig(configInfo, featureKey));
    }

    public Optional<Config> getGuiConfig(ConfigInfo configInfo, Biome biome, NamespacedKey featureKey) {
        return biomeCache.getOrCreate(configInfo).getOrCreate(biome).getOrCompute(featureKey, () -> loadGuiConfig(configInfo, biome, featureKey));
    }

    public void clearGuiConfigCache(ConfigInfo configInfo, NamespacedKey featureKey) {
        cache.get(configInfo).ifPresent(part -> part.clear(featureKey));
    }

    public void clearGuiConfigCache(ConfigInfo configInfo, Biome biome, NamespacedKey featureKey) {
        biomeCache.get(configInfo).flatMap(part -> part.get(biome)).ifPresent(part -> part.clear(featureKey));
    }

    private Optional<Config> loadGuiConfig(ConfigInfo configInfo, Biome biome, NamespacedKey key) {
        Optional<Config> defaultWorldConfig = defaultConfigCache.getDefault(key);
        Optional<Config> defaultBiomeConfig = defaultConfigCache.getDefault(biome, key);
        Optional<Config> globalWorldConfig = standardConfigCache.getGlobal(key);
        Optional<Config> globalBiomeConfig = standardConfigCache.getGlobal(biome, key);
        Optional<Config> worldConfig = standardConfigCache.get(configInfo, key);
        Optional<Config> biomeConfig = standardConfigCache.get(configInfo, biome, key);

        return combineConfig(combineConfig(combineConfig(combineConfig(combineConfig(biomeConfig, worldConfig), globalBiomeConfig), globalWorldConfig), defaultBiomeConfig), defaultWorldConfig);
    }

    private Optional<Config> loadGuiConfig(ConfigInfo configInfo, NamespacedKey key) {
        Optional<Config> defaultWorldConfig = defaultConfigCache.getDefault(key);
        Optional<Config> globalWorldConfig = standardConfigCache.getGlobal(key);
        Optional<Config> worldConfig = standardConfigCache.get(configInfo, key);

        return combineConfig(combineConfig(worldConfig, globalWorldConfig), defaultWorldConfig);
    }
}
