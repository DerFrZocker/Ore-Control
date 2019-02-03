package de.derfrzocker.ore.control.impl;

import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.BiomeOreSettings;
import de.derfrzocker.ore.control.api.Ore;
import de.derfrzocker.ore.control.api.OreSettings;
import de.derfrzocker.ore.control.utils.OreControlUtil;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;

public class WorldOreConfigYamlImpl extends WorldOreConfigImpl implements ConfigurationSerializable {

    private static final String WORLD_KEY = "world";

    public WorldOreConfigYamlImpl(String world) {
        super(world);
    }

    public WorldOreConfigYamlImpl(String world, Map<Ore, OreSettings> map) {
        super(world);
        map.entrySet().stream().filter(entry -> !(entry.getValue() instanceof ConfigurationSerializable)).map(entry -> new OreSettingsYamlImpl(entry.getKey(), entry.getValue().getSettings())).forEach(this::setOreSettings);
        map.entrySet().stream().filter(entry -> entry.getValue() instanceof ConfigurationSerializable).map(Map.Entry::getValue).forEach(this::setOreSettings);
    }

    public WorldOreConfigYamlImpl(String world, Map<Ore, OreSettings> map, Map<Biome, BiomeOreSettings> biomeOreSettings) {
        this(world, map);

        biomeOreSettings.entrySet().stream().filter(entry -> !(entry.getValue() instanceof ConfigurationSerializable)).map(entry -> new BiomeOreSettingsYamlImpl(entry.getKey(), entry.getValue().getOreSettings())).forEach(this::setBiomeOreSettings);
        biomeOreSettings.entrySet().stream().filter(entry -> entry.getValue() instanceof ConfigurationSerializable).map(Map.Entry::getValue).forEach(this::setBiomeOreSettings);
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();

        map.put(WORLD_KEY, getWorld());

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

    public static WorldOreConfigYamlImpl deserialize(Map<String, Object> map) {
        Map<Ore, OreSettings> oreSettings = new HashMap<>();

        Map<Biome, BiomeOreSettings> biomeOreSettings = new HashMap<>();

        map.entrySet().stream().filter(entry -> OreControlUtil.isOre(entry.getKey())).
                forEach(entry -> oreSettings.put(Ore.valueOf(entry.getKey().toUpperCase()), (OreSettings) entry.getValue()));

        map.entrySet().stream().filter(entry -> OreControlUtil.isBiome(entry.getKey())).
                forEach(entry -> biomeOreSettings.put(Biome.valueOf(entry.getKey().toUpperCase()), (BiomeOreSettings) entry.getValue()));

        // TODO remove in higher version
        if (map.containsKey("gold_normal"))
            oreSettings.put(Ore.GOLD, (OreSettings) map.get("gold_normal"));

        return new WorldOreConfigYamlImpl((String) map.get(WORLD_KEY), oreSettings, biomeOreSettings);
    }

}
