package de.derfrzocker.ore.control.cache.config.part;

import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.config.Config;
import org.apache.logging.log4j.util.TriConsumer;
import org.bukkit.NamespacedKey;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class BiomeConfigCachePart {
    private final Map<Biome, FeatureConfigCachePart> cache = new ConcurrentHashMap<>();

    public void forEachFlatMap(TriConsumer<Biome, NamespacedKey, Optional<Config>> consumer) { // TODO add and use Spigot Utils
        cache.forEach((biome, part) -> part.forEach((featureKey, config) -> consumer.accept(biome, featureKey, config)));
    }

    public FeatureConfigCachePart getOrCreate(Biome biome) {
        return cache.computeIfAbsent(biome, ignore -> new FeatureConfigCachePart());
    }

    public Optional<FeatureConfigCachePart> get(Biome biome) {
        return Optional.ofNullable(cache.get(biome));
    }
}
