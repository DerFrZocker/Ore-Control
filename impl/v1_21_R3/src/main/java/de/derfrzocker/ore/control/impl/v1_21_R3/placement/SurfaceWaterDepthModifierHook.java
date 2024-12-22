package de.derfrzocker.ore.control.impl.v1_21_R3.placement;

import de.derfrzocker.feature.api.FeaturePlacementModifier;
import de.derfrzocker.feature.common.feature.placement.configuration.SurfaceWaterDepthModifierConfiguration;
import de.derfrzocker.feature.common.value.number.integer.FixedDoubleToIntegerValue;
import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.OreControlManager;
import de.derfrzocker.ore.control.impl.v1_21_R3.NMSReflectionNames;

import java.lang.reflect.Field;
import java.util.Random;

import net.minecraft.world.level.levelgen.placement.SurfaceWaterDepthFilter;
import org.bukkit.NamespacedKey;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.BlockVector;
import org.bukkit.util.NumberConversions;
import org.jetbrains.annotations.NotNull;

public class SurfaceWaterDepthModifierHook extends MinecraftPlacementModifierHook<SurfaceWaterDepthFilter, SurfaceWaterDepthModifierConfiguration> {

    public SurfaceWaterDepthModifierHook(@NotNull OreControlManager oreControlManager, @NotNull Biome biome, @NotNull NamespacedKey namespacedKey, @NotNull SurfaceWaterDepthFilter defaultModifier) {
        super(oreControlManager, "surface_water_depth_filter", defaultModifier, biome, namespacedKey);
    }

    public static SurfaceWaterDepthModifierConfiguration createDefaultConfiguration(@NotNull SurfaceWaterDepthFilter defaultModifier, @NotNull FeaturePlacementModifier<?> modifier) {
        try {
            Field maxWaterDepth = SurfaceWaterDepthFilter.class.getDeclaredField(NMSReflectionNames.SURFACE_WATER_DEPTH_FILTER_MAX_WATER_DEPTH);
            maxWaterDepth.setAccessible(true);
            Object value = maxWaterDepth.get(defaultModifier);
            return new SurfaceWaterDepthModifierConfiguration(modifier, new FixedDoubleToIntegerValue(NumberConversions.toInt(value)));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public SurfaceWaterDepthModifierConfiguration createDefaultConfiguration(@NotNull SurfaceWaterDepthFilter defaultModifier) {
        return createDefaultConfiguration(defaultModifier, getPlacementModifier());
    }

    @Override
    public SurfaceWaterDepthFilter createModifier(@NotNull SurfaceWaterDepthModifierConfiguration defaultConfiguration, @NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull BlockVector position, @NotNull LimitedRegion limitedRegion, @NotNull SurfaceWaterDepthModifierConfiguration configuration) {
        int maxWaterDepth;
        if (configuration.getMaxWaterDepth() == null) {
            maxWaterDepth = defaultConfiguration.getMaxWaterDepth().getValue(worldInfo, random, position, limitedRegion);
        } else {
            maxWaterDepth = configuration.getMaxWaterDepth().getValue(worldInfo, random, position, limitedRegion);
        }

        return SurfaceWaterDepthFilter.forMaxDepth(maxWaterDepth);
    }
}