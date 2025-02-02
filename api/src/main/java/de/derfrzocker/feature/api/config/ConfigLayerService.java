package de.derfrzocker.feature.api.config;

import java.util.Optional;

public interface ConfigLayerService {

    Optional<ConfigLayer> getById(ConfigLayerId id);
}
