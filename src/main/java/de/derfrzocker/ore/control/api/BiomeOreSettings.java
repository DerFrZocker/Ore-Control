package de.derfrzocker.ore.control.api;

import java.util.Map;
import java.util.Optional;

public interface BiomeOreSettings {

    Biome getBiome();

    Optional<OreSettings> getOreSettings(Ore ore);

    void setOreSettings(OreSettings oreSettings);

    Map<Ore, OreSettings> getOreSettings();

}
