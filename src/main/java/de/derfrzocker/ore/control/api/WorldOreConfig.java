package de.derfrzocker.ore.control.api;

import java.util.Map;

public interface WorldOreConfig {

    String getWorld();

    OreSettings getOreSettings(Ore ore);

    void setOreSettings(OreSettings oreSettings);

    Map<Ore, OreSettings> getOreSettings();

}
