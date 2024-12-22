package de.derfrzocker.feature.api;

import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Represents a Feature which can generate in a world.
 * Each Feature contains one generator and can have multiple placement modifiers.
 * The order of the placement modifiers is important. A list which preserves its order should be used.
 *
 * @param namedKey           The unique key of this feature.
 * @param generator          The generator which generates the blocks.
 * @param placementModifiers The placement modifiers which determine the positions to generate the feature.
 */
public record Feature(@NotNull NamespacedKey namedKey, @NotNull FeatureGenerator<?> generator,
                      @NotNull List<FeaturePlacementModifier<?>> placementModifiers) implements Keyed {

    @NotNull
    @Override
    public NamespacedKey getKey() {
        return namedKey();
    }
}
