package de.derfrzocker.ore.control.cache.config.part;

import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.config.Config;
import de.derfrzocker.spigot.utils.function.TripleConsumer;
import org.bukkit.NamespacedKey;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class BiomeConfigCachePart {
    private final Map<Biome, FeatureConfigCachePart> cache = new ConcurrentHashMap<>();

    public void forEachFlatMap(TripleConsumer<Biome, NamespacedKey, Optional<Config>> consumer) {
        cache.forEach((biome, part) -> part.forEach((featureKey, config) -> consumer.accept(biome, featureKey, config)));
    }

    public FeatureConfigCachePart getOrCreate(Biome biome) {
        return cache.computeIfAbsent(biome, ignore -> new FeatureConfigCachePart());
    }

    public Optional<FeatureConfigCachePart> get(Biome biome) {
        return Optional.ofNullable(cache.get(biome));
    }
}
