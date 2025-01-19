package de.derfrzocker.feature.common.feature.placement.configuration;

import de.derfrzocker.feature.api.SettingId;
import de.derfrzocker.feature.api.config.resolved.ResolvedGenericConfig;
import de.derfrzocker.feature.common.value.bool.BooleanValue;
import org.bukkit.NamespacedKey;

public record ResolvedActivationConfig(BooleanValue activate) implements ResolvedGenericConfig {

    public static final SettingId ACTIVATE = new SettingId(NamespacedKey.fromString("feature:activate"));
}
