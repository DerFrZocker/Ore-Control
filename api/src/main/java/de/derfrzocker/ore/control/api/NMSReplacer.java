package de.derfrzocker.ore.control.api;

import org.bukkit.World;

import java.io.File;
import java.util.Set;

public interface NMSReplacer {

    void register();

    void saveDefaultValues(File file);

    void hookIntoBiomes();

    Set<Biome> getBiomes(World world);

}
