package de.derfrzocker.ore.control.impl;

import de.derfrzocker.ore.control.api.*;
import de.derfrzocker.ore.control.utils.OreControlUtil;
import lombok.*;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
@EqualsAndHashCode
public class WorldOreConfigYamlImpl implements ConfigurationSerializable, WorldOreConfig {

    private static final String WORLD_KEY = "world";
    private static final String TEMPLATE_KEY = "template";

    @Getter
    private final Map<Ore, OreSettings> oreSettings = new HashMap<>();

    @Getter
    private final Map<Biome, BiomeOreSettings> biomeOreSettings = new HashMap<>();

    @Getter
    @NonNull
    private final String world;

    @Getter
    @Setter
    private boolean template;

    public WorldOreConfigYamlImpl(final String world, final boolean template, final @NonNull Map<Ore, OreSettings> oreSettings) {
        this.world = world;
        this.template = template;

        oreSettings.forEach((key, value) -> this.oreSettings.put(key, value.clone()));
    }

    public WorldOreConfigYamlImpl(final String world, final boolean template, final @NonNull Map<Ore, OreSettings> oreSettings, final @NonNull Map<Biome, BiomeOreSettings> biomeOreSettings) {
        this(world, template, oreSettings);

        biomeOreSettings.forEach((key, value) -> this.biomeOreSettings.put(key, value.clone()));
    }

    @Override
    public Optional<OreSettings> getOreSettings(final @NonNull Ore ore) {
        return Optional.ofNullable(oreSettings.get(ore));
    }

    @Override
    public void setOreSettings(final OreSettings oreSettings) {
        this.oreSettings.put(oreSettings.getOre(), oreSettings);
    }

    @Override
    public Optional<BiomeOreSettings> getBiomeOreSettings(final @NonNull Biome biome) {
        return Optional.ofNullable(this.biomeOreSettings.get(biome));
    }

    @Override
    public void setBiomeOreSettings(final BiomeOreSettings biomeOreSettings) {
        this.biomeOreSettings.put(biomeOreSettings.getBiome(), biomeOreSettings);
    }

    @Override
    public WorldOreConfig clone() {
        return new WorldOreConfigYamlImpl(world, template, oreSettings, biomeOreSettings);
    }

    @Override
    public Map<String, Object> serialize() {
        final Map<String, Object> map = new LinkedHashMap<>();

        map.put(WORLD_KEY, getWorld());

        if (template)
            map.put(TEMPLATE_KEY, true);

        getOreSettings().entrySet().stream().
                map(entry -> {
                    if (entry.getValue() instanceof ConfigurationSerializable)
                        return entry.getValue();
                    return new OreSettingsYamlImpl(entry.getKey(), entry.getValue().getSettings());
                }).forEach(value -> map.put(value.getOre().toString(), value));

        getBiomeOreSettings().entrySet().stream().map(entry -> {
            if (entry.getValue() instanceof ConfigurationSerializable)
                return entry.getValue();
            return new BiomeOreSettingsYamlImpl(entry.getKey(), entry.getValue().getOreSettings());
        }).forEach(value -> map.put(value.getBiome().toString(), value));

        return map;
    }

    public static WorldOreConfigYamlImpl deserialize(final Map<String, Object> map) {
        final Map<Ore, OreSettings> oreSettings = new HashMap<>();

        final Map<Biome, BiomeOreSettings> biomeOreSettings = new HashMap<>();

        map.entrySet().stream().filter(entry -> OreControlUtil.isOre(entry.getKey())).
                forEach(entry -> oreSettings.put(Ore.valueOf(entry.getKey().toUpperCase()), (OreSettings) entry.getValue()));

        map.entrySet().stream().filter(entry -> OreControlUtil.isBiome(entry.getKey())).
                forEach(entry -> biomeOreSettings.put(Biome.valueOf(entry.getKey().toUpperCase()), (BiomeOreSettings) entry.getValue()));

        return new WorldOreConfigYamlImpl((String) map.get(WORLD_KEY), (boolean) map.getOrDefault(TEMPLATE_KEY, false), oreSettings, biomeOreSettings);
    }

}
