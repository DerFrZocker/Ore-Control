package de.derfrzocker.feature.api.config;

import de.derfrzocker.feature.api.SettingId;
import de.derfrzocker.feature.api.Value;
import java.util.Optional;

public interface GenericConfig {

    Optional<Value<?, ?, ?>> getById(SettingId id);
}
