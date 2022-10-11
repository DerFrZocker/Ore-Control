package de.derfrzocker.ore.control.cache.config;

import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.config.Config;
import de.derfrzocker.ore.control.api.config.ConfigInfo;
import de.derfrzocker.ore.control.api.config.dao.ConfigDao;
import org.bukkit.NamespacedKey;

import java.util.Optional;
import java.util.function.Supplier;

public class StandardConfigCache {
    private final BiomeConfigCacheSlice biomeCache = new BiomeConfigCacheSlice();
    private final ConfigCacheSlice cache = new ConfigCacheSlice();
    private final ConfigDao configDao;
    private final Supplier<ConfigInfo> global;

    public StandardConfigCache(ConfigDao configDao, Supplier<ConfigInfo> global) {
        this.configDao = configDao;
        this.global = global;
    }

    public void save() {
        // saving world specific values
        cache.forEachFlatMap((configInfo, featureKey, config) -> {
            if (config.isEmpty()) {
                return;
            }

            if (config.get().isDirty()) {
                configDao.saveConfig(configInfo, featureKey, config.get());
                config.get().saved();
            }
        });

        // saving biome specific values
        biomeCache.forEachFlatMap((configInfo, biome, featureKey, config) -> {
            if (config.isEmpty()) {
                return;
            }

            if (config.get().isDirty()) {
                configDao.saveConfig(configInfo, biome, featureKey, config.get());
                config.get().saved();
            }
        });
    }

    public void clear() {
        biomeCache.clear();
        cache.clear();
    }

    public Optional<Config> get(ConfigInfo configInfo, NamespacedKey featureKey) {
        return cache.getOrCreate(configInfo).getOrCompute(featureKey, () -> configDao.getConfig(configInfo, featureKey));
    }

    public Optional<Config> get(ConfigInfo configInfo, Biome biome, NamespacedKey featureKey) {
        return biomeCache.getOrCreate(configInfo).getOrCreate(biome).getOrCompute(featureKey, () -> configDao.getConfig(configInfo, biome, featureKey));
    }

    public Optional<Config> getGlobal(NamespacedKey featureKey) {
        return get(global.get(), featureKey);
    }

    public Optional<Config> getGlobal(Biome biome, NamespacedKey featureKey) {
        return get(global.get(), biome, featureKey);
    }

    public Config getOrCreate(ConfigInfo configInfo, NamespacedKey featureKey) {
        return cache.getOrCreate(configInfo).getOrCompute(featureKey, () -> Optional.of(configDao.getConfig(configInfo, featureKey).orElseGet(Config::new))).get();
    }

    public Config getOrCreate(ConfigInfo configInfo, Biome biome, NamespacedKey featureKey) {
        return biomeCache.getOrCreate(configInfo).getOrCreate(biome).getOrCompute(featureKey, () -> Optional.of(configDao.getConfig(configInfo, featureKey).orElseGet(Config::new))).get();
    }
}
