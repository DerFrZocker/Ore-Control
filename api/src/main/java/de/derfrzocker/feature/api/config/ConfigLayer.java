package de.derfrzocker.feature.api.config;

import java.util.List;

public interface ConfigLayer {

    List<ConfigLayer> preConfig();

    List<ConfigLayer> postConfig();

    BiomeConfig getBiomeConfig();

    ValueConfig getValueConfig();
}
