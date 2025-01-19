package de.derfrzocker.feature.common.feature.generator.configuration;

import de.derfrzocker.feature.api.SettingId;
import de.derfrzocker.feature.api.config.resolved.ResolvedGenericConfig;
import de.derfrzocker.feature.common.value.number.FloatValue;
import de.derfrzocker.feature.common.value.number.IntegerValue;
import de.derfrzocker.feature.common.value.target.TargetListValue;
import org.bukkit.NamespacedKey;

public record ResolvedOreConfig(TargetListValue targets, IntegerValue size,
                                FloatValue discardChanceOnAirExposure) implements ResolvedGenericConfig {

    public static final SettingId TARGETS = new SettingId(NamespacedKey.fromString("feature:targets"));
    public static final SettingId SIZE = new SettingId(NamespacedKey.fromString("feature:size"));
    public static final SettingId DISCARD_CHANCE_ON_AIR_EXPOSURE = new SettingId(NamespacedKey.fromString(
            "feature:discard-chance-on-air-exposure"));
}
