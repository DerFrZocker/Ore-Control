package de.derfrzocker.feature.api;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a configuration which has a {@link FeaturePlacementModifier} as owner.
 */
public interface PlacementModifierConfiguration extends Configuration {

    /**
     * Returns the owner of this configuration.
     *
     * @return the owner of this Configuration.
     */
    @NotNull
    FeaturePlacementModifier<?> getOwner();
}
