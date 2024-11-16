package de.derfrzocker.feature.api;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a configuration which has a {@link FeatureGenerator} as owner.
 */
public interface FeatureGeneratorConfiguration extends Configuration {

    /**
     * Returns the owner of this configuration.
     *
     * @return the owner of this Configuration.
     */
    @NotNull
    FeatureGenerator<?> getOwner();
}
