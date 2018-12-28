package de.derfrzocker.ore.control.api;

import org.bukkit.World;

import java.util.Optional;

public interface OreControlService {

    NMSReplacer getNMSReplacer();

    Optional<WorldOreConfig> getWorldOreConfig(World world);

    WorldOreConfig createWorldOreConfig(World world);

    void saveWorldOreConfig(WorldOreConfig config);

}
