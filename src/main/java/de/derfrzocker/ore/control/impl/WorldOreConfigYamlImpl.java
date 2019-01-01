package de.derfrzocker.ore.control.impl;

import de.derfrzocker.ore.control.api.Ore;
import de.derfrzocker.ore.control.api.OreSettings;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class WorldOreConfigYamlImpl extends WorldOreConfigImpl implements ConfigurationSerializable {

    private static final String WORLD_KEY = "world";

    public WorldOreConfigYamlImpl(String world) {
        super(world);
    }

    public WorldOreConfigYamlImpl(String world, Map<Ore, OreSettings> map) {
        super(world);
        map.entrySet().stream().map(entry -> new OreSettingsYamlImpl(entry.getKey(), entry.getValue().getSettings())).forEach(this::setOreSettings);
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();

        map.put(WORLD_KEY, getWorld());

        Stream.of(Ore.values()).forEach(value -> map.put(value.toString(), getOreSettings(value)));

        return map;
    }

    public static WorldOreConfigYamlImpl deserialize(Map<String, Object> map) {
        Map<Ore, OreSettings> oreSettings = new HashMap<>();

        map.entrySet().stream().filter(entry -> !entry.getKey().equalsIgnoreCase(WORLD_KEY) && !entry.getKey().endsWith("==") && !entry.getKey().equalsIgnoreCase("gold_normal")).forEach(entry -> oreSettings.put(Ore.valueOf(entry.getKey().toUpperCase()), (OreSettings) entry.getValue()));

        // TODO remove in higher version
        if (map.containsKey("gold_normal"))
            oreSettings.put(Ore.GOLD, (OreSettings) map.get("gold_normal"));

        return new WorldOreConfigYamlImpl((String) map.get(WORLD_KEY), oreSettings);
    }

}
