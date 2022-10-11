package de.derfrzocker.ore.control.cache.config;

import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.config.Config;
import de.derfrzocker.ore.control.api.config.ConfigInfo;
import de.derfrzocker.ore.control.cache.config.part.BiomeConfigCachePart;
import org.bukkit.NamespacedKey;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class BiomeConfigCacheSlice {
    private final Map<ConfigInfo, BiomeConfigCachePart> cache = new ConcurrentHashMap<>();

    public void forEachFlatMap(QuadConsumer<ConfigInfo, Biome, NamespacedKey, Optional<Config>> consumer) { // Add and use Spigot Utils
        cache.forEach((configInfo, part) -> part.forEachFlatMap((biome, featureKey, config) -> consumer.accept(configInfo, biome, featureKey, config)));
    }

    public BiomeConfigCachePart getOrCreate(ConfigInfo configInfo) {
        return cache.computeIfAbsent(configInfo, ignore -> new BiomeConfigCachePart());
    }

    public Optional<BiomeConfigCachePart> get(ConfigInfo configInfo) {
        return Optional.ofNullable(cache.get(configInfo));
    }

    public void clear() {
        cache.clear();
    }

    @FunctionalInterface
    public interface QuadConsumer<F, S, T, Q> {
        void accept(F first, S second, T third, Q fourth);
    }
}
