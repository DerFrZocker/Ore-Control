package de.derfrzocker.ore.control.api;

import de.derfrzocker.feature.api.config.ConfigLayerId;

public interface ConfigLayerInfo {

    ConfigLayerId id();

    String name();

    String description();
}
