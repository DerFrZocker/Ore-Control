package de.derfrzocker.ore.control;

import de.derfrzocker.ore.control.api.*;
import de.derfrzocker.ore.control.api.dao.WorldOreConfigDao;
import de.derfrzocker.ore.control.impl.WorldOreConfigYamlImpl;
import de.derfrzocker.ore.control.utils.OreControlUtil;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.bukkit.World;

import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@ToString
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

    @Override
    public int getValue(final Ore ore, final Setting setting, final WorldOreConfig worldOreConfig, final Biome biome) {
        return OreControlUtil.getAmount(ore, setting, worldOreConfig, biome);
    }

    @Override
    public boolean isActivated(final Ore ore, final WorldOreConfig worldOreConfig, final Biome biome) {
        return OreControlUtil.isActivated(ore, worldOreConfig, biome);
    }

    @Override
    public boolean isOre(final String string) {
        return OreControlUtil.isOre(string);
    }

    @Override
    public boolean isBiome(final String string) {
        return OreControlUtil.isBiome(string);
    }

    @Override
    public boolean isSetting(final String string) {
        return OreControlUtil.isSetting(string);
    }
}
