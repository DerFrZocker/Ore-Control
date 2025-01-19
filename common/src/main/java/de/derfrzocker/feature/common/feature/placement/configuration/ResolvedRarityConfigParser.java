package de.derfrzocker.feature.common.feature.placement.configuration;

import de.derfrzocker.feature.api.Value;
import de.derfrzocker.feature.api.SettingId;
import de.derfrzocker.feature.api.config.resolved.ResolvedGenericConfigParser;
import de.derfrzocker.feature.common.value.number.IntegerValue;
import java.util.function.Function;

public final class ResolvedRarityConfigParser implements ResolvedGenericConfigParser<ResolvedRarityConfig> {

    private static final ResolvedRarityConfigParser INSTANCE = new ResolvedRarityConfigParser();

    public static ResolvedRarityConfigParser getInstance() {
        return INSTANCE;
    }

    @Override
    public ResolvedRarityConfig parse(Function<SettingId, Value<?, ?, ?>> valueFunction) {
        return new ResolvedRarityConfig((IntegerValue) valueFunction.apply(ResolvedRarityConfig.CHANCE));
    }
}
