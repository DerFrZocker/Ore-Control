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

import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.Dimension;
import de.derfrzocker.ore.control.api.NMSUtil;
import de.derfrzocker.ore.control.api.Ore;
import de.derfrzocker.ore.control.api.OreControlService;
import de.derfrzocker.spigot.utils.ChunkCoordIntPair;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RangeDecoratorConfiguration;
import net.minecraft.world.level.levelgen.heightproviders.TrapezoidHeight;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

@SuppressWarnings("Duplicates")
public class NMSUtil_v1_17_R1 implements NMSUtil {

    @NotNull
    private final Supplier<OreControlService> serviceSupplier;

    public NMSUtil_v1_17_R1(@NotNull final Supplier<OreControlService> serviceSupplier) {
        Validate.notNull(serviceSupplier, "Service Supplier cannot be null");

        this.serviceSupplier = serviceSupplier;
    }

    @Override
    public void replaceNMS() {
        new NMSReplacer_v1_17_R1(serviceSupplier).replaceNMS();
    }

    @Override
    public Biome getBiome(@NotNull final World world, @NotNull final ChunkCoordIntPair chunkCoordIntPair) {
        final net.minecraft.world.level.biome.Biome biomeBase = ((CraftWorld) world).getHandle().getChunkProvider().getGenerator().getBiomeSource().getNoiseBiome(chunkCoordIntPair.getX() << 4, 0, chunkCoordIntPair.getZ() << 4);

        return Biome.valueOf(getRegistry().registryOrThrow(Registry.BIOME_REGISTRY).getKey(biomeBase).getPath().toUpperCase());
    }

    @Override
    public Object createFeatureConfiguration(@NotNull final Object defaultFeatureConfiguration, final int veinsSize) {
        final ConfiguredFeature<OreConfiguration, ?> worldGenFeatureConfigured = (ConfiguredFeature<OreConfiguration, ?>) defaultFeatureConfiguration;
        final OreConfiguration worldGenFeatureOreConfiguration = new OreConfiguration(worldGenFeatureConfigured.config.targetStates, veinsSize, worldGenFeatureConfigured.config.discardChanceOnAirExposure);

        return new ConfiguredFeature<>(worldGenFeatureConfigured.feature, worldGenFeatureOreConfiguration);
    }

    @Override
    public Object createCountConfiguration(final int veinsPerChunk, final int minimumHeight, final int heightSubtractValue, final int heightRange) {
        return new RangeCombinedConfiguration(veinsPerChunk, new RangeDecoratorConfiguration(UniformHeight.of(VerticalAnchor.absolute(minimumHeight), VerticalAnchor.absolute(minimumHeight + (heightRange - heightSubtractValue)))));
    }

    @Override
    public Object createHeightAverageConfiguration(final int veinsPerChunk, final int heightCenter, final int heightRange) {
        return new DepthAveragedCombinedConfiguration(veinsPerChunk, new RangeDecoratorConfiguration(TrapezoidHeight.of(VerticalAnchor.absolute(heightCenter - heightRange), VerticalAnchor.absolute(heightCenter + heightRange))));
    }

    @Override
    public Ore getOre(@NotNull final Object object) {
        if (object == Blocks.DIAMOND_ORE) {
            return Ore.DIAMOND;
        }
        if (object == Blocks.COAL_ORE) {
            return Ore.COAL;
        }
        if (object == Blocks.IRON_ORE) {
            return Ore.IRON;
        }
        if (object == Blocks.REDSTONE_ORE) {
            return Ore.REDSTONE;
        }
        if (object == Blocks.GOLD_ORE) {
            return Ore.GOLD;
        }
        if (object == Blocks.DIRT) {
            return Ore.DIRT;
        }
        if (object == Blocks.GRAVEL) {
            return Ore.GRAVEL;
        }
        if (object == Blocks.GRANITE) {
            return Ore.GRANITE;
        }
        if (object == Blocks.DIORITE) {
            return Ore.DIORITE;
        }
        if (object == Blocks.ANDESITE) {
            return Ore.ANDESITE;
        }
        if (object == Blocks.NETHER_QUARTZ_ORE) {
            return Ore.NETHER_QUARTZ;
        }
        if (object == Blocks.INFESTED_STONE) {
            return Ore.INFESTED_STONE;
        }
        if (object == Blocks.MAGMA_BLOCK) {
            return Ore.MAGMA;
        }
        if (object == Blocks.NETHER_GOLD_ORE) {
            return Ore.NETHER_GOLD;
        }
        if (object == Blocks.ANCIENT_DEBRIS) {
            return Ore.ANCIENT_DEBRIS;
        }
        if (object == Blocks.COPPER_ORE) {
            return Ore.COPPER;
        }

        return null;
    }

    @NotNull
    @Override
    public Dimension getDimension(@NotNull final World world) {
        if (world.getGenerator() != null) {
            return Dimension.CUSTOM;
        }

        final ServerLevel worldServer = ((CraftWorld) world).getHandle();
        final DimensionType dimensionManager = worldServer.dimensionType();

        final MappedRegistry<LevelStem> registryMaterials = worldServer.E.worldGenSettings().dimensions();

        check:
        {
            final LevelStem worlddimension = registryMaterials.get(LevelStem.OVERWORLD);

            if (worlddimension == null) {
                break check;
            }

            final DimensionType other = worlddimension.typeSupplier().get();

            if (other == dimensionManager) {
                return Dimension.OVERWORLD;
            }
        }

        check:
        {
            final LevelStem worlddimension = registryMaterials.get(LevelStem.NETHER);

            if (worlddimension == null) {
                break check;
            }

            final DimensionType other = worlddimension.typeSupplier().get();

            if (other == dimensionManager) {
                return Dimension.NETHER;
            }
        }

        check:
        {
            final LevelStem worlddimension = registryMaterials.get(LevelStem.END);

            if (worlddimension == null) {
                break check;
            }

            final DimensionType other = worlddimension.typeSupplier().get();

            if (other == dimensionManager) {
                return Dimension.THE_END;
            }
        }

        return Dimension.CUSTOM;
    }

    @NotNull
    private RegistryAccess getRegistry() {
        final DedicatedServer server = ((CraftServer) Bukkit.getServer()).getServer();

        return server.registryAccess();
    }

}
