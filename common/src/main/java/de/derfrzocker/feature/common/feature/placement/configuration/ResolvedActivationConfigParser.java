package de.derfrzocker.feature.common.feature.placement.configuration;

import de.derfrzocker.feature.api.Value;
import de.derfrzocker.feature.api.SettingId;
import de.derfrzocker.feature.api.config.resolved.ResolvedGenericConfigParser;
import de.derfrzocker.feature.common.value.bool.BooleanValue;
import java.util.function.Function;

public final class ResolvedActivationConfigParser implements ResolvedGenericConfigParser<ResolvedActivationConfig> {

    private static final ResolvedActivationConfigParser INSTANCE = new ResolvedActivationConfigParser();

    public static ResolvedActivationConfigParser getInstance() {
        return INSTANCE;
    }

    @Override
    public ResolvedActivationConfig parse(Function<SettingId, Value<?, ?, ?>> valueFunction) {
        return new ResolvedActivationConfig((BooleanValue) valueFunction.apply(ResolvedActivationConfig.ACTIVATE));
    }
}
