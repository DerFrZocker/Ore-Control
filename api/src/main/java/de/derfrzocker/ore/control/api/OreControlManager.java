package de.derfrzocker.ore.control.api;

import de.derfrzocker.ore.control.api.config.ConfigManager;
import org.bukkit.World;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class OreControlManager {

    private final OreControlRegistries registries;
    private final ConfigManager configManager;
    private final Function<World, Set<Biome>> biomeFunction;
    private final List<Runnable> valueChangeListener = new LinkedList<>();

    public OreControlManager(OreControlRegistries registries, ConfigManager configManager, Function<World, Set<Biome>> biomeFunction) {
        this.registries = registries;
        this.configManager = configManager;
        this.biomeFunction = biomeFunction;
    }

    public OreControlRegistries getRegistries() {
        return registries;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public Set<Biome> getBiomes(World world) {
        return biomeFunction.apply(world);
    }

    public void addValueChangeListener(Runnable listener) {
        valueChangeListener.add(listener);
    }

    public void onValueChange() {
        valueChangeListener.forEach(Runnable::run);
    }
}
