package de.derfrzocker.ore.control.impl;

import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.BiomeOreSettings;
import de.derfrzocker.ore.control.api.Ore;
import de.derfrzocker.ore.control.api.OreSettings;
import de.derfrzocker.ore.control.utils.OreControlUtil;
import lombok.*;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public class BiomeOreSettingsYamlImpl implements ConfigurationSerializable, BiomeOreSettings {

    private static final String BIOME_KEY = "biome";

    @Getter
    @NonNull
    private final Biome biome;

    @Getter
    private final Map<Ore, OreSettings> oreSettings = new HashMap<>();

    public BiomeOreSettingsYamlImpl(final Biome biome, final Map<Ore, OreSettings> oreSettings) {
        this.biome = biome;

        oreSettings.forEach((key, value) -> this.oreSettings.put(key, value.clone()));
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
    public BiomeOreSettings clone() {
        return new BiomeOreSettingsYamlImpl(biome, oreSettings);
    }

    @Override
    public Map<String, Object> serialize() {
        final Map<String, Object> map = new LinkedHashMap<>();

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
    public static BiomeOreSettingsYamlImpl deserialize(final Map<String, Object> map) {
        final Map<Ore, OreSettings> oreSettings = new HashMap<>();

        map.entrySet().stream().filter(entry -> OreControlUtil.isOre(entry.getKey())).
                forEach(entry -> oreSettings.put(Ore.valueOf(entry.getKey().toUpperCase()), (OreSettings) entry.getValue()));

        return new BiomeOreSettingsYamlImpl(Biome.valueOf(((String) map.get(BIOME_KEY)).toUpperCase()), oreSettings);
    }

}
