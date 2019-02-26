package de.derfrzocker.ore.control.api;

import org.bukkit.World;

import java.util.Optional;
import java.util.Set;

public interface OreControlService {

    NMSReplacer getNMSReplacer();

    Optional<WorldOreConfig> getWorldOreConfig(World world);

    Optional<WorldOreConfig> getWorldOreConfig(String name);

    WorldOreConfig createWorldOreConfig(World world);

    WorldOreConfig createWorldOreConfigTemplate(String name);

    void saveWorldOreConfig(WorldOreConfig config);

    void removeWorldOreConfig(WorldOreConfig config);

    Set<WorldOreConfig> getAllWorldOreConfigs();

}
