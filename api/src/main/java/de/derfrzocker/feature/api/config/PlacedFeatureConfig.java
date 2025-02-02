package de.derfrzocker.feature.api.config;

import de.derfrzocker.feature.api.PlacementModifierId;

import java.util.Optional;

public interface PlacedFeatureConfig {

    Optional<GenericConfig> featureConfig();

    Optional<GenericConfig> modifierConfigById(PlacementModifierId id);
}
