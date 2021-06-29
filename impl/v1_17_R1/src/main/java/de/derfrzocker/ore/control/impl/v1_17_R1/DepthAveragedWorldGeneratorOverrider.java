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

package de.derfrzocker.ore.control.impl.v1_17_R1;

import com.mojang.serialization.Codec;
import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.NMSService;
import de.derfrzocker.ore.control.api.Ore;
import de.derfrzocker.ore.control.api.OreControlService;
import de.derfrzocker.ore.control.api.Setting;
import de.derfrzocker.spigot.utils.ChunkCoordIntPair;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.CountConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;
import org.apache.commons.lang.Validate;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.function.Supplier;

public class DepthAveragedWorldGeneratorOverrider extends Feature<NoneFeatureConfiguration> {

    @NotNull
    private final Biome biome;
    @NotNull
    private final Ore ore;
    @NotNull
    private final Supplier<OreControlService> serviceSupplier;
    @NotNull
    private final ConfiguredFeature<OreConfiguration, ?> defaultFeatureConfiguration;
    @NotNull
    private final DepthAveragedCombinedConfiguration defaultConfiguration;

    public DepthAveragedWorldGeneratorOverrider(Codec<NoneFeatureConfiguration> codec, @NotNull final Biome biome, @NotNull final Ore ore, @NotNull final Supplier<OreControlService> serviceSupplier, @NotNull final ConfiguredFeature<OreConfiguration, ?> defaultFeatureConfiguration) {
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

        defaultConfiguration = (DepthAveragedCombinedConfiguration) service.getNMSService().getNMSUtil().createHeightAverageConfiguration(
                (int) service.getDefaultValue(biome, ore, Setting.VEINS_PER_CHUNK),
                (int) service.getDefaultValue(biome, ore, Setting.HEIGHT_CENTER),
                (int) service.getDefaultValue(biome, ore, Setting.HEIGHT_RANGE));
    }

    public boolean a(WorldGenLevel generatorAccessSeed, ChunkGenerator chunkGenerator, Random random, BlockPos blockPosition, NoneFeatureConfiguration configuration) {
        final NMSService nmsService = serviceSupplier.get().getNMSService();
        final World world = generatorAccessSeed.getMinecraftWorld().getWorld();
        return nmsService.generate(world, biome, ore, new ChunkCoordIntPair(blockPosition.getX() >> 4, blockPosition.getZ() >> 4), defaultConfiguration, defaultFeatureConfiguration, null, (object, object1) -> generate(generatorAccessSeed, chunkGenerator, random, blockPosition, object, object1), random);
    }

    public boolean generate(WorldGenLevel generatorAccessSeed, ChunkGenerator chunkGenerator, Random random, BlockPos blockPosition, NoneFeatureConfiguration configuration) {
        return this.a(generatorAccessSeed, chunkGenerator, random, blockPosition, configuration);
    }

    private boolean generate(WorldGenLevel generatorAccessSeed, ChunkGenerator chunkGenerator, Random random, BlockPos blockPosition, Object configuration, Object featureConfiguration) {
        final DepthAveragedCombinedConfiguration combinedConfiguration = (DepthAveragedCombinedConfiguration) configuration;
        final ConfiguredFeature<OreConfiguration, ?> worldGenFeatureConfigured = (ConfiguredFeature<OreConfiguration, ?>) featureConfiguration;

        return generate(generatorAccessSeed, chunkGenerator, random, blockPosition, combinedConfiguration, worldGenFeatureConfigured);
    }

    private boolean generate(WorldGenLevel generatorAccessSeed, ChunkGenerator chunkGenerator, Random random, BlockPos blockPosition, DepthAveragedCombinedConfiguration combinedConfiguration, ConfiguredFeature<OreConfiguration, ?> feature) {
        // We are doing this, in this more or less complicated way, to simulate Minecraft's normal flow as exactly as possible
        FeatureDecorator.COUNT.getPositions(null, random, new CountConfiguration(combinedConfiguration.getVeinCount()), blockPosition)
                .forEach(countBlockPosition -> FeatureDecorator.SQUARE.getPositions(null, random, null, countBlockPosition)
                        .forEach(squareBlockPosition -> FeatureDecorator.RANGE.getPositions(null, random, combinedConfiguration.getConfiguration(), squareBlockPosition)
                                .forEach(rangeBlockPosition -> feature.place(generatorAccessSeed, chunkGenerator, random, rangeBlockPosition)
                                )
                        )
                );

        return true;
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> featurePlaceContext) {
        return this.a(featurePlaceContext.level(), featurePlaceContext.chunkGenerator(), featurePlaceContext.random(), featurePlaceContext.origin(), featurePlaceContext.config());
    }
}
