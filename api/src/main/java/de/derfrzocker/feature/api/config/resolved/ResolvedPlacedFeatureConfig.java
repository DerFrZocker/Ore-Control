package de.derfrzocker.feature.api.config.resolved;

import de.derfrzocker.feature.api.PlacementModifierId;
import java.util.Optional;

public interface ResolvedPlacedFeatureConfig {

    Optional<ResolvedGenericConfig> getFeatureConfig();

    Optional<ResolvedGenericConfig> getModifierConfigById(PlacementModifierId id);
}
