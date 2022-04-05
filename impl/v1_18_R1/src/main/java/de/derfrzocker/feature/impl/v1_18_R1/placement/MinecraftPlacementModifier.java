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

package de.derfrzocker.feature.impl.v1_18_R1.placement;

import com.mojang.serialization.Codec;
import de.derfrzocker.feature.api.FeaturePlacementModifier;
import de.derfrzocker.feature.api.PlacementModifierConfiguration;
import de.derfrzocker.feature.api.Registries;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_18_R1.generator.CraftLimitedRegion;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;

public abstract class MinecraftPlacementModifier<M extends PlacementModifier, C extends PlacementModifierConfiguration> implements FeaturePlacementModifier<C> {

    private final Codec<PlacementModifierConfiguration> codec;
    private final NamespacedKey namespacedKey;

    public MinecraftPlacementModifier(@NotNull Registries registries, @NotNull String name) {
        this.codec = (Codec<PlacementModifierConfiguration>) createCodec(registries);
        this.namespacedKey = NamespacedKey.minecraft(name);
    }

    public abstract C mergeConfig(C first, C second);

    public abstract Codec<C> createCodec(Registries registries);

    public abstract M createPlacementModifier(@NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull BlockVector position, @NotNull LimitedRegion limitedRegion, @NotNull C configuration);

    @Override
    public C merge(PlacementModifierConfiguration first, PlacementModifierConfiguration second) {
        return mergeConfig((C) first, (C) second);
    }

    @Override
    public Stream<BlockVector> getPositions(@NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull BlockVector position, @NotNull LimitedRegion limitedRegion, @NotNull C configuration) {
        WorldGenLevel level = ((CraftLimitedRegion) limitedRegion).getHandle();
        M placementModifier = createPlacementModifier(worldInfo, random, position, limitedRegion, configuration);
        return placementModifier.getPositions(new PlacementContext(level, level.getMinecraftWorld().getChunkSource().getGenerator(), Optional.empty()), random, new BlockPos(position.getBlockX(), position.getBlockY(), position.getBlockZ())).
                map(pos -> new BlockVector(pos.getX(), pos.getY(), pos.getZ()));
    }

    @Override
    public Codec<PlacementModifierConfiguration> getCodec() {
        return codec;
    }

    @NotNull
    @Override
    public NamespacedKey getKey() {
        return namespacedKey;
    }
}
