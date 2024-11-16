package de.derfrzocker.feature.api;

import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class Registry<V extends Keyed> {

    private final Map<NamespacedKey, V> values = new LinkedHashMap<>();

    public Optional<V> get(NamespacedKey key) {
        return Optional.ofNullable(values.get(key));
    }

    public void register(V value) {
        values.put(value.getKey(), value);
    }

    public Map<NamespacedKey, V> getValues() {
        return values;
    }
}
