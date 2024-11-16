package de.derfrzocker.ore.control.impl.v1_18_R2.feature.generator;

import de.derfrzocker.feature.common.feature.generator.configuration.EmptyFeatureConfiguration;
import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.OreControlManager;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import org.bukkit.NamespacedKey;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class GlowstoneBlobFeatureGeneratorHook extends MinecraftFeatureGeneratorHook<NoneFeatureConfiguration, EmptyFeatureConfiguration> {

    public GlowstoneBlobFeatureGeneratorHook(@NotNull OreControlManager oreControlManager, Biome biome, NamespacedKey namespacedKey) {
        super(NoneFeatureConfiguration.CODEC, GLOWSTONE_BLOB, oreControlManager, "glowstone_blob", biome, namespacedKey);
    }

    @Override
    public NoneFeatureConfiguration createConfig(@NotNull NoneFeatureConfiguration defaultConfiguration, @NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull BlockVector position, @NotNull LimitedRegion limitedRegion, @NotNull EmptyFeatureConfiguration configuration) {
        return defaultConfiguration;
    }
}
