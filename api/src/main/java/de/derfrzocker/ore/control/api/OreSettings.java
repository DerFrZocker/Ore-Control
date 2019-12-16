/*
 * MIT License
 *
 * Copyright (c) 2019 Marvin (DerFrZocker)
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
 */

package de.derfrzocker.ore.control.api;

import java.util.Map;
import java.util.Optional;

/**
 * The OreSettings class holds the different Settings for the Ores,
 * that are needed for the Ore Generation.
 */
public interface OreSettings extends Cloneable {

    /**
     * @return the Ore
     */
    Ore getOre();

    /**
     * If this OreSettings contains the value of the given Setting,
     * it returns an Optional that contains the value,
     * otherwise it return an empty Optional.
     *
     * @param setting which must be non-null
     * @return an Optional that hold the value of the given Setting,
     * or an empty Optional if the OreSetting not contain the given Setting.
     * @throws NullPointerException if setting is null
     */
    Optional<Integer> getValue(Setting setting);

    /**
     * This adds the given Setting with the given value to this OreSettings.
     * If this Object already have a value for the given Setting,
     * than it replaced the old value with given value.
     *
     * @param setting which must be non-null
     * @param value   for the given Setting
     * @throws NullPointerException if setting is null
     */
    void setValue(Setting setting, int value);

    /**
     * @return the  Map with all Settings and values  that this  OreSettings have.
     */
    Map<Setting, Integer> getSettings();

    /**
     * Return if this Ore should be generated or not.
     *
     * @return true if the Ore should generated,
     * false if not.
     */
    boolean isActivated();

    /**
     * Set if the Ore of this OreSetting should be generated or not.
     *
     * @param status true for generate, false for not-generate.
     */
    void setActivated(boolean status);

    /**
     * Clones all Settings and values of the OreSetting to a new one.
     *
     * @return a new OreSettings
     */
    OreSettings clone();

}
