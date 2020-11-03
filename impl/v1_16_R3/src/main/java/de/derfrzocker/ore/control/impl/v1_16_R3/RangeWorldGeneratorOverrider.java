/*
 * MIT License
 *
 * Copyright (c) 2019 - 2020 Marvin (DerFrZocker)
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
import de.derfrzocker.ore.control.api.*;
import de.derfrzocker.spigot.utils.ChunkCoordIntPair;
import net.minecraft.server.v1_16_R3.*;
import org.apache.commons.lang.Validate;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.function.Supplier;

public class RangeWorldGeneratorOverrider extends WorldGenerator<WorldGenFeatureEmptyConfiguration> {

    @NotNull
    private final Biome biome;
    @NotNull
    private final Ore ore;
    @NotNull
    private final Supplier<OreControlService> serviceSupplier;
    @NotNull
    private final WorldGenFeatureConfigured<WorldGenFeatureOreConfiguration, ?> defaultFeatureConfiguration;
    @NotNull
    private final RangeCombinedConfiguration defaultConfiguration;

    public RangeWorldGeneratorOverrider(Codec<WorldGenFeatureEmptyConfiguration> codec, @NotNull final Biome biome, @NotNull final Ore ore, @NotNull final Supplier<OreControlService> serviceSupplier, @NotNull final WorldGenFeatureConfigured<WorldGenFeatureOreConfiguration, ?> defaultFeatureConfiguration) {
        super(codec);

        Validate.notNull(biome, "Biome cannot be null");
        Validate.notNull(ore, "Ore cannot be null");
        Validate.notNull(serviceSupplier, "Service Supplier cannot be null");
        Validate.notNull(defaultFeatureConfiguration, "WorldGenFeatureConfigured cannot be null");

        this.biome = biome;
        this.ore = ore;
        this.serviceSupplier = serviceSupplier;
        this.defaultFeatureConfiguration = defaultFeatureConfiguration;

        final OreControlService service = serviceSupplier.get();

        defaultConfiguration = (RangeCombinedConfiguration) service.getNMSService().getNMSUtil().createCountConfiguration(
                (int) service.getDefaultValue(biome, ore, Setting.VEINS_PER_CHUNK),
                (int) service.getDefaultValue(biome, ore, Setting.MINIMUM_HEIGHT),
                (int) service.getDefaultValue(biome, ore, Setting.HEIGHT_SUBTRACT_VALUE),
                (int) service.getDefaultValue(biome, ore, Setting.HEIGHT_RANGE));
    }

    public boolean a(GeneratorAccessSeed generatorAccessSeed, ChunkGenerator chunkGenerator, Random random, BlockPosition blockPosition, WorldGenFeatureEmptyConfiguration configuration) {
        final NMSService nmsService = serviceSupplier.get().getNMSService();
        final World world = generatorAccessSeed.getMinecraftWorld().getWorld();
        return nmsService.generate(world, biome, ore, new ChunkCoordIntPair(blockPosition.getX() >> 4, blockPosition.getZ() >> 4), defaultConfiguration, defaultFeatureConfiguration, null, (object, object1) -> generate(generatorAccessSeed, chunkGenerator, random, blockPosition, object, object1), random);
    }

    @Override
    public boolean generate(GeneratorAccessSeed generatorAccessSeed, ChunkGenerator chunkGenerator, Random random, BlockPosition blockPosition, WorldGenFeatureEmptyConfiguration configuration) {
        return this.a(generatorAccessSeed, chunkGenerator, random, blockPosition, configuration);
    }

    private boolean generate(GeneratorAccessSeed generatorAccessSeed, ChunkGenerator chunkGenerator, Random random, BlockPosition blockPosition, Object configuration, Object featureConfiguration) {
        final RangeCombinedConfiguration combinedConfiguration = (RangeCombinedConfiguration) configuration;
        final WorldGenFeatureConfigured<WorldGenFeatureOreConfiguration, ?> worldGenFeatureConfigured = (WorldGenFeatureConfigured<WorldGenFeatureOreConfiguration, ?>) featureConfiguration;

        return generate(generatorAccessSeed, chunkGenerator, random, blockPosition, combinedConfiguration, worldGenFeatureConfigured);
    }

    private boolean generate(GeneratorAccessSeed generatorAccessSeed, ChunkGenerator chunkGenerator, Random random, BlockPosition blockPosition, RangeCombinedConfiguration combinedConfiguration, WorldGenFeatureConfigured<WorldGenFeatureOreConfiguration, ?> feature) {
        // We are doing this, in this more or less complicated way, to simulate Minecraft's normal flow as exactly as possible
        WorldGenDecorator.c.a(null, random, new WorldGenDecoratorFrequencyConfiguration(combinedConfiguration.getVeinCount()), blockPosition)
                .forEach(countBlockPosition -> WorldGenDecorator.g.a(null, random, null, countBlockPosition)
                        .forEach(squareBlockPosition -> WorldGenDecorator.l.a(null, random, combinedConfiguration.getConfiguration(), squareBlockPosition)
                                .forEach(rangeBlockPosition -> feature.a(generatorAccessSeed, chunkGenerator, random, rangeBlockPosition)
                                )
                        )
                );

        return true;
    }

}
