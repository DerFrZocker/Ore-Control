package de.derfrzocker.ore.control.api.dao;

import de.derfrzocker.ore.control.api.WorldOreConfig;
import org.bukkit.World;

import java.util.Optional;
import java.util.Set;

public interface WorldOreConfigDao {

    Optional<WorldOreConfig> get(World world);

    void remove(WorldOreConfig config);

    void save(WorldOreConfig config);

    Set<WorldOreConfig> getAll();

}
