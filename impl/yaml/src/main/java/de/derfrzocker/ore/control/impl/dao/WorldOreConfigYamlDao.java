package de.derfrzocker.ore.control.impl.dao;

import de.derfrzocker.ore.control.api.WorldOreConfig;
import de.derfrzocker.ore.control.api.dao.WorldOreConfigDao;
import de.derfrzocker.ore.control.impl.WorldOreConfigYamlImpl;
import de.derfrzocker.spigot.utils.dao.yaml.BasicYamlDao;
import lombok.NonNull;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.io.File;
import java.util.Optional;

public class WorldOreConfigYamlDao extends BasicYamlDao<String, WorldOreConfig> implements WorldOreConfigDao {

    public WorldOreConfigYamlDao(File file) {
        super(file);
    }

    @Override
    public Optional<WorldOreConfig> get(final @NonNull String name) {
        return getFromStringKey(name);
    }

    @Override
    public void remove(final @NonNull WorldOreConfig config) {
        saveFromStringKey(config.getName(), null);
    }

    @Override
    public void save(@NonNull WorldOreConfig config) {
        if (!(config instanceof ConfigurationSerializable))
            config = new WorldOreConfigYamlImpl(config.getName(), config.isTemplate(), config.getOreSettings(), config.getBiomeOreSettings());

        saveFromStringKey(config.getName(), config);
    }

}
