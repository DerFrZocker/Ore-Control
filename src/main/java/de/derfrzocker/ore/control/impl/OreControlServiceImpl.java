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

@RequiredArgsConstructor
public class OreControlServiceImpl implements OreControlService {

    @Getter
    @NonNull
    private final NMSReplacer NMSReplacer;

    @NonNull
    private final WorldOreConfigDao dao;

    @Override
    public Optional<WorldOreConfig> getWorldOreConfig(World world) {
        return dao.get(world);
    }

    @Override
    public WorldOreConfig createWorldOreConfig(@NonNull World world) {
        WorldOreConfig worldOreConfig = new WorldOreConfigYamlImpl(world.getName());

        saveWorldOreConfig(worldOreConfig);

        return worldOreConfig;
    }

    @Override
    public void saveWorldOreConfig(WorldOreConfig config) {
        dao.save(config);
    }
}
