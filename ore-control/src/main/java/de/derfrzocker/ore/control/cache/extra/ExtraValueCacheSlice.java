package de.derfrzocker.ore.control.cache.extra;

import de.derfrzocker.feature.api.ExtraValues;
import de.derfrzocker.ore.control.api.config.ConfigInfo;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class ExtraValueCacheSlice {
    private final Map<ConfigInfo, Optional<ExtraValues>> cache = new LinkedHashMap<>();

    public Optional<ExtraValues> getOrCompute(ConfigInfo configInfo, Supplier<Optional<ExtraValues>> supplier) {
        return cache.computeIfAbsent(configInfo, ignore -> supplier.get());
    }

    private Optional<ExtraValues> getOrCreate() {
        return Optional.empty();
    }
}
