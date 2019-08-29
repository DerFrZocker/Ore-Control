package de.derfrzocker.ore.control.impl;

import de.derfrzocker.ore.control.api.*;
import lombok.*;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
@EqualsAndHashCode
@ToString
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
    public void setOreSettings(final @NonNull OreSettings oreSettings) {
        this.oreSettings.put(oreSettings.getOre(), oreSettings);
    }

    @Override
    public Optional<BiomeOreSettings> getBiomeOreSettings(final @NonNull Biome biome) {
        return Optional.ofNullable(this.biomeOreSettings.get(biome));
    }

    @Override
    public void setBiomeOreSettings(final @NonNull BiomeOreSettings biomeOreSettings) {
        this.biomeOreSettings.put(biomeOreSettings.getBiome(), biomeOreSettings);
    }

    @Override
    public WorldOreConfig clone(@NonNull String name) {
        return new WorldOreConfigYamlImpl(name, template, oreSettings, biomeOreSettings);
    }

    @Override
    public Map<String, Object> serialize() {
        final Map<String, Object> map = new LinkedHashMap<>();

        map.put(NAME_KEY, getName());

        if (template)
            map.put(TEMPLATE_KEY, true);

        getOreSettings().entrySet().stream().filter(entry -> !entry.getValue().getSettings().isEmpty() || !entry.getValue().isActivated()).
                map(entry -> {
                    if (entry.getValue() instanceof ConfigurationSerializable)
                        return entry.getValue();
                    final OreSettingsYamlImpl oreSettingsYaml = new OreSettingsYamlImpl(entry.getKey(), entry.getValue().getSettings());
                    oreSettingsYaml.setActivated(entry.getValue().isActivated());
                    return oreSettingsYaml;
                }).forEach(value -> map.put(value.getOre().toString(), value));

        getBiomeOreSettings().entrySet().stream().
                filter(entry -> entry.getValue().getOreSettings().entrySet().stream().
                        anyMatch(entry2 -> !entry2.getValue().getSettings().isEmpty() || !entry2.getValue().isActivated())).
                map(entry -> {
                    if (entry.getValue() instanceof ConfigurationSerializable)
                        return entry.getValue();
                    return new BiomeOreSettingsYamlImpl(entry.getKey(), entry.getValue().getOreSettings());
                }).forEach(value -> map.put(value.getBiome().toString(), value));

        return map;
    }

    public static WorldOreConfigYamlImpl deserialize(final @NonNull Map<String, Object> map) {
        final Map<Ore, OreSettings> oreSettings = new HashMap<>();
        final Map<Biome, BiomeOreSettings> biomeOreSettings = new HashMap<>();
        final OreControlService service = Bukkit.getServicesManager().load(OreControlService.class);

        map.entrySet().stream().filter(entry -> service.isOre(entry.getKey())).
                forEach(entry -> oreSettings.put(Ore.valueOf(entry.getKey().toUpperCase()), (OreSettings) entry.getValue()));

        map.entrySet().stream().filter(entry -> service.isBiome(entry.getKey())).
                forEach(entry -> biomeOreSettings.put(Biome.valueOf(entry.getKey().toUpperCase()), (BiomeOreSettings) entry.getValue()));

        final String name;

        if (map.containsKey(WORLD_KEY))
            name = (String) map.get(WORLD_KEY);
        else
            name = (String) map.get(NAME_KEY);

        return new WorldOreConfigYamlImpl(name, (boolean) map.getOrDefault(TEMPLATE_KEY, false), oreSettings, biomeOreSettings);
    }

}
