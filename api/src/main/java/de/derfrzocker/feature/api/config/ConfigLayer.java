package de.derfrzocker.feature.api.config;

import java.util.List;

public interface ConfigLayer {

    List<ConfigLayerId> preConfig();

    List<ConfigLayerId> postConfig();

    BiomeConfig biomeConfig();

    ValueConfig valueConfig();
}
