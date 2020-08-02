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

package de.derfrzocker.ore.control.impl.v1_13_R1;

import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.Ore;
import de.derfrzocker.ore.control.api.OreControlService;
import de.derfrzocker.spigot.utils.ChunkCoordIntPair;
import net.minecraft.server.v1_13_R1.*;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.function.Supplier;

@SuppressWarnings("Duplicates")
public class WorldGenDecoratorHeightAverageOverrider_v1_13_R1 extends WorldGenDecoratorHeightAverage {

    @NotNull
    private final Biome biome;

    @NotNull
    private final Supplier<OreControlService> serviceSupplier;

    public WorldGenDecoratorHeightAverageOverrider_v1_13_R1(@NotNull final Biome biome, @NotNull final Supplier<OreControlService> serviceSupplier) {
        Validate.notNull(biome, "Biome cannot be null");
        Validate.notNull(serviceSupplier, "Service Supplier cannot be null");

        this.biome = biome;
        this.serviceSupplier = serviceSupplier;
    }

    @Override
    public <C extends WorldGenFeatureConfiguration> boolean a(final GeneratorAccess generatorAccess, final ChunkGenerator<? extends GeneratorSettings> chunkGenerator, final Random random, final BlockPosition blockPosition, final WorldGenDecoratorHeightAverageConfiguration worldGenDecoratorHeightAverageConfiguration, final WorldGenerator<C> worldGenerator, final C worldGenFeatureConfigured) {
        return serviceSupplier.get().getNMSService().generate(generatorAccess.getMinecraftWorld().getWorld(), biome, Ore.LAPIS, new ChunkCoordIntPair(blockPosition.getX() >> 4, blockPosition.getZ() >> 4), worldGenDecoratorHeightAverageConfiguration, worldGenFeatureConfigured,
                null,
                (configuration, featureConfiguration) -> super.a(generatorAccess, chunkGenerator, random, blockPosition, (WorldGenDecoratorHeightAverageConfiguration) configuration, worldGenerator, (C) featureConfiguration)
                , random);
    }
}
