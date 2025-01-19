package de.derfrzocker.feature.common.feature.placement.configuration;

import de.derfrzocker.feature.api.Value;
import de.derfrzocker.feature.api.SettingId;
import de.derfrzocker.feature.api.config.resolved.ResolvedGenericConfigParser;
import de.derfrzocker.feature.common.value.number.IntegerValue;
import java.util.function.Function;

public final class ResolvedHeightRangeConfigParser implements ResolvedGenericConfigParser<ResolvedHeightRangeConfig> {

    private static final ResolvedHeightRangeConfigParser INSTANCE = new ResolvedHeightRangeConfigParser();

    public static ResolvedHeightRangeConfigParser getInstance() {
        return INSTANCE;
    }

    @Override
    public ResolvedHeightRangeConfig parse(Function<SettingId, Value<?, ?, ?>> valueFunction) {
        return new ResolvedHeightRangeConfig((IntegerValue) valueFunction.apply(ResolvedHeightRangeConfig.HEIGHT));
    }
}
