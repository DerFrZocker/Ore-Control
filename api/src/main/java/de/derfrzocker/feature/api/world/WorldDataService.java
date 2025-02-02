package de.derfrzocker.feature.api.world;

import de.derfrzocker.feature.api.config.world.WorldId;

import java.util.Optional;

public interface WorldDataService {

    Optional<WorldData> getById(WorldId id);
}
