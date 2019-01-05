package de.derfrzocker.ore.control.impl;

import de.derfrzocker.ore.control.api.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@RequiredArgsConstructor
@EqualsAndHashCode
public class WorldOreConfigImpl implements WorldOreConfig {

    @Getter
    private final Map<Ore, OreSettings> oreSettings = new HashMap<>();

    @Getter
    private final Map<Biome, BiomeOreSettings> biomeOreSettings = new HashMap<>();

    @Getter
    @NonNull
    private final String world;

    public WorldOreConfigImpl(String world, Map<Ore, OreSettings> map) {
        this.world = world;
        this.oreSettings.putAll(map);
    }

    public WorldOreConfigImpl(String world, Map<Ore, OreSettings> map, Map<Biome, BiomeOreSettings> biomeOreSettings) {
        this.world = world;
        this.oreSettings.putAll(map);
        this.biomeOreSettings.putAll(biomeOreSettings);
    }

    @Override
    public Optional<OreSettings> getOreSettings(@NonNull Ore ore) {
        return Optional.ofNullable(oreSettings.get(ore));
    }

    @Override
    public void setOreSettings(OreSettings oreSettings) {
        this.oreSettings.put(oreSettings.getOre(), oreSettings);
    }

    @Override
    public Optional<BiomeOreSettings> getBiomeOreSettings(@NonNull Biome biome) {
        return Optional.ofNullable(this.biomeOreSettings.get(biome));
    }

    @Override
    public void setBiomeOreSettings(BiomeOreSettings biomeOreSettings) {
        this.biomeOreSettings.put(biomeOreSettings.getBiome(), biomeOreSettings);
    }
}
