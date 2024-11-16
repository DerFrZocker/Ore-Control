package de.derfrzocker.ore.control.impl.v1_19_R2.placement;

import de.derfrzocker.feature.api.FeaturePlacementModifier;
import de.derfrzocker.feature.common.feature.placement.configuration.CountModifierConfiguration;
import de.derfrzocker.feature.common.value.number.IntegerValue;
import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.OreControlManager;
import de.derfrzocker.ore.control.impl.v1_19_R2.ConversionUtil;
import de.derfrzocker.ore.control.impl.v1_19_R2.NMSReflectionNames;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import org.bukkit.NamespacedKey;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Random;

public class CountModifierHook extends MinecraftPlacementModifierHook<CountPlacement, CountModifierConfiguration> {

    public CountModifierHook(@NotNull OreControlManager oreControlManager, @NotNull Biome biome, @NotNull NamespacedKey namespacedKey, @NotNull CountPlacement defaultModifier) {
        super(oreControlManager, "count", defaultModifier, biome, namespacedKey);
    }

    public static CountModifierConfiguration createDefaultConfiguration(@NotNull CountPlacement defaultModifier, @NotNull FeaturePlacementModifier<?> modifier) {
        try {
            Field count = CountPlacement.class.getDeclaredField(NMSReflectionNames.COUNT_PLACEMENT_COUNT);
            count.setAccessible(true);
            IntProvider value = (IntProvider) count.get(defaultModifier);
            IntegerValue integerValue = ConversionUtil.convert(value);
            return new CountModifierConfiguration(modifier, integerValue);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CountModifierConfiguration createDefaultConfiguration(@NotNull CountPlacement defaultModifier) {
        return createDefaultConfiguration(defaultModifier, getPlacementModifier());
    }

    @Override
    public CountPlacement createModifier(@NotNull CountModifierConfiguration defaultConfiguration, @NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull BlockVector position, @NotNull LimitedRegion limitedRegion, @NotNull CountModifierConfiguration configuration) {
        int count;
        if (configuration.getCount() == null) {
            count = defaultConfiguration.getCount().getValue(worldInfo, random, position, limitedRegion);
        } else {
            count = configuration.getCount().getValue(worldInfo, random, position, limitedRegion);
        }

        return CountPlacement.of(count);
    }
}
