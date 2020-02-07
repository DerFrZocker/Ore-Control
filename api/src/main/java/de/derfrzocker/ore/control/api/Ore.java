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

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;

@RequiredArgsConstructor
@Getter
public enum Ore {

    DIAMOND(Material.DIAMOND_ORE),
    COAL(Material.COAL_ORE),
    GOLD(Material.GOLD_ORE),
    GOLD_BADLANDS(Material.GOLD_ORE),
    LAPIS(Material.LAPIS_ORE),
    IRON(Material.IRON_ORE),
    REDSTONE(Material.REDSTONE_ORE),
    EMERALD(Material.EMERALD_ORE),
    DIRT(Material.DIRT),
    GRAVEL(Material.GRAVEL),
    GRANITE(Material.GRANITE),
    DIORITE(Material.DIORITE),
    ANDESITE(Material.ANDESITE),
    NETHER_QUARTZ(Material.NETHER_QUARTZ_ORE),
    INFESTED_STONE(Material.INFESTED_STONE);

    final static Ore[] DEFAULT_OVERWORLD_ORES = new Ore[]{
            Ore.ANDESITE,
            Ore.COAL,
            Ore.DIAMOND,
            Ore.DIORITE,
            Ore.DIRT,
            Ore.GOLD,
            Ore.GRANITE,
            Ore.GRAVEL,
            Ore.IRON,
            Ore.LAPIS,
            Ore.REDSTONE
    };

    final static Ore[] DEFAULT_NETHER_ORES = new Ore[]{
            Ore.NETHER_QUARTZ
    };

    private final Material material;

    public Setting[] getSettings() {
        if (this == LAPIS)
            return new Setting[]{Setting.VEIN_SIZE, Setting.VEINS_PER_CHUNK, Setting.HEIGHT_RANGE, Setting.HEIGHT_CENTER, Setting.VEINS_PER_BIOME};

        if (this == EMERALD)
            return new Setting[]{Setting.MINIMUM_ORES_PER_CHUNK, Setting.ORES_PER_CHUNK_RANGE, Setting.HEIGHT_RANGE, Setting.MINIMUM_HEIGHT, Setting.VEINS_PER_BIOME};

        return new Setting[]{Setting.VEIN_SIZE, Setting.VEINS_PER_CHUNK, Setting.MINIMUM_HEIGHT, Setting.HEIGHT_RANGE, Setting.HEIGHT_SUBTRACT_VALUE, Setting.VEINS_PER_BIOME};
    }

}
