package de.derfrzocker.feature.common.feature.placement.configuration;

import de.derfrzocker.feature.api.SettingId;
import de.derfrzocker.feature.api.config.resolved.ResolvedGenericConfig;
import de.derfrzocker.feature.common.value.number.IntegerValue;
import org.bukkit.NamespacedKey;

public record ResolvedSurfaceWaterDepthConfig(IntegerValue maxWaterDepth) implements ResolvedGenericConfig {

    public static final SettingId MAX_WATER_DEPTH = new SettingId(NamespacedKey.fromString("feature:max-water-depth"));
}
