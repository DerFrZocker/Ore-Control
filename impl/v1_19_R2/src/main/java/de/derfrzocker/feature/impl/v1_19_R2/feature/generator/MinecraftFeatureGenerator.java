package de.derfrzocker.feature.impl.v1_19_R2.feature.generator;

import de.derfrzocker.feature.api.FeatureGenerator;
import de.derfrzocker.feature.api.FeatureGeneratorConfiguration;
import de.derfrzocker.feature.api.Registries;
import de.derfrzocker.feature.api.util.Parser;
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

    private final Parser<FeatureGeneratorConfiguration> parser;
    private final Feature<M> feature;
    private final NamespacedKey namespacedKey;

    public MinecraftFeatureGenerator(Registries registries, Feature<M> feature, String name) {
        this.parser = (Parser<FeatureGeneratorConfiguration>) createParser(registries);
        this.feature = feature;
        this.namespacedKey = NamespacedKey.minecraft(name);
    }

    public abstract C mergeConfig(C first, C second);

    public abstract Parser<C> createParser(Registries registries);

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
    public Parser<FeatureGeneratorConfiguration> getParser() {
        return parser;
    }

    @NotNull
    @Override
    public NamespacedKey getKey() {
        return namespacedKey;
    }
}
