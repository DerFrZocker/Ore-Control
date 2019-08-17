package de.derfrzocker.ore.control.api;

import de.derfrzocker.spigot.utils.ChunkCoordIntPair;
import org.bukkit.World;

public interface NMSUtil {

    void replaceNMS();

    Biome getBiome(World world, ChunkCoordIntPair chunkCoordIntPair);

    Object createFeatureConfiguration(Object defaultFeatureConfiguration, int veinsSize);

    Object createCountConfiguration(int veinsPerChunk, int minimumHeight, int heightSubtractValue, int heightRange);

    Object createHeightAverageConfiguration(int veinsPerChunk, int heightCenter, int heightRange);

    Ore getOre(Object object);

}