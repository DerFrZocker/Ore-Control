package de.derfrzocker.ore.control.cache.extra;

import de.derfrzocker.feature.api.ExtraValues;
import de.derfrzocker.ore.control.api.config.ConfigInfo;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class ExtraValueCacheSlice {
    private final Map<ConfigInfo, Optional<ExtraValues>> cache = new ConcurrentHashMap<>();

    public void forEach(BiConsumer<ConfigInfo, Optional<ExtraValues>> consumer) {
        cache.forEach(consumer);
    }

    public Optional<ExtraValues> getOrCompute(ConfigInfo configInfo, Supplier<Optional<ExtraValues>> supplier) {
        return cache.computeIfAbsent(configInfo, ignore -> supplier.get());
    }

    public ExtraValues getOrCreate(ConfigInfo configInfo) {
        Optional<ExtraValues> value = cache.getOrDefault(configInfo, Optional.empty());

        if (value.isPresent()) {
            return value.get();
        }

        ExtraValues values = new ExtraValues();
        cache.put(configInfo, Optional.of(values));

        return values;
    }

    public void clear(ConfigInfo configInfo) {
        cache.remove(configInfo);
    }

    public void clear() {
        cache.clear();
    }
}
