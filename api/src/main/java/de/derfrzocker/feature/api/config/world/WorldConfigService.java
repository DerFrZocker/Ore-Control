package de.derfrzocker.feature.api.config.world;

import java.util.Optional;

public interface WorldConfigService {

    Optional<WorldConfig> getById(WorldId id);
}
