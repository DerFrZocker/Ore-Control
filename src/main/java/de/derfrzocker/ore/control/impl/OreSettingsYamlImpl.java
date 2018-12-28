package de.derfrzocker.ore.control.impl;

import de.derfrzocker.ore.control.api.Ore;
import de.derfrzocker.ore.control.api.OreSettings;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;

public class OreSettingsYamlImpl extends OreSettingsImpl implements ConfigurationSerializable {

    public OreSettingsYamlImpl(Ore ore, int veinSize, int veinsPerChunk, int minimumHeight, int heightRange, int heightSubtractValue) {
        super(ore, veinSize, veinsPerChunk, minimumHeight, heightRange, heightSubtractValue);
    }

    public OreSettingsYamlImpl(OreSettings settings) {
        super(settings.getOre(), settings.getVeinSize(), settings.getVeinsPerChunk(), settings.getMinimumHeight(), settings.getHeightRange(), settings.getHeightSubtractValue());
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();

        map.put("ore", getOre().name());
        map.put("vein_size", getVeinSize());
        map.put("veins_per_chunk", getVeinsPerChunk());
        map.put("minimum_height", getMinimumHeight());
        map.put("height_range", getHeightRange());
        map.put("height_subtract_value", getHeightSubtractValue());

        return map;
    }

    public static OreSettingsYamlImpl deserialize(Map<String, Object> map) {
        return new OreSettingsYamlImpl(builder().
                ore(Ore.valueOf((String) map.get("ore"))).
                veinSize((Integer) map.getOrDefault("vein_size", 0)).
                veinsPerChunk((Integer) map.getOrDefault("veins_per_chunk", 0)).
                minimumHeight((Integer) map.getOrDefault("minimum_height", 0)).
                heightRange((Integer) map.getOrDefault("height_range", 0)).
                heightSubtractValue((Integer) map.getOrDefault("height_subtract_value", 0)).
                build());
    }

}
