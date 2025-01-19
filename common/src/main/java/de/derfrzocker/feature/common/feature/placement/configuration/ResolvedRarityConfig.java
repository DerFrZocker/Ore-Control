package de.derfrzocker.feature.common.feature.placement.configuration;

import de.derfrzocker.feature.api.SettingId;
import de.derfrzocker.feature.api.config.resolved.ResolvedGenericConfig;
import de.derfrzocker.feature.common.value.number.IntegerValue;
import org.bukkit.NamespacedKey;

public record ResolvedRarityConfig(IntegerValue chance) implements ResolvedGenericConfig {

    public static final SettingId CHANCE = new SettingId(NamespacedKey.fromString("feature:chance"));
}
