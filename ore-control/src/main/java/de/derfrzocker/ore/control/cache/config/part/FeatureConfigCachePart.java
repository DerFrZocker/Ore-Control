package de.derfrzocker.ore.control.cache.config.part;

import de.derfrzocker.ore.control.api.config.Config;
import org.bukkit.NamespacedKey;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class FeatureConfigCachePart {
    private final Map<NamespacedKey, Optional<Config>> cache = new ConcurrentHashMap<>();

    public void forEach(BiConsumer<NamespacedKey, Optional<Config>> consumer) {
        cache.forEach(consumer);
    }

    public Optional<Config> getOrCompute(NamespacedKey featureKey, Supplier<Optional<Config>> supplier) {
        return cache.computeIfAbsent(featureKey, ignore -> supplier.get());
    }

    public Config getOrCreate(NamespacedKey featureKey) {
        Optional<Config> optional = cache.getOrDefault(featureKey, Optional.empty());

        if (optional.isPresent()) {
            return optional.get();
        }

        Config config = new Config();
        cache.put(featureKey, Optional.of(config));

        return config;
    }

    public void set(NamespacedKey featureKey, Optional<Config> config) {
        cache.put(featureKey, config);
    }

    public void clear(NamespacedKey featureKey) {
        cache.remove(featureKey);
    }
}
