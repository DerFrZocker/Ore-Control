package de.derfrzocker.ore.control.api;

import de.derfrzocker.feature.api.Feature;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashSet;
import java.util.Set;

public class Biome implements Keyed {

    private final NamespacedKey key;
    private final Set<Feature> features = new LinkedHashSet<>();

    public Biome(NamespacedKey key) {
        this.key = key;
    }

    @NotNull
    @Override
    public NamespacedKey getKey() {
        return key;
    }

    public Set<Feature> getFeatures() {
        return features;
    }
}
