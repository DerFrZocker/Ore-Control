/*
 * MIT License
 *
 * Copyright (c) 2019 Marvin (DerFrZocker)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.derfrzocker.ore.control.impl;

import com.google.common.collect.Sets;
import de.derfrzocker.ore.control.api.*;
import de.derfrzocker.ore.control.utils.OreControlUtil;
import de.derfrzocker.spigot.utils.ChunkCoordIntPair;
import de.derfrzocker.spigot.utils.NumberUtil;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class NMSServiceImpl implements NMSService {

    @NonNull
    @Getter
    private final NMSUtil nMSUtil;

    @NonNull
    private final Supplier<OreControlService> serviceSupplier;

    @Override
    public void replaceNMS() {
        nMSUtil.replaceNMS();
    }

    @Override
    public boolean generate(final @NonNull World world, final @NonNull Biome biome, final Ore ore, final @NonNull ChunkCoordIntPair chunkCoordIntPair,
                            final @NonNull Object defaultConfiguration, final @NonNull Object defaultFeatureConfiguration,
                            final BiFunction<Location, Integer, Boolean> generateFunction, final @NonNull BiFunction<Object, Object, Boolean> passFunction, final @NonNull Random random) {

        final OreControlService service = serviceSupplier.get();

        if (ore == null)
            return passFunction.apply(defaultConfiguration, defaultFeatureConfiguration);


        final WorldOreConfig worldOreConfig = service.getWorldOreConfig(world).orElse(null);

        if (worldOreConfig == null)
            return passFunction.apply(defaultConfiguration, defaultFeatureConfiguration);

        try {
            if (!OreControlUtil.isActivated(ore, worldOreConfig, biome))
                return true;

            final double veinsPerBiome = OreControlUtil.getAmount(ore, Setting.VEINS_PER_BIOME, worldOreConfig, biome);
            final int veinsPerChunk;

            if (veinsPerBiome > 0) {
                veinsPerChunk = calculateVeinsPerChunk(world, biome, chunkCoordIntPair, veinsPerBiome);
            } else {
                if (ore == Ore.EMERALD) {
                    final int minimumOresPerChunk = NumberUtil.getInt(service.getValue(Ore.EMERALD, Setting.MINIMUM_ORES_PER_CHUNK, worldOreConfig, biome), random);
                    final int oresPerChunkRange = NumberUtil.getInt(service.getValue(Ore.EMERALD, Setting.ORES_PER_CHUNK_RANGE, worldOreConfig, biome), random);

                    veinsPerChunk = minimumOresPerChunk + oresPerChunkRange == 0 ? 0 : random.nextInt(oresPerChunkRange);
                } else
                    veinsPerChunk = NumberUtil.getInt(OreControlUtil.getAmount(ore, Setting.VEINS_PER_CHUNK, worldOreConfig, biome), random);
            }

            if (veinsPerChunk == 0)
                return true;

            if (ore == Ore.EMERALD)
                return handleEmeraldGeneration(worldOreConfig, biome, chunkCoordIntPair, random, veinsPerChunk, generateFunction, service);

            final Object configuration;

            if (ore == Ore.LAPIS) {
                final int heightCenter = NumberUtil.getInt(OreControlUtil.getAmount(ore, Setting.HEIGHT_CENTER, worldOreConfig, biome), random);
                final int heightRange = NumberUtil.getInt(OreControlUtil.getAmount(ore, Setting.HEIGHT_RANGE, worldOreConfig, biome), random);
                configuration = nMSUtil.createHeightAverageConfiguration(veinsPerChunk, heightCenter, heightRange == 0 ? 1 : heightRange);
            } else {
                final int minimumHeight = NumberUtil.getInt(OreControlUtil.getAmount(ore, Setting.MINIMUM_HEIGHT, worldOreConfig, biome), random);
                final int heightSubtractValue = NumberUtil.getInt(OreControlUtil.getAmount(ore, Setting.HEIGHT_SUBTRACT_VALUE, worldOreConfig, biome), random);
                final int heightRange = NumberUtil.getInt(OreControlUtil.getAmount(ore, Setting.HEIGHT_RANGE, worldOreConfig, biome), random);

                configuration = nMSUtil.createCountConfiguration(veinsPerChunk, minimumHeight, heightSubtractValue, heightRange == 0 ? 1 : heightRange);
            }

            final int veinSize = NumberUtil.getInt(OreControlUtil.getAmount(ore, Setting.VEIN_SIZE, worldOreConfig, biome), random);

            if (veinSize == 0)
                return true;

            final Object featureConfiguration = nMSUtil.createFeatureConfiguration(defaultFeatureConfiguration, veinSize);

            return passFunction.apply(configuration, featureConfiguration);

        } catch (final Exception e) {
            final StringBuilder errorMessage = new StringBuilder("Error while generate Chunk" +
                    ", Worldname: " + worldOreConfig.getName() +
                    ", Ore: " + ore +
                    ", Biome: " + biome);

            for (final Setting setting : ore.getSettings())
                errorMessage.append(", ").append(setting).append(": ").append(OreControlUtil.getAmount(ore, setting, worldOreConfig, biome));

            throw new RuntimeException(errorMessage.toString(), e);
        }
    }

    private int calculateVeinsPerChunk(final @NonNull World world, final @NonNull Biome biome, final @NonNull ChunkCoordIntPair chunkCoordIntPair, final double veinsPerBiome) {
        final Set<ChunkCoordIntPair> chunkCoordIntPairs = getChunkCoordIntPairs(world, biome, chunkCoordIntPair);
        final ChunkCoordIntPair[] coordIntPairs = chunkCoordIntPairs.toArray(new ChunkCoordIntPair[0]);

        final Random random = getRandom(world.getSeed(), coordIntPairs[0]);
        final int veinsPerBiomeInt = NumberUtil.getInt(veinsPerBiome, random);

        int veinsAmount = 0;

        if (coordIntPairs.length == 1) {
            return veinsPerBiomeInt;
        } else {
            for (int i = 0; i < veinsPerBiomeInt; i++) {
                final int randomInt = random.nextInt((coordIntPairs.length - 1));
                final ChunkCoordIntPair coordIntPair = coordIntPairs[randomInt];
                if (coordIntPair.equals(chunkCoordIntPair))
                    veinsAmount++;
            }
        }

        return veinsAmount;
    }

    private Set<ChunkCoordIntPair> getChunkCoordIntPairs(final @NonNull World world, final @NonNull Biome biome, final @NonNull ChunkCoordIntPair chunkCoordIntPair) {
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

    private Set<ChunkCoordIntPair> getSurroundedChunkCoordIntPairs(final @NonNull World world, final @NonNull Biome biome, final @NonNull ChunkCoordIntPair chunkCoordIntPair) {
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

        int heightRange = NumberUtil.getInt(service.getValue(Ore.EMERALD, Setting.HEIGHT_RANGE, worldOreConfig, biome), random);
        final int minimumHeight = NumberUtil.getInt(service.getValue(Ore.EMERALD, Setting.MINIMUM_HEIGHT, worldOreConfig, biome), random);

        if (heightRange == 0)
            heightRange = 1;

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
