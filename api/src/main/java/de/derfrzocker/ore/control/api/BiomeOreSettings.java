/*
 * MIT License
 *
 * Copyright (c) 2019 - 2020 Marvin (DerFrZocker)
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

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;

/**
 * The BiomeOreSettings class holds the different OreSettings for a specific biome,
 * that are needed for the Ore Generation.
 */
public interface BiomeOreSettings extends Cloneable {

    /**
     * @return the biome
     */
    @NotNull
    Biome getBiome();

    /**
     * If this BiomeOreSettings contains the OreSettings of the given Ore,
     * it returns an Optional that contains the OreSettings,
     * otherwise it return an empty Optional
     *
     * @param ore which must be non-null
     * @return an Optional that hold the OreSettings of the given Ore,
     * or an empty Optional if the BiomeOreSettings not contain the given Ore.
     * @throws IllegalArgumentException if ore is null
     */
    @NotNull
    Optional<OreSettings> getOreSettings(@NotNull Ore ore);

    /**
     * @return the Map with all OreSettings this BiomeOreSetting have.
     */
    @NotNull
    Map<Ore, OreSettings> getOreSettings();

    /**
     * Adds the given OreSetting to this BiomeOreSettings
     * If a OreSettings for the ore already exits in this BiomeOreSetting,
     * the given one will override the old one
     *
     * @param oreSettings to add
     * @throws IllegalArgumentException if oreSettings is null
     */
    void setOreSettings(@NotNull OreSettings oreSettings);

    /**
     * Clones all OreSettings of this BiomeOreSettings to a new one.
     *
     * @return a new BiomeOreSettings
     */
    @NotNull
    BiomeOreSettings clone();

}
