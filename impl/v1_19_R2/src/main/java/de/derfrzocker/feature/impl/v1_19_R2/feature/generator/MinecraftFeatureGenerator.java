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

package de.derfrzocker.feature.impl.v1_19_R2.feature.generator;

import com.mojang.serialization.Codec;
import de.derfrzocker.feature.api.FeatureGenerator;
import de.derfrzocker.feature.api.FeatureGeneratorConfiguration;
import de.derfrzocker.feature.api.Registries;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_19_R2.generator.CraftLimitedRegion;
import org.bukkit.craftbukkit.v1_19_R2.util.RandomSourceWrapper;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Random;

public abstract class MinecraftFeatureGenerator<M extends FeatureConfiguration, C extends FeatureGeneratorConfiguration> implements FeatureGenerator<C> {

    private final Codec<FeatureGeneratorConfiguration> codec;
    private final Feature<M> feature;
    private final NamespacedKey namespacedKey;

    public MinecraftFeatureGenerator(Registries registries, Feature<M> feature, String name) {
        this.codec = (Codec<FeatureGeneratorConfiguration>) createCodec(registries);
        this.feature = feature;
        this.namespacedKey = NamespacedKey.minecraft(name);
    }

    public abstract C mergeConfig(C first, C second);

    public abstract Codec<C> createCodec(Registries registries);

    public abstract M createConfiguration(@NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull BlockVector position, @NotNull LimitedRegion limitedRegion, @NotNull C configuration);

    @NotNull
    @Override
    public C merge(@NotNull FeatureGeneratorConfiguration first, @NotNull FeatureGeneratorConfiguration second) {
        return mergeConfig((C) first, (C) second);
    }

    @Override
    public void place(@NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull BlockVector position, @NotNull LimitedRegion limitedRegion, @NotNull C configuration) {
        WorldGenLevel level = ((CraftLimitedRegion) limitedRegion).getHandle();
        M config = createConfiguration(worldInfo, random, position, limitedRegion, configuration);
        feature.place(new FeaturePlaceContext<>(Optional.empty(), level, level.getMinecraftWorld().getChunkSource().getGenerator(), new RandomSourceWrapper(random), new BlockPos(position.getBlockX(), position.getBlockY(), position.getBlockZ()), config));
    }

    @NotNull
    @Override
    public Codec<FeatureGeneratorConfiguration> getCodec() {
        return codec;
    }

    @NotNull
    @Override
    public NamespacedKey getKey() {
        return namespacedKey;
    }
}
