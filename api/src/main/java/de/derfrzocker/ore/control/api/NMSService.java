package de.derfrzocker.ore.control.api;

import de.derfrzocker.spigot.utils.ChunkCoordIntPair;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Random;
import java.util.function.BiFunction;

public interface NMSService {

    void replaceNMS();

    boolean generate(World world, Biome biome, Ore ore, ChunkCoordIntPair chunkCoordIntPair,
                     Object defaultConfiguration, Object defaultFeatureConfiguration,
                     BiFunction<Location, Integer, Boolean> generateFunction, BiFunction<Object, Object, Boolean> passFunction, Random random);

    NMSUtil getNMSUtil();

}
