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

package de.derfrzocker.ore.control.utils;

import com.google.common.collect.Sets;
import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.NMSUtil;
import de.derfrzocker.spigot.utils.ChunkCoordIntPair;
import de.derfrzocker.spigot.utils.NumberUtil;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class GenerationUtil {

    public static int calculateVeinsPerChunk(@NotNull final NMSUtil nmsUtil, @NotNull final World world, @NotNull final Biome biome, @NotNull final ChunkCoordIntPair chunkCoordIntPair, final double veinsPerBiome) {
        final Set<ChunkCoordIntPair> chunkCoordIntPairs = getChunkCoordIntPairs(nmsUtil, world, biome, chunkCoordIntPair);
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
                if (coordIntPair.equals(chunkCoordIntPair)) {
                    veinsAmount++;
                }
            }
        }

        return veinsAmount;
    }

    public static Set<ChunkCoordIntPair> getChunkCoordIntPairs(@NotNull final NMSUtil nmsUtil, @NotNull final World world, @NotNull final Biome biome, @NotNull final ChunkCoordIntPair chunkCoordIntPair) {
        final Set<ChunkCoordIntPair> chunkCoordIntPairs = new TreeSet<>();

        final Set<ChunkCoordIntPair> chunkCoordIntPairsToCheck = Sets.newHashSet(chunkCoordIntPair);

        Iterator<ChunkCoordIntPair> chunkCoordIntPairsToCheckIterator = chunkCoordIntPairsToCheck.iterator();
        while (chunkCoordIntPairsToCheckIterator.hasNext()) {
            final ChunkCoordIntPair chunkCoordIntPairToCheck = chunkCoordIntPairsToCheckIterator.next();

            chunkCoordIntPairs.add(chunkCoordIntPairToCheck);
            chunkCoordIntPairsToCheck.remove(chunkCoordIntPairToCheck);

            final Set<ChunkCoordIntPair> set = getSurroundedChunkCoordIntPairs(nmsUtil, world, biome, chunkCoordIntPairToCheck);

            set.removeAll(chunkCoordIntPairs);
            chunkCoordIntPairsToCheck.addAll(set);

            chunkCoordIntPairsToCheckIterator = chunkCoordIntPairsToCheck.iterator();
        }

        return chunkCoordIntPairs;

    }

    public static Set<ChunkCoordIntPair> getSurroundedChunkCoordIntPairs(@NotNull final NMSUtil nmsUtil, @NotNull final World world, @NotNull final Biome biome, @NotNull final ChunkCoordIntPair chunkCoordIntPair) {
        final Set<ChunkCoordIntPair> chunkCoordIntPairs = new HashSet<>();

        for (int x = -1; x < 2; x++) {
            for (int z = -1; z < 2; z++) {
                final int xToCheck = chunkCoordIntPair.getX() + x;
                final int zToCheck = chunkCoordIntPair.getZ() + z;


                final ChunkCoordIntPair chunkCoordIntPairToCheck = new ChunkCoordIntPair(xToCheck, zToCheck);

                final Biome biomeToCheck = nmsUtil.getBiome(world, chunkCoordIntPairToCheck);

                if (biomeToCheck == null) {
                    throw new NullPointerException("BiomeBase for chunk position x: " + xToCheck + " and z: " + zToCheck + " is null, this should never happen!");
                }

                if (biome == biomeToCheck) {
                    chunkCoordIntPairs.add(chunkCoordIntPairToCheck);
                }
            }
        }
        return chunkCoordIntPairs;
    }

    public static Random getRandom(final long seed, @NotNull final ChunkCoordIntPair chunkCoordIntPair) {
        final Random random = new Random(seed);

        final long long1 = random.nextLong();
        final long long2 = random.nextLong();
        final long newSeed = (long) chunkCoordIntPair.getX() * long1 ^ (long) chunkCoordIntPair.getZ() * long2 ^ seed;
        random.setSeed(newSeed);

        return random;
    }

}
