package de.derfrzocker.feature.api.config;

import de.derfrzocker.feature.api.FeaturePlacementModifier;
import java.util.Optional;

public interface PlacedFeatureConfig {

    Optional<GenericConfig> getById(FeaturePlacementModifier<?> modifier);
}
