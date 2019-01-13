package de.derfrzocker.ore.control.impl;

import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.BiomeOreSettings;
import de.derfrzocker.ore.control.api.Ore;
import de.derfrzocker.ore.control.api.OreSettings;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@EqualsAndHashCode
public class BiomeOreSettingsImpl implements BiomeOreSettings {

    @Getter
    private final Map<Ore, OreSettings> oreSettings = new HashMap<>();

    @Getter
    private final Biome biome;

    public BiomeOreSettingsImpl(Biome biome, Map<Ore, OreSettings> map) {
        this.biome = biome;
        this.oreSettings.putAll(map);
    }

    @Override
    public Optional<OreSettings> getOreSettings(Ore ore) {
        return Optional.ofNullable(oreSettings.get(ore));
    }

    @Override
    public void setOreSettings(OreSettings oreSettings) {
        this.oreSettings.put(oreSettings.getOre(), oreSettings);
    }

}
