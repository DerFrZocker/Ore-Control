package de.derfrzocker.ore.control.impl;

import de.derfrzocker.ore.control.api.LapisSettings;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;

public class LapisSettingsYamlImpl extends LapisSettingsImpl implements ConfigurationSerializable {

    public LapisSettingsYamlImpl(int veinSize, int veinsPerChunk, int heightRange, int heightCenter) {
        super(veinSize, veinsPerChunk, heightRange, heightCenter);
    }

    public LapisSettingsYamlImpl(LapisSettings settings) {
        super(settings.getVeinSize(), settings.getVeinsPerChunk(), settings.getHeightRange(), settings.getHeightCenter());
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();

        map.put("vein_size", getVeinSize());
        map.put("veins_per_chunk", getVeinsPerChunk());
        map.put("height_range", getHeightRange());
        map.put("height_center", getHeightCenter());

        return map;
    }

    public static LapisSettingsYamlImpl deserialize(Map<String, Object> map) {
        return new LapisSettingsYamlImpl(builder().
                veinSize((Integer) map.getOrDefault("vein_size", 0)).
                veinsPerChunk((Integer) map.getOrDefault("veins_per_chunk", 0)).
                heightRange((Integer) map.getOrDefault("height_range", 0)).
                heightCenter((Integer) map.getOrDefault("height_center", 0)).
                build());
    }

}
