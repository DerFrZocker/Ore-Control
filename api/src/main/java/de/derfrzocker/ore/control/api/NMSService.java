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

package de.derfrzocker.ore.control.api;

import de.derfrzocker.spigot.utils.ChunkCoordIntPair;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;
import java.util.function.BiFunction;

public interface NMSService {

    /**
     * Hooks into Minecraft generation system
     */
    void replaceNMS();

    /**
     * @return the NMSUtil to use
     */
    @NotNull
    NMSUtil getNMSUtil();

    /**
     * Register the given generationHandler to this NMSService
     * If a generationHandler for the given Ore is already registered,
     * it will override the old one with the new one
     *
     * @param ore               to register
     * @param generationHandler to register
     * @throws IllegalArgumentException if ore or generationHandler is null
     */
    void registerGenerationHandler(@NotNull Ore ore, @NotNull GenerationHandler generationHandler);

    /**
     * Handles generation for the feature
     *
     * @param world                       to use
     * @param biome                       to use
     * @param ore                         to use
     * @param chunkCoordIntPair           to use
     * @param defaultConfiguration        to use
     * @param defaultFeatureConfiguration to use
     * @param generateFunction            to use
     * @param passFunction                to use
     * @param random                      to use
     * @return true if generation is success other wise false
     */
    boolean generate(@NotNull World world, @NotNull Biome biome, @NotNull Ore ore, @NotNull ChunkCoordIntPair chunkCoordIntPair,
                     @NotNull Object defaultConfiguration, @NotNull Object defaultFeatureConfiguration,
                     @Nullable BiFunction<Location, Integer, Boolean> generateFunction, @NotNull BiFunction<Object, Object, Boolean> passFunction, @NotNull Random random);

}
