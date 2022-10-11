package de.derfrzocker.ore.control.cache.config;

import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.config.Config;
import de.derfrzocker.ore.control.cache.config.part.BiomeConfigCachePart;
import de.derfrzocker.ore.control.cache.config.part.FeatureConfigCachePart;
import org.bukkit.NamespacedKey;

import java.util.Optional;

public class DefaultConfigCache {
    private final BiomeConfigCachePart biomeCache = new BiomeConfigCachePart();
    private final FeatureConfigCachePart cache = new FeatureConfigCachePart();

    public Optional<Config> getDefault(NamespacedKey featureKey) {
        return cache.getOrCompute(featureKey, Optional::empty);
    }

    public Optional<Config> getDefault(Biome biome, NamespacedKey featureKey) {
        return biomeCache.getOrCreate(biome).getOrCompute(featureKey, Optional::empty);
    }

    public void setDefault(NamespacedKey featureKey, Config config) {
        cache.set(featureKey, Optional.ofNullable(config));
    }

    public void setDefault(Biome biome, NamespacedKey featureKey, Config config) {
        biomeCache.getOrCreate(biome).set(featureKey, Optional.ofNullable(config));
    }
}
