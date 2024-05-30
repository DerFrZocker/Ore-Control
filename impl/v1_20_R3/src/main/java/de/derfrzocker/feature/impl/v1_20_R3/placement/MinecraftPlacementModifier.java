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

package de.derfrzocker.feature.impl.v1_20_R3.placement;

import de.derfrzocker.feature.api.FeaturePlacementModifier;
import de.derfrzocker.feature.api.PlacementModifierConfiguration;
import de.derfrzocker.feature.api.Registries;
import de.derfrzocker.feature.api.util.Parser;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R3.generator.CraftLimitedRegion;
import org.bukkit.craftbukkit.v1_20_R3.util.RandomSourceWrapper;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;

public abstract class MinecraftPlacementModifier<M extends PlacementModifier, C extends PlacementModifierConfiguration> implements FeaturePlacementModifier<C> {

    private final Parser<PlacementModifierConfiguration> parser;
    private final NamespacedKey namespacedKey;

    public MinecraftPlacementModifier(@NotNull Registries registries, @NotNull String name) {
        this.parser = (Parser<PlacementModifierConfiguration>) createParser(registries);
        this.namespacedKey = NamespacedKey.minecraft(name);
    }

    public abstract C mergeConfig(C first, C second);

    public abstract Parser<C> createParser(Registries registries);

    public abstract M createPlacementModifier(@NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull BlockVector position, @NotNull LimitedRegion limitedRegion, @NotNull C configuration);

    @NotNull
    @Override
    public C merge(@NotNull PlacementModifierConfiguration first, @NotNull PlacementModifierConfiguration second) {
        return mergeConfig((C) first, (C) second);
    }

    @NotNull
    @Override
    public Stream<BlockVector> getPositions(@NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull BlockVector position, @NotNull LimitedRegion limitedRegion, @NotNull C configuration) {
        WorldGenLevel level = ((CraftLimitedRegion) limitedRegion).getHandle();
        M placementModifier = createPlacementModifier(worldInfo, random, position, limitedRegion, configuration);
        return placementModifier.getPositions(new PlacementContext(level, level.getMinecraftWorld().getChunkSource().getGenerator(), Optional.empty()), new RandomSourceWrapper(random), new BlockPos(position.getBlockX(), position.getBlockY(), position.getBlockZ())).
                map(pos -> new BlockVector(pos.getX(), pos.getY(), pos.getZ()));
    }

    @NotNull
    @Override
    public Parser<PlacementModifierConfiguration> getParser() {
        return parser;
    }

    @NotNull
    @Override
    public NamespacedKey getKey() {
        return namespacedKey;
    }
}
