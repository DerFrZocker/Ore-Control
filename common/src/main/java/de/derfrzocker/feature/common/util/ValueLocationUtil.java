package de.derfrzocker.feature.common.util;

import de.derfrzocker.feature.api.Configuration;
import de.derfrzocker.feature.api.PlacementModifierConfiguration;
import de.derfrzocker.feature.api.Setting;
import de.derfrzocker.feature.api.Value;
import de.derfrzocker.feature.api.ValueLocation;
import de.derfrzocker.ore.control.api.config.Config;

import java.util.Optional;

public final class ValueLocationUtil {

    private ValueLocationUtil() {
    }

    public static Optional<Config> setValueLocation(Optional<Config> configOptional, ValueLocation valueLocation) {
        configOptional.ifPresent(config -> {
            setValueLocation(config.getFeature(), valueLocation);

            if (config.getPlacements() != null) {
                for (PlacementModifierConfiguration placementConfig : config.getPlacements().values()) {
                    setValueLocation(placementConfig, valueLocation);
                }
            }
        });

        return configOptional;
    }

    public static void setValueLocation(Configuration configuration, ValueLocation valueLocation) {
        if (configuration == null) {
            return;
        }

        for (Setting setting : configuration.getSettings()) {
            Value<?, ?, ?> value = configuration.getValue(setting);
            if (value != null) {
                value.setValueLocation(valueLocation);
            }
        }
    }
}
