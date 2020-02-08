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

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public enum Ore {

    DIAMOND(Material.DIAMOND_ORE, Setting.DEFAULT_ORE_SETTINGS, Setting.DEFAULT_COUNT_RANGE_SETTINGS),
    COAL(Material.COAL_ORE, Setting.DEFAULT_ORE_SETTINGS, Setting.DEFAULT_COUNT_RANGE_SETTINGS),
    GOLD(Material.GOLD_ORE, Setting.DEFAULT_ORE_SETTINGS, Setting.DEFAULT_COUNT_RANGE_SETTINGS),
    GOLD_BADLANDS(Material.GOLD_ORE, Setting.DEFAULT_ORE_SETTINGS, Setting.DEFAULT_COUNT_RANGE_SETTINGS),
    LAPIS(Material.LAPIS_ORE, Setting.DEFAULT_ORE_SETTINGS, Setting.DEFAULT_COUNT_DEPTH_AVERAGE_SETTINGS),
    IRON(Material.IRON_ORE, Setting.DEFAULT_ORE_SETTINGS, Setting.DEFAULT_COUNT_RANGE_SETTINGS),
    REDSTONE(Material.REDSTONE_ORE, Setting.DEFAULT_ORE_SETTINGS, Setting.DEFAULT_COUNT_RANGE_SETTINGS),
    EMERALD(Material.EMERALD_ORE, Setting.DEFAULT_EMERALD_ORE_SETTINGS),
    DIRT(Material.DIRT, Setting.DEFAULT_ORE_SETTINGS, Setting.DEFAULT_COUNT_RANGE_SETTINGS),
    GRAVEL(Material.GRAVEL, Setting.DEFAULT_ORE_SETTINGS, Setting.DEFAULT_COUNT_RANGE_SETTINGS),
    GRANITE(Material.GRANITE, Setting.DEFAULT_ORE_SETTINGS, Setting.DEFAULT_COUNT_RANGE_SETTINGS),
    DIORITE(Material.DIORITE, Setting.DEFAULT_ORE_SETTINGS, Setting.DEFAULT_COUNT_RANGE_SETTINGS),
    ANDESITE(Material.ANDESITE, Setting.DEFAULT_ORE_SETTINGS, Setting.DEFAULT_COUNT_RANGE_SETTINGS),
    NETHER_QUARTZ(Material.NETHER_QUARTZ_ORE, Setting.DEFAULT_ORE_SETTINGS, Setting.DEFAULT_COUNT_RANGE_SETTINGS),
    INFESTED_STONE(Material.INFESTED_STONE, Setting.DEFAULT_ORE_SETTINGS, Setting.DEFAULT_COUNT_RANGE_SETTINGS),
    MAGMA(Material.MAGMA_BLOCK, Setting.DEFAULT_ORE_SETTINGS, Setting.DEFAULT_MAGMA_SETTINGS);

    final static Ore[] DEFAULT_OVERWORLD_ORES = new Ore[]{
            ANDESITE,
            COAL,
            DIAMOND,
            DIORITE,
            DIRT,
            GOLD,
            GRANITE,
            GRAVEL,
            IRON,
            LAPIS,
            REDSTONE
    };

    final static Ore[] DEFAULT_NETHER_ORES = new Ore[]{
            NETHER_QUARTZ,
            MAGMA
    };

    @NotNull
    private final Material material;

    @NotNull
    private final Setting[] settings;

    Ore(@NotNull final Material material, @NotNull final Setting[] settings, @NotNull final Setting... settings1) {
        this.material = material;

        final int firstLength = settings.length;
        final int secondLength = settings1.length;
        final Setting[] result = new Setting[firstLength + secondLength];

        System.arraycopy(settings, 0, result, 0, firstLength);
        System.arraycopy(settings1, 0, result, firstLength, secondLength);

        this.settings = result;
    }

    @NotNull
    public Setting[] getSettings() {
        return settings.clone();
    }

    @NotNull
    public Material getMaterial() {
        return material;
    }

}
