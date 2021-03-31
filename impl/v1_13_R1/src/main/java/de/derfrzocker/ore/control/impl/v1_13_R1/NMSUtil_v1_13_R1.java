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

package de.derfrzocker.ore.control.impl.v1_13_R1;

import de.derfrzocker.ore.control.api.*;
import de.derfrzocker.spigot.utils.ChunkCoordIntPair;
import net.minecraft.server.v1_13_R1.*;
import org.apache.commons.lang.Validate;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_13_R1.CraftWorld;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

@SuppressWarnings("Duplicates")
public class NMSUtil_v1_13_R1 implements NMSUtil {

    @NotNull
    private final Supplier<OreControlService> serviceSupplier;

    public NMSUtil_v1_13_R1(@NotNull final Supplier<OreControlService> serviceSupplier) {
        Validate.notNull(serviceSupplier, "Service Supplier cannot be null");

        this.serviceSupplier = serviceSupplier;
    }

    @Override
    public void replaceNMS() {
        new NMSReplacer_v1_13_R1(serviceSupplier).replaceNMS();
    }

    @Override
    public Biome getBiome(@NotNull final World world, @NotNull final ChunkCoordIntPair chunkCoordIntPair) {
        final BiomeBase biomeBase = ((CraftWorld) world).getHandle().getChunkProvider().getChunkGenerator().getWorldChunkManager().getBiome(new BlockPosition(chunkCoordIntPair.getX() << 4, 0, chunkCoordIntPair.getZ() << 4), null);

        return Biome.valueOf(BiomeBase.REGISTRY_ID.b(biomeBase).getKey().toUpperCase());
    }

    @Override
    public Object createFeatureConfiguration(@NotNull final Object defaultFeatureConfiguration, final int veinsSize) {
        final WorldGenFeatureOreConfiguration worldGenFeatureOreConfiguration = (WorldGenFeatureOreConfiguration) defaultFeatureConfiguration;
        return new WorldGenFeatureOreConfiguration(worldGenFeatureOreConfiguration.b, worldGenFeatureOreConfiguration.d, veinsSize);
    }

    @Override
    public Object createCountConfiguration(int veinsPerChunk, int minimumHeight, int heightSubtractValue, int heightRange) {
        return new WorldGenFeatureChanceDecoratorCountConfiguration(veinsPerChunk, minimumHeight, heightSubtractValue, heightRange);
    }

    @Override
    public Object createHeightAverageConfiguration(int veinsPerChunk, int heightCenter, int heightRange) {
        return new WorldGenDecoratorHeightAverageConfiguration(veinsPerChunk, heightCenter, heightRange);
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

        return null;
    }

    @NotNull
    @Override
    public Dimension getDimension(@NotNull final World world) {
        if (world.getGenerator() != null) {
            return Dimension.CUSTOM;
        }

        final WorldServer worldServer = ((CraftWorld) world).getHandle();
        final WorldProvider worldProvider = worldServer.worldProvider.getDimensionManager().d();

        if (worldProvider instanceof WorldProviderNormal) {
            return Dimension.OVERWORLD;
        }
        if (worldProvider instanceof WorldProviderHell) {
            return Dimension.NETHER;
        }
        if (worldProvider instanceof WorldProviderTheEnd) {
            return Dimension.THE_END;
        }

        return Dimension.CUSTOM;
    }

}
