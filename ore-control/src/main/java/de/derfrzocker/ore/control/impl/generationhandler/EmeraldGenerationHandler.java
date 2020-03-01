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

package de.derfrzocker.ore.control.impl.generationhandler;

import de.derfrzocker.ore.control.api.*;
import de.derfrzocker.ore.control.utils.GenerationUtil;
import de.derfrzocker.ore.control.utils.OreControlUtil;
import de.derfrzocker.spigot.utils.ChunkCoordIntPair;
import de.derfrzocker.spigot.utils.NumberUtil;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;
import java.util.function.BiFunction;

public class EmeraldGenerationHandler implements GenerationHandler {
    @NotNull
    private final NMSUtil nmsUtil;

    public EmeraldGenerationHandler(@NotNull final NMSUtil nmsUtil) {
        Validate.notNull(nmsUtil, "NMSUtil can not be null");

        this.nmsUtil = nmsUtil;
    }

    @Override
    public boolean generate(@NotNull final World world, @NotNull final WorldOreConfig worldOreConfig, @NotNull final OreControlService service, @NotNull final Biome biome, @NotNull final Ore ore, @NotNull final ChunkCoordIntPair chunkCoordIntPair, @NotNull final Object defaultConfiguration, @NotNull final Object defaultFeatureConfiguration, @Nullable final BiFunction<Location, Integer, Boolean> generateFunction, @NotNull final BiFunction<Object, Object, Boolean> passFunction, @NotNull final Random random) {
        final double veinsPerBiome = OreControlUtil.getAmount(ore, Setting.VEINS_PER_BIOME, worldOreConfig, biome);
        final int veinsPerChunk;

        if (veinsPerBiome > 0) {
            veinsPerChunk = GenerationUtil.calculateVeinsPerChunk(nmsUtil, world, biome, chunkCoordIntPair, veinsPerBiome);
        } else {
            final int minimumOresPerChunk = NumberUtil.getInt(service.getValue(ore, Setting.MINIMUM_ORES_PER_CHUNK, worldOreConfig, biome), random);
            final int oresPerChunkRange = NumberUtil.getInt(service.getValue(ore, Setting.ORES_PER_CHUNK_RANGE, worldOreConfig, biome), random);

            veinsPerChunk = minimumOresPerChunk + oresPerChunkRange == 0 ? 0 : random.nextInt(oresPerChunkRange);
        }

        if (veinsPerChunk == 0)
            return true;

        int heightRange = NumberUtil.getInt(service.getValue(ore, Setting.HEIGHT_RANGE, worldOreConfig, biome), random);
        final int minimumHeight = NumberUtil.getInt(service.getValue(ore, Setting.MINIMUM_HEIGHT, worldOreConfig, biome), random);

        if (heightRange == 0)
            heightRange = 1;

        final Location location = new Location(null, chunkCoordIntPair.getX() << 4, 0, chunkCoordIntPair.getZ() << 4);

        for (int i = 0; i < veinsPerChunk; ++i) {
            final int x = random.nextInt(16);
            final int y = random.nextInt(heightRange) + minimumHeight;
            final int z = random.nextInt(16);
            generateFunction.apply(location.clone().add(x, y, z), -1);
        }

        return true;
    }

}
