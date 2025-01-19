package de.derfrzocker.feature.api.config.resolved;

import de.derfrzocker.feature.api.Value;
import de.derfrzocker.feature.api.SettingId;
import java.util.function.Function;

public interface ResolvedGenericConfigParser<T extends ResolvedGenericConfig> {

    T parse(Function<SettingId, Value<?, ?, ?>> valueFunction);
}
