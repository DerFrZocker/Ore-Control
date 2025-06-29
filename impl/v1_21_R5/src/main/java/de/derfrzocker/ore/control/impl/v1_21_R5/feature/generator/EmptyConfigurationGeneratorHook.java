package de.derfrzocker.ore.control.impl.v1_21_R5.feature.generator;

import de.derfrzocker.feature.common.feature.generator.configuration.EmptyFeatureConfiguration;
import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.OreControlManager;
import java.util.Random;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import org.bukkit.NamespacedKey;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;

public class EmptyConfigurationGeneratorHook extends MinecraftFeatureGeneratorHook<NoneFeatureConfiguration, EmptyFeatureConfiguration> {

    private EmptyConfigurationGeneratorHook(Feature<NoneFeatureConfiguration> feature, @NotNull OreControlManager oreControlManager, @NotNull String name, Biome biome, NamespacedKey namespacedKey) {
        super(NoneFeatureConfiguration.CODEC, feature, oreControlManager, name, biome, namespacedKey);
    }

    public static EmptyConfigurationGeneratorHook createGlowstoneBlobHook(@NotNull OreControlManager oreControlManager, @NotNull NamespacedKey namespacedKey, @NotNull Biome biome) {
        return new EmptyConfigurationGeneratorHook(GLOWSTONE_BLOB, oreControlManager, "glowstone_blob", biome, namespacedKey);
    }

    @Override
    public NoneFeatureConfiguration createConfig(@NotNull NoneFeatureConfiguration defaultConfiguration, @NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull BlockVector position, @NotNull LimitedRegion limitedRegion, @NotNull EmptyFeatureConfiguration configuration) {
        return defaultConfiguration;
    }
}
