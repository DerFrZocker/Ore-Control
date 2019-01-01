package de.derfrzocker.ore.control.impl;

import de.derfrzocker.ore.control.api.Ore;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.Map;

@Deprecated // TODO remove in higher version
public class EmeraldSettingsYamlImpl implements ConfigurationSerializable {

    @Override
    public Map<String, Object> serialize() {
        throw new UnsupportedOperationException();
    }

    public static OreSettingsYamlImpl deserialize(Map<String, Object> map) {
        map.put("ore", Ore.EMERALD.toString());
        return OreSettingsYamlImpl.deserialize(map);
    }

}
