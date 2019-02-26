package de.derfrzocker.ore.control.impl;

import de.derfrzocker.ore.control.api.NMSReplacer;
import de.derfrzocker.ore.control.api.OreControlService;
import de.derfrzocker.ore.control.api.WorldOreConfig;
import de.derfrzocker.ore.control.api.dao.WorldOreConfigDao;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.World;

import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
public class OreControlServiceImpl implements OreControlService {

    @Getter
    @NonNull
    private final NMSReplacer NMSReplacer;

    @NonNull
    private final WorldOreConfigDao dao;

    @Override
    public Optional<WorldOreConfig> getWorldOreConfig(final @NonNull World world) {
        return dao.get(world.getName());
    }

    @Override
    public Optional<WorldOreConfig> getWorldOreConfig(final @NonNull String name) {
        return dao.get(name);
    }

    @Override
    public WorldOreConfig createWorldOreConfig(final @NonNull World world) {
        final WorldOreConfig worldOreConfig = new WorldOreConfigYamlImpl(world.getName(), false);

        saveWorldOreConfig(worldOreConfig);

        return worldOreConfig;
    }

    @Override
    public WorldOreConfig createWorldOreConfigTemplate(final @NonNull String name) {
        final WorldOreConfig worldOreConfig = new WorldOreConfigYamlImpl(name, true);

        saveWorldOreConfig(worldOreConfig);

        return worldOreConfig;
    }

    @Override
    public void saveWorldOreConfig(final @NonNull WorldOreConfig config) {
        dao.save(config);
    }

    @Override
    public void removeWorldOreConfig(final @NonNull WorldOreConfig config) {
        dao.remove(config);
    }

    @Override
    public Set<WorldOreConfig> getAllWorldOreConfigs() {
        return dao.getAll();
    }
}
