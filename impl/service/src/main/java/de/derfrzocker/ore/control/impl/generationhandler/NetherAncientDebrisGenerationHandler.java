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
import de.derfrzocker.spigot.utils.ChunkCoordIntPair;
import de.derfrzocker.spigot.utils.NumberUtil;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;
import java.util.function.BiFunction;

public class NetherAncientDebrisGenerationHandler implements GenerationHandler {
    @NotNull
    private final NMSUtil nmsUtil;

    public NetherAncientDebrisGenerationHandler(@NotNull final NMSUtil nmsUtil) {
        Validate.notNull(nmsUtil, "NMSUtil cannot be null");

        this.nmsUtil = nmsUtil;
    }

    @Override
    public boolean generate(@NotNull final World world, @NotNull final WorldOreConfig worldOreConfig, @NotNull final OreControlService service, @NotNull final Biome biome, @NotNull final Ore ore, @NotNull final ChunkCoordIntPair chunkCoordIntPair, @NotNull final Object defaultConfiguration, @NotNull final Object defaultFeatureConfiguration, @Nullable final BiFunction<Location, Integer, Boolean> generateFunction, @NotNull final BiFunction<Object, Object, Boolean> passFunction, @NotNull final Random random) {
        final int veinsPerChunk = NumberUtil.getInt(service.getValue(worldOreConfig, biome, ore, Setting.VEINS_PER_CHUNK), random);

        if (veinsPerChunk == 0) {
            return true;
        }

        final Object configuration;

        final int heightCenter = NumberUtil.getInt(service.getValue(worldOreConfig, biome, ore, Setting.HEIGHT_CENTER), random);
        final int heightRange = NumberUtil.getInt(service.getValue(worldOreConfig, biome, ore, Setting.HEIGHT_RANGE), random);
        configuration = nmsUtil.createHeightAverageConfiguration(veinsPerChunk, heightCenter, heightRange == 0 ? 1 : heightRange);

        final int veinSize = NumberUtil.getInt(service.getValue(worldOreConfig, biome, ore, Setting.VEIN_SIZE), random);

        if (veinSize == 0) {
            return true;
        }

        final Object featureConfiguration = nmsUtil.createFeatureConfiguration(defaultFeatureConfiguration, veinSize);

        return passFunction.apply(configuration, featureConfiguration);
    }

}
