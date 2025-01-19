package de.derfrzocker.feature.common.feature.placement.configuration;

import de.derfrzocker.feature.api.Value;
import de.derfrzocker.feature.api.SettingId;
import de.derfrzocker.feature.api.config.resolved.ResolvedGenericConfigParser;
import de.derfrzocker.feature.common.value.number.IntegerValue;
import java.util.function.Function;

public final class ResolvedSurfaceWaterDepthConfigParser implements ResolvedGenericConfigParser<ResolvedSurfaceWaterDepthConfig> {

    private static final ResolvedSurfaceWaterDepthConfigParser INSTANCE = new ResolvedSurfaceWaterDepthConfigParser();

    public static ResolvedSurfaceWaterDepthConfigParser getInstance() {
        return INSTANCE;
    }

    @Override
    public ResolvedSurfaceWaterDepthConfig parse(Function<SettingId, Value<?, ?, ?>> valueFunction) {
        return new ResolvedSurfaceWaterDepthConfig((IntegerValue) valueFunction.apply(ResolvedSurfaceWaterDepthConfig.MAX_WATER_DEPTH));
    }
}
