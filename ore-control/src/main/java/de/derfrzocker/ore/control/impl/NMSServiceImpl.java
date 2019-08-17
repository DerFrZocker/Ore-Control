package de.derfrzocker.ore.control.impl;

import com.google.common.collect.Sets;
import de.derfrzocker.ore.control.api.*;
import de.derfrzocker.ore.control.utils.OreControlUtil;
import de.derfrzocker.spigot.utils.ChunkCoordIntPair;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.*;
import java.util.function.BiFunction;

@RequiredArgsConstructor
public class NMSServiceImpl implements NMSService {

    @NonNull
    @Getter
    private final NMSUtil nMSUtil;

    private OreControlService service;

    private OreControlService getOreControlService() {
        final OreControlService tempService = Bukkit.getServicesManager().load(OreControlService.class);

        if (service == null && tempService == null)
            throw new NullPointerException("The Bukkit Service has no OreControlService and no OreControlService is cached!");

        if (tempService != null && service != tempService)
            service = tempService;

        return service;
    }

    @Override
    public void replaceNMS() {
        nMSUtil.replaceNMS();
    }

    @Override
    public boolean generate(final @NonNull World world, final @NonNull Biome biome, final Ore ore, final @NonNull ChunkCoordIntPair chunkCoordIntPair,
                            final @NonNull Object defaultConfiguration, final @NonNull Object defaultFeatureConfiguration,
                            final BiFunction<Location, Integer, Boolean> generateFunction, final @NonNull BiFunction<Object, Object, Boolean> passFunction, final @NonNull Random random) {

        final OreControlService service = getOreControlService();

        if (ore == null)
            return passFunction.apply(defaultConfiguration, defaultFeatureConfiguration);


        final WorldOreConfig worldOreConfig = service.getWorldOreConfig(world).orElse(null);

        if (worldOreConfig == null)
            return passFunction.apply(defaultConfiguration, defaultFeatureConfiguration);

        try {
            if (!OreControlUtil.isActivated(ore, worldOreConfig, biome))
                return true;

            final int veinsPerBiome = OreControlUtil.getAmount(ore, Setting.VEINS_PER_BIOME, worldOreConfig, biome);
            final int veinsPerChunk;

            if (veinsPerBiome > 0)
                veinsPerChunk = calculateVeinsPerChunk(world, biome, chunkCoordIntPair, veinsPerBiome);
            else {
                if (ore == Ore.EMERALD) {
                    final int minimumOresPerChunk = service.getValue(Ore.EMERALD, Setting.MINIMUM_ORES_PER_CHUNK, worldOreConfig, biome);
                    final int oresPerChunkRange = service.getValue(Ore.EMERALD, Setting.ORES_PER_CHUNK_RANGE, worldOreConfig, biome);
                    veinsPerChunk = minimumOresPerChunk + random.nextInt(oresPerChunkRange);
                } else
                    veinsPerChunk = OreControlUtil.getAmount(ore, Setting.VEINS_PER_CHUNK, worldOreConfig, biome);
            }

            if (veinsPerChunk == 0)
                return true;

            if (ore == Ore.EMERALD)
                return handleEmeraldGeneration(worldOreConfig, biome, chunkCoordIntPair, random, veinsPerChunk, generateFunction, service);

            final Object configuration;

            if (ore == Ore.LAPIS)
                configuration = nMSUtil.createHeightAverageConfiguration(veinsPerChunk, OreControlUtil.getAmount(ore, Setting.HEIGHT_CENTER, worldOreConfig, biome), OreControlUtil.getAmount(ore, Setting.HEIGHT_RANGE, worldOreConfig, biome));
            else
                configuration = nMSUtil.createCountConfiguration(veinsPerChunk, OreControlUtil.getAmount(ore, Setting.MINIMUM_HEIGHT, worldOreConfig, biome), OreControlUtil.getAmount(ore, Setting.HEIGHT_SUBTRACT_VALUE, worldOreConfig, biome), OreControlUtil.getAmount(ore, Setting.HEIGHT_RANGE, worldOreConfig, biome));

            final Object featureConfiguration = nMSUtil.createFeatureConfiguration(defaultFeatureConfiguration, OreControlUtil.getAmount(ore, Setting.VEIN_SIZE, worldOreConfig, biome));

            return passFunction.apply(configuration, featureConfiguration);

        } catch (final Exception e) {
            e.printStackTrace();
            final StringBuilder errorMessage = new StringBuilder("Error while generate Chunk" +
                    " Name: " + worldOreConfig.getName() +
                    " Ore: " + ore +
                    " Biome: " + biome);

            for (final Setting setting : ore.getSettings())
                errorMessage.append(" ").append(setting).append(": ").append(OreControlUtil.getAmount(ore, setting, worldOreConfig, biome));

            throw new RuntimeException(errorMessage.toString(), e);
        }
    }

