package de.derfrzocker.ore.control.impl;

import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.Ore;
import de.derfrzocker.ore.control.api.OreSettings;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;

public class BiomeOreSettingsYamlImpl extends BiomeOreSettingsImpl implements ConfigurationSerializable {

    private static final String BIOME_KEY = "biome";

    public BiomeOreSettingsYamlImpl(Biome biome) {
        super(biome);
    }

    public BiomeOreSettingsYamlImpl(Biome biome, Map<Ore, OreSettings> map) {
        super(biome);
        map.entrySet().stream().filter(entry -> !(entry.getValue() instanceof ConfigurationSerializable)).map(entry -> new OreSettingsYamlImpl(entry.getKey(), entry.getValue().getSettings())).forEach(this::setOreSettings);
        map.entrySet().stream().filter(entry -> entry.getValue() instanceof ConfigurationSerializable).map(Map.Entry::getValue).forEach(this::setOreSettings);
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();

        map.put(BIOME_KEY, getBiome().toString());

        getOreSettings().entrySet().stream().
                map(entry -> {
                    if (entry.getValue() instanceof ConfigurationSerializable)
                        return entry.getValue();
                    return new OreSettingsYamlImpl(entry.getKey(), entry.getValue().getSettings());
                }).forEach(value -> map.put(value.getOre().toString(), value));

        return map;
    }


    @SuppressWarnings("Duplicates")
    public static BiomeOreSettingsYamlImpl deserialize(Map<String, Object> map) {
        Map<Ore, OreSettings> oreSettings = new HashMap<>();

        map.entrySet().stream().
                filter(entry -> {
                    try {
                        Ore.valueOf(entry.getKey().toUpperCase());
                        return true;
                    } catch (IllegalArgumentException e) {
                        return false;
                    }
                }).
                forEach(entry -> oreSettings.put(Ore.valueOf(entry.getKey().toUpperCase()), (OreSettings) entry.getValue()));

        //TODO remove in higher versions
        Biome biome = null;
        try {
            biome = Biome.valueOf(((String) map.get(BIOME_KEY)).toUpperCase());
        } catch (IllegalArgumentException ignored) {
        }

        return new BiomeOreSettingsYamlImpl(biome, oreSettings);
    }

}
