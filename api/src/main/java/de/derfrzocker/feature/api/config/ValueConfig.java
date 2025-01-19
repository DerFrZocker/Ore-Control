package de.derfrzocker.feature.api.config;

import de.derfrzocker.feature.api.Feature;
import java.util.Optional;

public interface ValueConfig {

    Optional<PlacedFeatureConfig> getValue(Feature feature);
}
