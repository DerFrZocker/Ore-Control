package de.derfrzocker.ore.control.impl.v1_18_R2.placement;

import de.derfrzocker.feature.api.FeaturePlacementModifier;
import de.derfrzocker.feature.api.PlacementModifierConfiguration;
import de.derfrzocker.feature.common.feature.placement.ActivationModifier;
import de.derfrzocker.feature.common.feature.placement.configuration.ActivationConfiguration;
import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.OreControlManager;
import de.derfrzocker.ore.control.api.PlacementModifierHook;
import de.derfrzocker.ore.control.api.config.Config;
import de.derfrzocker.ore.control.api.config.ConfigManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_18_R2.generator.CraftLimitedRegion;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

// TODO make more generic
public class ActivationModifierHook extends PlacementModifier implements PlacementModifierHook<ActivationConfiguration> {

    private final Map<String, PlacementModifierConfiguration> cache = new ConcurrentHashMap<>();
    private final ActivationModifier activationModifier;
    private final ConfigManager configManager;
    private final Biome biome;
    private final NamespacedKey namespacedKey;

    public ActivationModifierHook(@NotNull OreControlManager oreControlManager, @NotNull Biome biome, @NotNull NamespacedKey namespacedKey) {
        this.configManager = oreControlManager.getConfigManager();
        this.activationModifier = (ActivationModifier) oreControlManager.getRegistries().getPlacementModifierRegistry().get(ActivationModifier.KEY).get();
        this.biome = biome;
        this.namespacedKey = namespacedKey;

        oreControlManager.addValueChangeListener(cache::clear);
    }

    @Override
    public FeaturePlacementModifier<ActivationConfiguration> getPlacementModifier() {
        return activationModifier;
    }

    @Override
    public Biome getBiome() {
        return biome;
    }

    @NotNull
    @Override
    public NamespacedKey getKey() {
        return activationModifier.getKey();
    }

    @Override
    public Stream<BlockPos> getPositions(PlacementContext context, Random random, BlockPos blockPos) {
        BlockPos origin = blockPos;

        PlacementModifierConfiguration configuration = cache.computeIfAbsent(context.getLevel().getMinecraftWorld().getWorld().getName(), this::loadConfig);
        if (configuration == null) {
            return Stream.of(blockPos);
        }

        CraftLimitedRegion limitedRegion = new CraftLimitedRegion(context.getLevel(), new ChunkPos(origin));
        Stream<BlockVector> pos = activationModifier.getPositions(context.getLevel().getMinecraftWorld().getWorld(), random, new BlockVector(origin.getX(), origin.getY(), origin.getZ()), limitedRegion, (ActivationConfiguration) configuration);

        limitedRegion.breakLink();

        return pos.map(blockVector -> new BlockPos(blockVector.getBlockX(), blockVector.getBlockY(), blockVector.getBlockZ()));
    }

    private PlacementModifierConfiguration loadConfig(String name) {
        Config config = configManager.getGenerationConfig(name, biome, namespacedKey).orElse(null);

        if (config == null) {
            return null;
        }

        if (config.getPlacements() == null) {
            return null;
        }

        for (PlacementModifierConfiguration configuration : config.getPlacements()) {
            if (configuration.getOwner() == activationModifier) {
                return configuration;
            }
        }

        return null;
    }

    @Override
    public PlacementModifierType<?> type() {
        return null;
    }
}
