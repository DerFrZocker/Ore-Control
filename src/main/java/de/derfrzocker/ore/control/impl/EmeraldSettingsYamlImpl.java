package de.derfrzocker.ore.control.impl;

import de.derfrzocker.ore.control.api.EmeraldSettings;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;

public class EmeraldSettingsYamlImpl extends EmeraldSettingsImpl implements ConfigurationSerializable {

    public EmeraldSettingsYamlImpl(int minimumOresPerChunk, int oresPerChunkRange, int heightRange, int minimumHeight) {
        super(minimumOresPerChunk, oresPerChunkRange, heightRange, minimumHeight);
    }

    public EmeraldSettingsYamlImpl(EmeraldSettings settings) {
        super(settings.getMinimumOresPerChunk(), settings.getOresPerChunkRange(), settings.getHeightRange(), settings.getMinimumHeight());
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();

        map.put("minimum_ores_per_chunk", getMinimumOresPerChunk());
        map.put("ores_per_chunk_range", getOresPerChunkRange());
        map.put("height_range", getHeightRange());
        map.put("minimum_height", getMinimumHeight());

        return map;
    }

    public static EmeraldSettingsYamlImpl deserialize(Map<String, Object> map) {
        return new EmeraldSettingsYamlImpl(builder().
                minimumOresPerChunk((Integer) map.getOrDefault("minimum_ores_per_chunk", 0)).
                oresPerChunkRange((Integer) map.getOrDefault("ores_per_chunk_range", 0)).
                heightRange((Integer) map.getOrDefault("height_range", 0)).
                minimumHeight((Integer) map.getOrDefault("minimum_height", 0)).
                build());
    }

}
