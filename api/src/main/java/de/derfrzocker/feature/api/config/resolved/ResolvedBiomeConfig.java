package de.derfrzocker.feature.api.config.resolved;

import de.derfrzocker.feature.api.PlacedFeatureId;
import java.util.Optional;

public interface ResolvedBiomeConfig {

    Optional<ResolvedPlacedFeatureConfig> getById(PlacedFeatureId id);
}
