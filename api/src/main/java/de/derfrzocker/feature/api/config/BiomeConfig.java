package de.derfrzocker.feature.api.config;

import de.derfrzocker.ore.control.api.Biome;
import java.util.Optional;

public interface BiomeConfig {

    Optional<ValueConfig> getValueConfig(Biome biome);
}
