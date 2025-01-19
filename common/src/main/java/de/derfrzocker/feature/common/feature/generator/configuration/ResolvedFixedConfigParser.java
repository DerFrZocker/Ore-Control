package de.derfrzocker.feature.common.feature.generator.configuration;

import de.derfrzocker.feature.api.Value;
import de.derfrzocker.feature.api.SettingId;
import de.derfrzocker.feature.api.config.resolved.ResolvedGenericConfig;
import de.derfrzocker.feature.api.config.resolved.ResolvedGenericConfigParser;
import java.util.function.Function;

public record ResolvedFixedConfigParser<T extends ResolvedGenericConfig>(
        T instance) implements ResolvedGenericConfigParser<T> {

    @Override
    public T parse(Function<SettingId, Value<?, ?, ?>> valueFunction) {
        return instance();
    }
}
