package de.derfrzocker.ore.control.api;

import de.derfrzocker.feature.api.Registries;
import de.derfrzocker.feature.api.Registry;

public class OreControlRegistries extends Registries {

    private final Registry<Biome> biomeRegistry = new Registry<>();

    public Registry<Biome> getBiomeRegistry() {
        return biomeRegistry;
    }
}