    private int calculateVeinsPerChunk(final World world, final Biome biome, final ChunkCoordIntPair chunkCoordIntPair, final int veinsPerBiome) {
        final Set<ChunkCoordIntPair> chunkCoordIntPairs = getChunkCoordIntPairs(world, biome, chunkCoordIntPair);
        final ChunkCoordIntPair[] coordIntPairs = chunkCoordIntPairs.toArray(new ChunkCoordIntPair[0]);

        final Random random = getRandom(world.getSeed(), coordIntPairs[0]);

        int veinsAmount = 0;

        if (coordIntPairs.length == 1) {
            return veinsPerBiome;
        } else {
            for (int i = 0; i < veinsPerBiome; i++) {
                int randomInt = random.nextInt((coordIntPairs.length - 1));
                ChunkCoordIntPair coordIntPair = coordIntPairs[randomInt];
                if (coordIntPair.equals(chunkCoordIntPair))
                    veinsAmount++;
            }
        }

        return veinsAmount;
    }

    private Set<ChunkCoordIntPair> getChunkCoordIntPairs(final World world, final Biome biome, final ChunkCoordIntPair chunkCoordIntPair) {
        final Set<ChunkCoordIntPair> chunkCoordIntPairs = new TreeSet<>();

        final Set<ChunkCoordIntPair> chunkCoordIntPairsToCheck = Sets.newHashSet(chunkCoordIntPair);

        Iterator<ChunkCoordIntPair> chunkCoordIntPairsToCheckIterator = chunkCoordIntPairsToCheck.iterator();
        while (chunkCoordIntPairsToCheckIterator.hasNext()) {
            final ChunkCoordIntPair chunkCoordIntPairToCheck = chunkCoordIntPairsToCheckIterator.next();

            chunkCoordIntPairs.add(chunkCoordIntPairToCheck);
            chunkCoordIntPairsToCheck.remove(chunkCoordIntPairToCheck);

            final Set<ChunkCoordIntPair> set = getSurroundedChunkCoordIntPairs(world, biome, chunkCoordIntPairToCheck);

            set.removeAll(chunkCoordIntPairs);
            chunkCoordIntPairsToCheck.addAll(set);

            chunkCoordIntPairsToCheckIterator = chunkCoordIntPairsToCheck.iterator();
        }

        return chunkCoordIntPairs;

    }

    private Set<ChunkCoordIntPair> getSurroundedChunkCoordIntPairs(final World world, final Biome biome, final ChunkCoordIntPair chunkCoordIntPair) {
        final Set<ChunkCoordIntPair> chunkCoordIntPairs = new HashSet<>();

        for (int x = -1; x < 2; x++) {
            for (int z = -1; z < 2; z++) {
                final int xToCheck = chunkCoordIntPair.getX() + x;
                final int zToCheck = chunkCoordIntPair.getZ() + z;


                final ChunkCoordIntPair chunkCoordIntPairToCheck = new ChunkCoordIntPair(xToCheck, zToCheck);

                final Biome biomeToCheck = nMSUtil.getBiome(world, chunkCoordIntPairToCheck);

                if (biomeToCheck == null)
                    throw new NullPointerException("BiomeBase for chunk position x: " + xToCheck + " and z: " + zToCheck + " is null, this should never happen!");

                if (biome == biomeToCheck) {
                    chunkCoordIntPairs.add(chunkCoordIntPairToCheck);
                }
            }
        }
        return chunkCoordIntPairs;
    }

    private Random getRandom(final long seed, final ChunkCoordIntPair chunkCoordIntPair) {
        final Random random = new Random(seed);

        final long long1 = random.nextLong();
        final long long2 = random.nextLong();
        final long newSeed = (long) chunkCoordIntPair.getX() * long1 ^ (long) chunkCoordIntPair.getZ() * long2 ^ seed;
        random.setSeed(newSeed);

        return random;
    }

    private boolean handleEmeraldGeneration(final WorldOreConfig worldOreConfig, final Biome biome, final ChunkCoordIntPair chunkCoordIntPair, final Random random, final int veinsPerChunk, final @NonNull BiFunction<Location, Integer, Boolean> generateFunction, final OreControlService service) {

        final int heightRange = service.getValue(Ore.EMERALD, Setting.HEIGHT_RANGE, worldOreConfig, biome);
        final int minimumHeight = service.getValue(Ore.EMERALD, Setting.MINIMUM_HEIGHT, worldOreConfig, biome);

        final Location location = new Location(null, chunkCoordIntPair.getX() << 4, 0, chunkCoordIntPair.getZ() << 4);

        for (int i = 0; i < veinsPerChunk; ++i) {
            final int var7 = random.nextInt(16);
            final int var8 = random.nextInt(heightRange) + minimumHeight;
            final int var9 = random.nextInt(16);
            generateFunction.apply(location.clone().add(var7, var8, var9), -1);
        }

        return true;
    }

}
