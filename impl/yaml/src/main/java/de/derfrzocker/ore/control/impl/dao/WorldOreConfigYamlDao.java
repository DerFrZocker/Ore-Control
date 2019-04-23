package de.derfrzocker.ore.control.impl.dao;

import com.google.common.collect.Sets;
import de.derfrzocker.ore.control.api.WorldOreConfig;
import de.derfrzocker.ore.control.api.dao.WorldOreConfigDao;
import de.derfrzocker.ore.control.impl.WorldOreConfigYamlImpl;
import de.derfrzocker.spigot.utils.Config;
import lombok.NonNull;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class WorldOreConfigYamlDao implements WorldOreConfigDao {

    @NonNull
    private final File file;

    @NonNull
    private final YamlConfiguration yaml; //TODO Check Thread safety

    public WorldOreConfigYamlDao(final File file) {
        this.file = file;
        yaml = new Config(file);
        try {
            yaml.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Unexpected error while save YamlConfiguration to file: " + file, e);
        }
    }

    @Override
    public Optional<WorldOreConfig> get(final @NonNull String name) {
        return Optional.ofNullable(yaml.getSerializable(name, WorldOreConfigYamlImpl.class, null));
    }

    @Override
    public void remove(final @NonNull WorldOreConfig config) {
        yaml.set(config.getName(), null);

        try {
            yaml.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Unexpected error while save YamlConfiguration to file: " + file + ", while remove WorldOreConfig: " + config, e);
        }
    }

    @Override
    public void save(@NonNull WorldOreConfig config) {
        if (!(config instanceof ConfigurationSerializable))
            config = new WorldOreConfigYamlImpl(config.getName(), config.isTemplate(), config.getOreSettings(), config.getBiomeOreSettings());

        yaml.set(config.getName(), config);

        try {
            yaml.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Unexpected error while save YamlConfiguration to file: " + file + ", while save WorldOreConfig: " + config, e);
        }
    }

    @Override
    public Set<WorldOreConfig> getAll() {
        return Sets.newHashSet(yaml.getKeys(false).stream().map(yaml::get).filter(Objects::nonNull).filter(value -> value instanceof WorldOreConfig).map(value -> (WorldOreConfig) value).toArray(WorldOreConfig[]::new));
    }

}
