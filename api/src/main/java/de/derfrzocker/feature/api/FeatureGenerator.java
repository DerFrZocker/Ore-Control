package de.derfrzocker.feature.api;

import de.derfrzocker.feature.api.util.Parser;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

/**
 * Represents a generator which generates blocks in a region.
 *
 * @param <C> The type if configuration this generator while use.
 */
public interface FeatureGenerator<C extends FeatureGeneratorConfiguration> extends ConfigurationAble {

    /**
     * Returns the parser which can be used to serialize
     * the configuration used for this feature generator.
     *
     * @return the parser to save the configuration
     */
    @NotNull
    Parser<FeatureGeneratorConfiguration> getParser();

    /**
     * Merges the second configuration into the first one.
     * This means the first configuration will be used primary and
     * the second one will only be used if the first one those not
     * have a setting.
     * <br>
     * While a new configuration is returned the values are not cloned.
     *
     * @param first  The main configuration to merge.
     * @param second The second configuration to merge.
     * @return a new configuration with the merged values.
     */
    @NotNull
    C merge(@NotNull FeatureGeneratorConfiguration first, @NotNull FeatureGeneratorConfiguration second);

    /**
     * Generates the feature at the given position and region.
     * During generation the given random and configuration should be used.
     *
     * @param worldInfo     The information about the world.
     * @param random        The random which should be used.
     * @param position      The position where the feature should get generated.
     * @param limitedRegion The LimitedRegion to use to generate the feature.
     * @param configuration The configuration to use to generate the feature.
     */
    void place(@NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull BlockVector position, @NotNull LimitedRegion limitedRegion, @NotNull C configuration);
}
