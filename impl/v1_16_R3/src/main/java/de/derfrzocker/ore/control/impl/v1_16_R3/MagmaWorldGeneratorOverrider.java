/*
 * MIT License
 *
 * Copyright (c) 2019 - 2021 Marvin (DerFrZocker)
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
 *
 */

package de.derfrzocker.ore.control.impl.v1_16_R3;

import com.mojang.serialization.Codec;
import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.NMSService;
import de.derfrzocker.ore.control.api.Ore;
import de.derfrzocker.ore.control.api.OreControlService;
import de.derfrzocker.spigot.utils.ChunkCoordIntPair;
import net.minecraft.server.v1_16_R3.*;
import org.apache.commons.lang.Validate;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.function.Supplier;

public class MagmaWorldGeneratorOverrider extends WorldGenerator<WorldGenFeatureEmptyConfiguration> {

    @NotNull
    private final Biome biome;
    @NotNull
    private final Ore ore;
    @NotNull
    private final Supplier<OreControlService> serviceSupplier;
    @NotNull
    private final WorldGenFeatureConfigured<WorldGenFeatureOreConfiguration, ?> defaultFeatureConfiguration;

    public MagmaWorldGeneratorOverrider(Codec<WorldGenFeatureEmptyConfiguration> codec, @NotNull final Biome biome, @NotNull final Ore ore, @NotNull final Supplier<OreControlService> serviceSupplier, @NotNull final WorldGenFeatureConfigured<WorldGenFeatureOreConfiguration, ?> defaultFeatureConfiguration) {
        super(codec);

        Validate.notNull(biome, "Biome cannot be null");
        Validate.notNull(ore, "Ore cannot be null");
        Validate.notNull(serviceSupplier, "Service Supplier cannot be null");
        Validate.notNull(defaultFeatureConfiguration, "WorldGenFeatureConfigured cannot be null");

        this.biome = biome;
        this.ore = ore;
        this.serviceSupplier = serviceSupplier;
        this.defaultFeatureConfiguration = defaultFeatureConfiguration;
    }

    public boolean a(GeneratorAccessSeed generatorAccessSeed, ChunkGenerator chunkGenerator, Random random, BlockPosition blockPosition, WorldGenFeatureEmptyConfiguration configuration) {
        final NMSService nmsService = serviceSupplier.get().getNMSService();
        final World world = generatorAccessSeed.getMinecraftWorld().getWorld();
        return nmsService.generate(world, biome, ore, new ChunkCoordIntPair(blockPosition.getX() >> 4, blockPosition.getZ() >> 4), WorldGenFeatureEmptyConfiguration2.c, defaultFeatureConfiguration,
                (location, Integer) -> WorldGenerator.ORE.generate(generatorAccessSeed, chunkGenerator, random, new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ()), defaultFeatureConfiguration.f),
                (object, object1) -> generate(generatorAccessSeed, chunkGenerator, random, blockPosition, object1), random);
    }

    @Override
    public boolean generate(GeneratorAccessSeed generatorAccessSeed, ChunkGenerator chunkGenerator, Random random, BlockPosition blockPosition, WorldGenFeatureEmptyConfiguration configuration) {
        return this.a(generatorAccessSeed, chunkGenerator, random, blockPosition, configuration);
    }

    private boolean generate(GeneratorAccessSeed generatorAccessSeed, ChunkGenerator chunkGenerator, Random random, BlockPosition blockPosition, Object featureConfiguration) {
        final WorldGenFeatureConfigured<WorldGenFeatureOreConfiguration, ?> worldGenFeatureConfigured = (WorldGenFeatureConfigured<WorldGenFeatureOreConfiguration, ?>) featureConfiguration;

        return generate(generatorAccessSeed, chunkGenerator, random, blockPosition, worldGenFeatureConfigured);
    }

    private boolean generate(GeneratorAccessSeed generatorAccessSeed, ChunkGenerator chunkGenerator, Random random, BlockPosition blockPosition, WorldGenFeatureConfigured<WorldGenFeatureOreConfiguration, ?> feature) {
        // We are doing this, in this more or less complicated way, to simulate Minecraft's normal flow as exactly as possible
        WorldGenDecorator.s.a(null, random, null, blockPosition)
                .forEach(emeraldBlockPosition -> feature.a(generatorAccessSeed, chunkGenerator, random, emeraldBlockPosition)
                );

        return true;
    }

}