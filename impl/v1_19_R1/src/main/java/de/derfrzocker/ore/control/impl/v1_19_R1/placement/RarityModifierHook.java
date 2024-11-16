package de.derfrzocker.ore.control.impl.v1_19_R1.placement;

import de.derfrzocker.feature.api.FeaturePlacementModifier;
import de.derfrzocker.feature.common.feature.placement.configuration.RarityModifierConfiguration;
import de.derfrzocker.feature.common.value.number.integer.FixedDoubleToIntegerValue;
import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.OreControlManager;
import de.derfrzocker.ore.control.impl.v1_19_R1.NMSReflectionNames;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import org.bukkit.NamespacedKey;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.BlockVector;
import org.bukkit.util.NumberConversions;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Random;

public class RarityModifierHook extends MinecraftPlacementModifierHook<RarityFilter, RarityModifierConfiguration> {

    public RarityModifierHook(@NotNull OreControlManager oreControlManager, @NotNull Biome biome, @NotNull NamespacedKey namespacedKey, @NotNull RarityFilter defaultModifier) {
        super(oreControlManager, "rarity_filter", defaultModifier, biome, namespacedKey);
    }

    public static RarityModifierConfiguration createDefaultConfiguration(@NotNull RarityFilter defaultModifier, @NotNull FeaturePlacementModifier<?> modifier) {
        try {
            Field chance = RarityFilter.class.getDeclaredField(NMSReflectionNames.RARITY_FILTER_CHANCE);
            chance.setAccessible(true);
            Object value = chance.get(defaultModifier);
            return new RarityModifierConfiguration(modifier, new FixedDoubleToIntegerValue(NumberConversions.toInt(value)));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public RarityModifierConfiguration createDefaultConfiguration(@NotNull RarityFilter defaultModifier) {
        return createDefaultConfiguration(defaultModifier, getPlacementModifier());
    }

    @Override
    public RarityFilter createModifier(@NotNull RarityModifierConfiguration defaultConfiguration, @NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull BlockVector position, @NotNull LimitedRegion limitedRegion, @NotNull RarityModifierConfiguration configuration) {
        int chance;
        if (configuration.getChance() == null) {
            chance = defaultConfiguration.getChance().getValue(worldInfo, random, position, limitedRegion);
        } else {
            chance = configuration.getChance().getValue(worldInfo, random, position, limitedRegion);
        }

        return RarityFilter.onAverageOnceEvery(chance);
    }
}
