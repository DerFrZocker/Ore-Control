package de.derfrzocker.ore.control.api;

import org.bukkit.World;

import java.util.Optional;
import java.util.Set;

public interface OreControlService {

    NMSService getNMSService();

    Optional<WorldOreConfig> getWorldOreConfig(World world);

    Optional<WorldOreConfig> getWorldOreConfig(String name);

    WorldOreConfig createWorldOreConfig(World world);

    WorldOreConfig createWorldOreConfigTemplate(String name);

    void saveWorldOreConfig(WorldOreConfig config);

    void removeWorldOreConfig(WorldOreConfig config);

    Set<WorldOreConfig> getAllWorldOreConfigs();

    int getValue(Ore ore, Setting setting, WorldOreConfig worldOreConfig, Biome biome);

    boolean isActivated(Ore ore, WorldOreConfig worldOreConfig, Biome biome);

    boolean isOre(String string);

    boolean isBiome(String string);

    boolean isSetting(String string);

}
