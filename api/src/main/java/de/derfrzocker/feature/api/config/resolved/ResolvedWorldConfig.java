package de.derfrzocker.feature.api.config.resolved;

import de.derfrzocker.feature.api.BiomId;
import java.util.Optional;

public interface ResolvedWorldConfig {

    Optional<ResolvedBiomeConfig> getById(BiomId id);
}
