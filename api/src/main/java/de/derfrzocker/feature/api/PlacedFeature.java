package de.derfrzocker.feature.api;

import java.util.List;

public record PlacedFeature(FeatureId featureId, List<PlacementModifierId> placementModifierIds) {
}
