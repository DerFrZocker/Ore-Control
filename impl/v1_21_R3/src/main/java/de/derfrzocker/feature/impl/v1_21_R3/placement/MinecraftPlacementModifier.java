package de.derfrzocker.feature.impl.v1_21_R3.placement;

import de.derfrzocker.feature.api.FeaturePlacementModifier;
import de.derfrzocker.feature.api.PlacementModifierConfiguration;
import de.derfrzocker.feature.api.Registries;
import de.derfrzocker.feature.api.util.Parser;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.generator.CraftLimitedRegion;
import org.bukkit.craftbukkit.util.RandomSourceWrapper;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;

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
