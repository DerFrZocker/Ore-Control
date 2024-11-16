package de.derfrzocker.ore.control.api;

import de.derfrzocker.feature.api.FeaturePlacementModifier;
import de.derfrzocker.feature.api.PlacementModifierConfiguration;
import org.bukkit.Keyed;

public interface PlacementModifierHook<C extends PlacementModifierConfiguration> extends Keyed {

    FeaturePlacementModifier<C> getPlacementModifier();

    Biome getBiome();
}
