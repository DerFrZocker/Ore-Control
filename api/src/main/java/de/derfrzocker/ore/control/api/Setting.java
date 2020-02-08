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

public enum Setting {

    VEIN_SIZE(0),
    VEINS_PER_CHUNK(0),
    HEIGHT_RANGE(0),
    HEIGHT_CENTER(0),
    MINIMUM_ORES_PER_CHUNK(-1),
    ORES_PER_CHUNK_RANGE(0),
    MINIMUM_HEIGHT(0),
    HEIGHT_SUBTRACT_VALUE(0),
    VEINS_PER_BIOME(0),
    SEA_LEVEL_DIVIDER(0),
    SEA_LEVEL_ADDER(Integer.MIN_VALUE);

    final static Setting[] DEFAULT_ORE_SETTINGS = new Setting[]{
            VEIN_SIZE
    };

    final static Setting[] DEFAULT_COUNT_RANGE_SETTINGS = new Setting[]{
            VEINS_PER_CHUNK,
            HEIGHT_RANGE,
            MINIMUM_HEIGHT,
            HEIGHT_SUBTRACT_VALUE,
            VEINS_PER_BIOME
    };

    final static Setting[] DEFAULT_COUNT_DEPTH_AVERAGE_SETTINGS = new Setting[]{
            VEINS_PER_CHUNK,
            HEIGHT_RANGE,
            HEIGHT_CENTER,
            VEINS_PER_BIOME
    };

    final static Setting[] DEFAULT_EMERALD_ORE_SETTINGS = new Setting[]{
            MINIMUM_ORES_PER_CHUNK,
            ORES_PER_CHUNK_RANGE,
            HEIGHT_RANGE,
            MINIMUM_HEIGHT,
            VEINS_PER_BIOME
    };

    final static Setting[] DEFAULT_MAGMA_SETTINGS = new Setting[]{
            VEINS_PER_CHUNK,
            HEIGHT_RANGE,
            SEA_LEVEL_DIVIDER,
            SEA_LEVEL_ADDER,
            VEINS_PER_BIOME
    };

    private final int minimumValue;

    Setting(final int minimumValue) {
        this.minimumValue = minimumValue;
    }

    public int getMinimumValue() {
        return minimumValue;
    }

}
