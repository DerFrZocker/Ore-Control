package de.derfrzocker.ore.control.api;

import java.util.Map;
import java.util.Optional;

public interface WorldOreConfig extends Cloneable {

    String getWorld();

    Optional<OreSettings> getOreSettings(Ore ore);

    void setOreSettings(OreSettings oreSettings);

    Map<Ore, OreSettings> getOreSettings();

    Optional<BiomeOreSettings> getBiomeOreSettings(Biome biome);

    void setBiomeOreSettings(BiomeOreSettings biomeOreSettings);

    Map<Biome, BiomeOreSettings> getBiomeOreSettings();

    boolean isTemplate();

    void setTemplate(boolean status);

    WorldOreConfig clone();

}
