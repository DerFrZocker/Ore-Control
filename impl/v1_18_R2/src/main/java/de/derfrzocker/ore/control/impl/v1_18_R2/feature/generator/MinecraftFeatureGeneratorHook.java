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

package de.derfrzocker.ore.control.impl.v1_18_R2.feature.generator;

import com.mojang.serialization.Codec;
import de.derfrzocker.feature.api.FeatureGenerator;
import de.derfrzocker.feature.api.FeatureGeneratorConfiguration;
import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.FeatureGeneratorHook;
import de.derfrzocker.ore.control.api.OreControlManager;
import de.derfrzocker.ore.control.api.config.Config;
import de.derfrzocker.ore.control.api.config.ConfigManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_18_R2.generator.CraftLimitedRegion;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public abstract class MinecraftFeatureGeneratorHook<M extends FeatureConfiguration, C extends FeatureGeneratorConfiguration> extends Feature<M> implements FeatureGeneratorHook<C> {

    private final Map<String, FeatureGeneratorConfiguration> cache = new ConcurrentHashMap<>();
    private final Feature<M> feature;
    private final ConfigManager configManager;
    private final FeatureGenerator<C> featureGenerator;
    private final Biome biome;
    private final NamespacedKey namespacedKey;

    public MinecraftFeatureGeneratorHook(Codec<M> codec, Feature<M> feature, @NotNull OreControlManager oreControlManager, @NotNull String name, Biome biome, NamespacedKey namespacedKey) {
        super(codec);
        this.feature = feature;
        this.configManager = oreControlManager.getConfigManager();
        this.featureGenerator = (FeatureGenerator<C>) oreControlManager.getRegistries().getFeatureGeneratorRegistry().get(NamespacedKey.minecraft(name)).get();
        this.biome = biome;
        this.namespacedKey = namespacedKey;

        oreControlManager.addValueChangeListener(cache::clear);
    }

    public abstract M createConfig(@NotNull M defaultConfiguration, @NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull BlockVector position, @NotNull LimitedRegion limitedRegion, @NotNull C configuration);

    @Override
    public FeatureGenerator<C> getFeatureGenerator() {
        return featureGenerator;
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
    public boolean place(FeaturePlaceContext<M> context) {
        BlockPos origin = context.origin();

        FeatureGeneratorConfiguration configuration = cache.computeIfAbsent(context.level().getMinecraftWorld().getWorld().getName(), this::loadConfig);
        if (configuration == null) {
            return feature.place(context);
        }

        CraftLimitedRegion limitedRegion = new CraftLimitedRegion(context.level(), new ChunkPos(origin));
        M config = createConfig(context.config(), context.level().getMinecraftWorld().getWorld(), context.random(), new BlockVector(origin.getX(), origin.getY(), origin.getZ()), limitedRegion, (C) configuration);
        limitedRegion.breakLink();

        return place(context.topFeature(), context.level(), context.chunkGenerator(), context.random(), origin, config);
    }

    private FeatureGeneratorConfiguration loadConfig(String name) {
        Config config = configManager.getGenerationConfig(configManager.getOrCreateConfigInfo(name), biome, namespacedKey).orElse(null);

        if (config == null) {
            return null;
        }

        return config.getFeature();
    }

    private boolean place(Optional<ConfiguredFeature<?, ?>> top, WorldGenLevel world, ChunkGenerator generator, Random random, BlockPos pos, M config) {
        return feature.place(new FeaturePlaceContext<>(top, world, generator, random, pos, config));
    }
}
