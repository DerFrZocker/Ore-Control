package de.derfrzocker.ore.control.cache.config;

import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.config.Config;
import de.derfrzocker.ore.control.api.config.ConfigInfo;
import org.bukkit.NamespacedKey;

import java.util.Optional;

import static de.derfrzocker.ore.control.cache.config.ConfigCache.combineConfig;

public class GenerationConfigCache {
    private final BiomeConfigCacheSlice biomeCache = new BiomeConfigCacheSlice();
    private final StandardConfigCache standardConfigCache;

    public GenerationConfigCache(StandardConfigCache standardConfigCache) {
        this.standardConfigCache = standardConfigCache;
    }

    public Optional<Config> getGeneration(ConfigInfo configInfo, Biome biome, NamespacedKey featureKey) {
        return biomeCache.getOrCreate(configInfo).getOrCreate(biome).getOrCompute(featureKey, () -> loadGenerationConfig(configInfo, biome, featureKey));
    }

    public void clear() {
        biomeCache.clear();
    }

    private Optional<Config> loadGenerationConfig(ConfigInfo configInfo, Biome biome, NamespacedKey featureKey) {
        Optional<Config> globalWorldConfig = standardConfigCache.getGlobal(featureKey);
        Optional<Config> globalBiomeConfig = standardConfigCache.getGlobal(biome, featureKey);
        Optional<Config> worldConfig = standardConfigCache.get(configInfo, featureKey);
        Optional<Config> biomeConfig = standardConfigCache.get(configInfo, biome, featureKey);

        return combineConfig(combineConfig(combineConfig(biomeConfig, worldConfig), globalBiomeConfig), globalWorldConfig);
    }
}
