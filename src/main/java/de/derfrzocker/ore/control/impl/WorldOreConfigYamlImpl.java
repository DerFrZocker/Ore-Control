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

    @Deprecated
    private static final String WORLD_KEY = "world";
    private static final String NAME_KEY = "name";
    private static final String TEMPLATE_KEY = "template";

    @Getter
    private final Map<Ore, OreSettings> oreSettings = new HashMap<>();

    @Getter
    private final Map<Biome, BiomeOreSettings> biomeOreSettings = new HashMap<>();

    @Getter
    @NonNull
    private final String name;

    @Getter
    @Setter
    private boolean template;

    public WorldOreConfigYamlImpl(final @NonNull String name, final boolean template, final @NonNull Map<Ore, OreSettings> oreSettings) {
        this.name = name;
        this.template = template;

        oreSettings.forEach((key, value) -> this.oreSettings.put(key, value.clone()));
    }

    public WorldOreConfigYamlImpl(final @NonNull String name, final boolean template, final @NonNull Map<Ore, OreSettings> oreSettings, final @NonNull Map<Biome, BiomeOreSettings> biomeOreSettings) {
        this(name, template, oreSettings);

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
    public WorldOreConfig clone(String name) {
        return new WorldOreConfigYamlImpl(name, template, oreSettings, biomeOreSettings);
    }

    @Override
    public Map<String, Object> serialize() {
        final Map<String, Object> map = new LinkedHashMap<>();

        map.put(NAME_KEY, getName());

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

        final String name;

        if (map.containsKey(WORLD_KEY))
            name = (String) map.get(WORLD_KEY);
        else
            name = (String) map.get(NAME_KEY);

        return new WorldOreConfigYamlImpl(name, (boolean) map.getOrDefault(TEMPLATE_KEY, false), oreSettings, biomeOreSettings);
    }

}
