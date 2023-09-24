/*
 * MIT License
 *
 * Copyright (c) 2019 - 2022 Marvin (DerFrZocker)
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

package de.derfrzocker.ore.control.impl.v1_20_R2.placement;

import de.derfrzocker.feature.api.FeaturePlacementModifier;
import de.derfrzocker.feature.api.PlacementModifierConfiguration;
import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.OreControlManager;
import de.derfrzocker.ore.control.api.PlacementModifierHook;
import de.derfrzocker.ore.control.api.config.Config;
import de.derfrzocker.ore.control.api.config.ConfigManager;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R2.generator.CraftLimitedRegion;
import org.bukkit.craftbukkit.v1_20_R2.util.RandomSourceWrapper;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public abstract class MinecraftPlacementModifierHook<M extends PlacementModifier, C extends PlacementModifierConfiguration> extends PlacementModifier implements PlacementModifierHook<C> {

    private final Map<String, PlacementModifierConfiguration> cache = new ConcurrentHashMap<>();
    private final FeaturePlacementModifier<C> placementModifier;
    private final ConfigManager configManager;
    private final M defaultModifier;
    private final C defaultConfiguration;
    private final Biome biome;
    private final NamespacedKey namespacedKey;

    public MinecraftPlacementModifierHook(@NotNull OreControlManager oreControlManager, @NotNull String name, @NotNull M defaultModifier, @NotNull Biome biome, @NotNull NamespacedKey namespacedKey) {
        this.configManager = oreControlManager.getConfigManager();
        this.placementModifier = (FeaturePlacementModifier<C>) oreControlManager.getRegistries().getPlacementModifierRegistry().get(NamespacedKey.minecraft(name)).get();
        this.defaultModifier = defaultModifier;
        this.defaultConfiguration = createDefaultConfiguration(defaultModifier);
        this.biome = biome;
        this.namespacedKey = namespacedKey;

        oreControlManager.addValueChangeListener(cache::clear);
    }

    public abstract M createModifier(@NotNull C defaultConfiguration, @NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull BlockVector position, @NotNull LimitedRegion limitedRegion, @NotNull C configuration);

    public abstract C createDefaultConfiguration(M defaultModifier);

    @Override
    public FeaturePlacementModifier<C> getPlacementModifier() {
        return placementModifier;
    }

    @Override
    public Biome getBiome() {
        return biome;
    }

    @NotNull
    @Override
    public NamespacedKey getKey() {
        return namespacedKey;
    }

    @Override
    public Stream<BlockPos> getPositions(PlacementContext context, RandomSource random, BlockPos blockPos) {
        PlacementModifierConfiguration configuration = cache.computeIfAbsent(context.getLevel().getMinecraftWorld().getWorld().getName(), this::loadConfig);

        M modifier = defaultModifier;
        if (configuration != null) {
            CraftLimitedRegion limitedRegion = new CraftLimitedRegion(context.getLevel(), new ChunkPos(blockPos));
            modifier = createModifier(defaultConfiguration, context.getLevel().getMinecraftWorld().getWorld(), new RandomSourceWrapper.RandomWrapper(random), new BlockVector(blockPos.getX(), blockPos.getY(), blockPos.getZ()), limitedRegion, (C) configuration);
            limitedRegion.breakLink();
        }

        return getPositions(context.topFeature(), context.getLevel(), context.generator(), random, blockPos, modifier);
    }

    private PlacementModifierConfiguration loadConfig(String name) {
        Config config = configManager.getGenerationConfig(configManager.getOrCreateConfigInfo(name), biome, namespacedKey).orElse(null);

        if (config == null) {
            return null;
        }

        return config.getPlacements().get(placementModifier);
    }

    @Override
    public PlacementModifierType<?> type() {
        return null;
    }

    private Stream<BlockPos> getPositions(Optional<PlacedFeature> top, WorldGenLevel world, ChunkGenerator generator, RandomSource random, BlockPos pos, M modifier) {
        return modifier.getPositions(new PlacementContext(world, generator, top), random, pos);
    }
}
