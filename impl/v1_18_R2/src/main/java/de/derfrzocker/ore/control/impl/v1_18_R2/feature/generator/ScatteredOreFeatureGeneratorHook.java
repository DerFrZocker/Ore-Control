package de.derfrzocker.ore.control.impl.v1_18_R2.feature.generator;

import de.derfrzocker.feature.impl.v1_18_R2.feature.generator.configuration.OreFeatureConfiguration;
import de.derfrzocker.feature.impl.v1_18_R2.value.target.TargetValue;
import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.OreControlManager;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import org.bukkit.NamespacedKey;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ScatteredOreFeatureGeneratorHook extends MinecraftFeatureGeneratorHook<OreConfiguration, OreFeatureConfiguration> {

    public ScatteredOreFeatureGeneratorHook(@NotNull OreControlManager oreControlManager, @NotNull NamespacedKey namespacedKey, @NotNull Biome biome) {
        super(OreConfiguration.CODEC, Feature.SCATTERED_ORE, oreControlManager, "scattered_ore", biome, namespacedKey);
    }

    @Override
    public OreConfiguration createConfig(@NotNull OreConfiguration defaultConfiguration, @NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull BlockVector position, @NotNull LimitedRegion limitedRegion, @NotNull OreFeatureConfiguration configuration) {
        List<OreConfiguration.TargetBlockState> blockStates;
        if (configuration.getTargets() == null) {
            blockStates = defaultConfiguration.targetStates;
        } else {
            blockStates = new ArrayList<>();

            for (TargetValue targetValue : configuration.getTargets()) {
                blockStates.add(targetValue.getValue(worldInfo, random, position, limitedRegion));
            }
        }

        int size;
        if (configuration.getSize() == null) {
            size = defaultConfiguration.size;
        } else {
            size = configuration.getSize().getValue(worldInfo, random, position, limitedRegion);
        }

        float discardChanceOnAirExposure;
        if (configuration.getDiscardChanceOnAirExposure() == null) {
            discardChanceOnAirExposure = defaultConfiguration.discardChanceOnAirExposure;
        } else {
            discardChanceOnAirExposure = configuration.getDiscardChanceOnAirExposure().getValue(worldInfo, random, position, limitedRegion);
        }

        return new OreConfiguration(blockStates, size, discardChanceOnAirExposure);
    }
}
