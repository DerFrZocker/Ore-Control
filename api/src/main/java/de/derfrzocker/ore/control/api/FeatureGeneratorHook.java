package de.derfrzocker.ore.control.api;

import de.derfrzocker.feature.api.FeatureGenerator;
import de.derfrzocker.feature.api.FeatureGeneratorConfiguration;
import org.bukkit.Keyed;

public interface FeatureGeneratorHook<C extends FeatureGeneratorConfiguration> extends Keyed {

    FeatureGenerator<C> getFeatureGenerator();

    Biome getBiome();
}
