package de.derfrzocker.feature.common.feature.generator.configuration;

import de.derfrzocker.feature.api.Value;
import de.derfrzocker.feature.api.SettingId;
import de.derfrzocker.feature.api.config.resolved.ResolvedGenericConfigParser;
import de.derfrzocker.feature.common.value.number.FloatValue;
import de.derfrzocker.feature.common.value.number.IntegerValue;
import de.derfrzocker.feature.common.value.target.TargetListValue;
import java.util.function.Function;

public final class ResolvedOreConfigParser implements ResolvedGenericConfigParser<ResolvedOreConfig> {

    private static final ResolvedOreConfigParser INSTANCE = new ResolvedOreConfigParser();

    public static ResolvedOreConfigParser getInstance() {
        return INSTANCE;
    }

    @Override
    public ResolvedOreConfig parse(Function<SettingId, Value<?, ?, ?>> valueFunction) {
        return new ResolvedOreConfig((TargetListValue) valueFunction.apply(ResolvedOreConfig.TARGETS),
                                     (IntegerValue) valueFunction.apply(ResolvedOreConfig.SIZE),
                                     (FloatValue) valueFunction.apply(ResolvedOreConfig.DISCARD_CHANCE_ON_AIR_EXPOSURE));
    }
}
