package de.derfrzocker.feature.common.feature.placement.configuration;

import de.derfrzocker.feature.api.Value;
import de.derfrzocker.feature.api.SettingId;
import de.derfrzocker.feature.api.config.resolved.ResolvedGenericConfigParser;
import de.derfrzocker.feature.common.value.number.IntegerValue;
import java.util.function.Function;

public final class ResolvedCountConfigParser implements ResolvedGenericConfigParser<ResolvedCountConfig> {

    private static final ResolvedCountConfigParser INSTANCE = new ResolvedCountConfigParser();

    public static ResolvedCountConfigParser getInstance() {
        return INSTANCE;
    }

    @Override
    public ResolvedCountConfig parse(Function<SettingId, Value<?, ?, ?>> valueFunction) {
        return new ResolvedCountConfig((IntegerValue) valueFunction.apply(ResolvedCountConfig.COUNT));
    }
}
