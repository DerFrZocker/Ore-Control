/*
 * MIT License
 *
 * Copyright (c) 2019 - 2021 Marvin (DerFrZocker)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package de.derfrzocker.feature.api;

import com.mojang.serialization.Codec;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.stream.Stream;

/**
 * Represents a placement modifier which generates positions where features should be generated.
 *
 * @param <C> The type if configuration this placement modifier while use.
 */
public interface FeaturePlacementModifier<C extends PlacementModifierConfiguration> extends ConfigurationAble {

    /**
     * Returns the codec which can be used to serialize
     * the configuration used for this placement modifier.
     *
     * @return the codec to save the configuration
     */
    @NotNull
    Codec<PlacementModifierConfiguration> getCodec();

    /**
     * Merges the second configuration into the first one.
     * This means the first configuration will be used primary and
     * the second one will only be used if the first one those not
     * have a setting.
     * <br>
     * While a new configuration is returned the values are not cloned.
     *
     * @param first The main configuration to merge.
     * @param second The second configuration to merge.
     * @return a new configuration with the merged values.
     */
    @NotNull
    C merge(@NotNull PlacementModifierConfiguration first, @NotNull PlacementModifierConfiguration second);

    /**
     * Generates the positions based on the given position,
     * where a feature should be generated.
     * The returned positions may be altered by other placement modifiers.
     * During generation the given random and configuration should be used.
     *
     * @param worldInfo The information about the world.
     * @param random The random which should be used.
     * @param position The position where a feature should generate.
     * @param limitedRegion The LimitedRegion to use to generate the feature
     * @param configuration The configuration to use for this placement modifier.
     * @return a stream of positions where a feature should generate.
     */
    @NotNull
    Stream<BlockVector> getPositions(@NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull BlockVector position, @NotNull LimitedRegion limitedRegion, @NotNull C configuration);
}
