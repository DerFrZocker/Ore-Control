package de.derfrzocker.ore.control.impl.v1_14_R1;

import com.google.common.collect.Sets;
import de.derfrzocker.ore.control.api.*;
import net.minecraft.server.v1_14_R1.*;
import org.bukkit.Bukkit;

import java.util.*;

@SuppressWarnings("Duplicates")
public class NMSUtil_v1_14_R1 {

    static WorldGenFeatureChanceDecoratorCountConfiguration getCountConfiguration(final WorldOreConfig config, final Ore ore, WorldGenFeatureChanceDecoratorCountConfiguration countConfiguration, final Biome biome) {
        if (ore == null)
            return countConfiguration;

        final OreControlService service = Bukkit.getServicesManager().load(OreControlService.class);

        return new WorldGenFeatureChanceDecoratorCountConfiguration(
                service.getValue(ore, Setting.VEINS_PER_CHUNK, config, biome),
                service.getValue(ore, Setting.MINIMUM_HEIGHT, config, biome),
                service.getValue(ore, Setting.HEIGHT_SUBTRACT_VALUE, config, biome),
                service.getValue(ore, Setting.HEIGHT_RANGE, config, biome));
    }

    static <C extends WorldGenFeatureConfiguration> C getFeatureConfiguration(final WorldOreConfig config, final Ore ore, final C c, final Biome biome) {
        if (ore == null)
            return c;

        final OreControlService service = Bukkit.getServicesManager().load(OreControlService.class);

        return (C) new WorldGenFeatureOreConfiguration(WorldGenFeatureOreConfiguration.Target.NATURAL_STONE, ((WorldGenFeatureOreConfiguration) c).c, service.getValue(ore, Setting.VEIN_SIZE, config, biome));
    }

    static Ore getOre(final Block block) {
        if (block == Blocks.DIAMOND_ORE)
            return Ore.DIAMOND;
        if (block == Blocks.COAL_ORE)
            return Ore.COAL;
        if (block == Blocks.IRON_ORE)
            return Ore.IRON;
        if (block == Blocks.REDSTONE_ORE)
            return Ore.REDSTONE;
        if (block == Blocks.GOLD_ORE)
            return Ore.GOLD;
        if (block == Blocks.DIRT)
            return Ore.DIRT;
        if (block == Blocks.GRAVEL)
            return Ore.GRAVEL;
        if (block == Blocks.GRANITE)
            return Ore.GRANITE;
        if (block == Blocks.DIORITE)
            return Ore.DIORITE;
        if (block == Blocks.ANDESITE)
            return Ore.ANDESITE;

        return null;
    }

    static Random getRandom(long seed, int x, int z) {
        Random random = new Random(seed);

        long long1 = random.nextLong();
        long long2 = random.nextLong();
        long newseed = (long) x * long1 ^ (long) z * long2 ^ seed;
        random.setSeed(newseed);

        return random;
    }

    static Set<ChunkCoordIntPair> getChunkCoordIntPair(WorldChunkManager worldChunkManager, int x, int z) { // TODO better algorithms
        final BiomeBase biomeBase = worldChunkManager.getBiome(x + 8, z + 8);

        Set<ChunkCoordIntPair> chunkCoordIntPairs = new TreeSet<>((o1, o2) -> {
            if (o1.x < o2.x)
                return 1;

            if (o1.x > o2.x)
                return -1;

            if(o1.z < o2.z)
                return 1;

            if(o1.z > o2.z)
                return -1;

            return 0;
        });

        Set<ChunkCoordIntPair> chunkCoordIntPairs2 = Sets.newHashSet(new ChunkCoordIntPair(x, z));

        Iterator<ChunkCoordIntPair> iterator = chunkCoordIntPairs2.iterator();

        while (iterator.hasNext()) {
            ChunkCoordIntPair chunkCoordIntPair = iterator.next();
            chunkCoordIntPairs.add(chunkCoordIntPair);
            chunkCoordIntPairs2.remove(chunkCoordIntPair);
            Set<ChunkCoordIntPair> set = get(worldChunkManager, chunkCoordIntPair.x, chunkCoordIntPair.z, biomeBase);
            set.removeAll(chunkCoordIntPairs);

            chunkCoordIntPairs2.addAll(set);
            iterator = chunkCoordIntPairs2.iterator();
        }

        return chunkCoordIntPairs;
    }

    private static Set<ChunkCoordIntPair> get(WorldChunkManager worldChunkManager, int centerX, int centerZ, BiomeBase biome) {
        Set<ChunkCoordIntPair> chunkCoordIntPairs = new HashSet<>();

        for (int x = -1; x < 2; x++) {
            for (int z = -1; z < 2; z++) {
                int newX = centerX + (x * 16);
                int newZ = centerZ + (z * 16);

                BiomeBase biomeBase = worldChunkManager.getBiome(newX + 8, newZ + 8);

                if (biome.equals(biomeBase)) {
                    chunkCoordIntPairs.add(new ChunkCoordIntPair(newX, newZ));
                }
            }
        }

        return chunkCoordIntPairs;
    }

}
