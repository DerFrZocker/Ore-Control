package de.derfrzocker.ore.control.impl.dao;

import com.google.common.collect.Sets;
import de.derfrzocker.ore.control.api.WorldOreConfig;
import de.derfrzocker.ore.control.api.dao.WorldOreConfigDao;
import de.derfrzocker.ore.control.impl.WorldOreConfigYamlImpl;
import de.derfrzocker.ore.control.utils.Config;
import de.derfrzocker.ore.control.utils.YamlReloadAble;
import lombok.NonNull;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class WorldOreConfigYamlDao implements WorldOreConfigDao, YamlReloadAble {

    @NonNull
    private File file;

    @NonNull
    private YamlConfiguration yaml; //TODO Check Thread safety

    public WorldOreConfigYamlDao(File file) {
        this.file = file;
        yaml = new Config(file);
        try {
            yaml.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<WorldOreConfig> get(@NonNull World world) {
        return Optional.ofNullable(yaml.getSerializable(world.getName(), WorldOreConfigYamlImpl.class, null));
    }

    @Override
    public void remove(@NonNull WorldOreConfig config) {
        yaml.set(config.getWorld(), null);

        try {
            yaml.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save(@NonNull WorldOreConfig config) {
        if (!(config instanceof ConfigurationSerializable))
            config = new WorldOreConfigYamlImpl(config.getWorld(), config.getOreSettings());

        yaml.set(config.getWorld(), config);

        try {
            yaml.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Set<WorldOreConfig> getAll() {
        return Sets.newHashSet(yaml.getKeys(false).stream().map(yaml::get).filter(Objects::nonNull).filter(value -> value instanceof WorldOreConfig).map(value -> (WorldOreConfig) value).toArray(WorldOreConfig[]::new));
    }

    @Override
    public void reload(File file) {
        this.file = file;
        reload();
    }

    @Override
    public void reload() {
        yaml = new Config(file);
    }
}
