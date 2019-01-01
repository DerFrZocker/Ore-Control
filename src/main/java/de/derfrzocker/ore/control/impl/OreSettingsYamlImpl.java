package de.derfrzocker.ore.control.impl;

import de.derfrzocker.ore.control.api.Ore;
import de.derfrzocker.ore.control.api.Setting;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;

public class OreSettingsYamlImpl extends OreSettingsImpl implements ConfigurationSerializable {

    private static final String ORE_KEY = "ore";

    public OreSettingsYamlImpl(Ore ore) {
        super(ore);
    }

    public OreSettingsYamlImpl(Ore ore, Map<Setting, Integer> map) {
        super(ore, map);
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();

        map.put(ORE_KEY, getOre().toString());

        getSettings().forEach((key, value) -> map.put(key.toString(), value));

        return map;
    }

    public static OreSettingsYamlImpl deserialize(Map<String, Object> map) {
        Map<Setting, Integer> settings = new HashMap<>();

        map.entrySet().stream().filter(entry -> !entry.getKey().equalsIgnoreCase(ORE_KEY) && !entry.getKey().endsWith("==")).forEach(entry -> settings.put(Setting.valueOf(entry.getKey().toUpperCase()), (Integer) entry.getValue()));

        return new OreSettingsYamlImpl(Ore.valueOf(((String) map.get(ORE_KEY)).toUpperCase()), settings);
    }

    @Override
    public OreSettingsYamlImpl clone() {
        return new OreSettingsYamlImpl(getOre(), getSettings());
    }
}
