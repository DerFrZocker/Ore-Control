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

import de.derfrzocker.spigot.utils.Version;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum Biome {

    OCEAN,
    PLAINS,
    DESERT,
    MOUNTAINS(Ore.EMERALD, Ore.INFESTED_STONE),
    FOREST,
    TAIGA,
    SWAMP,
    RIVER,
    FROZEN_OCEAN,
    FROZEN_RIVER,
    SNOWY_TUNDRA,
    SNOWY_MOUNTAINS,
    MUSHROOM_FIELDS,
    MUSHROOM_FIELD_SHORE,
    BEACH,
    DESERT_HILLS,
    WOODED_HILLS,
    TAIGA_HILLS,
    MOUNTAIN_EDGE(Ore.EMERALD, Ore.INFESTED_STONE),
    JUNGLE,
    JUNGLE_HILLS,
    JUNGLE_EDGE,
    DEEP_OCEAN,
    STONE_SHORE,
    SNOWY_BEACH,
    BIRCH_FOREST,
    BIRCH_FOREST_HILLS,
    DARK_FOREST,
    SNOWY_TAIGA,
    SNOWY_TAIGA_HILLS,
    GIANT_TREE_TAIGA,
    GIANT_TREE_TAIGA_HILLS,
    WOODED_MOUNTAINS(Ore.EMERALD, Ore.INFESTED_STONE),
    SAVANNA,
    SAVANNA_PLATEAU,
    BADLANDS(Ore.GOLD_BADLANDS),
    WOODED_BADLANDS_PLATEAU(Ore.GOLD_BADLANDS),
    BADLANDS_PLATEAU(Ore.GOLD_BADLANDS),
    WARM_OCEAN,
    LUKEWARM_OCEAN,
    COLD_OCEAN,
    DEEP_WARM_OCEAN,
    DEEP_LUKEWARM_OCEAN,
    DEEP_COLD_OCEAN,
    DEEP_FROZEN_OCEAN,
    SUNFLOWER_PLAINS,
    DESERT_LAKES,
    GRAVELLY_MOUNTAINS(Ore.EMERALD, Ore.INFESTED_STONE),
    FLOWER_FOREST,
    TAIGA_MOUNTAINS,
    SWAMP_HILLS,
    ICE_SPIKES,
    MODIFIED_JUNGLE,
    MODIFIED_JUNGLE_EDGE,
    TALL_BIRCH_FOREST,
    TALL_BIRCH_HILLS,
    DARK_FOREST_HILLS,
    SNOWY_TAIGA_MOUNTAINS,
    GIANT_SPRUCE_TAIGA,
    GIANT_SPRUCE_TAIGA_HILLS,
    MODIFIED_GRAVELLY_MOUNTAINS(Ore.EMERALD, Ore.INFESTED_STONE),
    SHATTERED_SAVANNA,
    SHATTERED_SAVANNA_PLATEAU,
    ERODED_BADLANDS(Ore.GOLD_BADLANDS),
    MODIFIED_WOODED_BADLANDS_PLATEAU(Ore.GOLD_BADLANDS),
    MODIFIED_BADLANDS_PLATEAU(Ore.GOLD_BADLANDS),
    BAMBOO_JUNGLE(Version.v1_14_R1),
    BAMBOO_JUNGLE_HILLS(Version.v1_14_R1),
    NETHER(Version.v1_13_R1, Version.v1_15_R1, Ore.DEFAULT_NETHER_ORES, Dimension.NETHER),
    NETHER_WASTES(Version.v1_16_R1, null, Ore.DEFAULT_NETHER_ORES, Dimension.NETHER),
    SOUL_SAND_VALLEY(Version.v1_16_R1, null, Ore.DEFAULT_NETHER_ORES, Dimension.NETHER),
    CRIMSON_FOREST(Version.v1_16_R1, null, Ore.DEFAULT_NETHER_ORES, Dimension.NETHER),
    WARPED_FOREST(Version.v1_16_R1, null, Ore.DEFAULT_NETHER_ORES, Dimension.NETHER),
    BASALT_DELTAS(Version.v1_16_R1, null, Ore.DEFAULT_NETHER_ORES, Dimension.NETHER);

    @NotNull
    private final Version since;
    @Nullable
    private final Version until;
    @NotNull
    private final Ore[] ores;
    @NotNull
    private final Dimension dimension;

    Biome() {
        this(Version.v1_13_R1, null, Ore.DEFAULT_OVERWORLD_ORES, Dimension.OVERWORLD);
    }


    Biome(@NotNull final Ore... ores) {
        this(Version.v1_13_R1, null, combineOres(Ore.DEFAULT_OVERWORLD_ORES, ores), Dimension.OVERWORLD);
    }

    Biome(@NotNull final Version since) {
        this(since, null, Ore.DEFAULT_OVERWORLD_ORES, Dimension.OVERWORLD);
    }

    Biome(@NotNull final Version since, @Nullable final Version until, @NotNull final Ore[] ores, @NotNull final Dimension dimension) {
        this.since = since;
        this.until = until;
        this.ores = ores;
        this.dimension = dimension;
    }

    private static Ore[] combineOres(@NotNull final Ore[] ores, @NotNull final Ore... ores1) {
        final int firstLength = ores.length;
        final int secondLength = ores1.length;
        final Ore[] result = new Ore[firstLength + secondLength];

        System.arraycopy(ores, 0, result, 0, firstLength);
        System.arraycopy(ores1, 0, result, firstLength, secondLength);

        return result;
    }

    @NotNull
    public Version getSince() {
        return this.since;
    }

    @Nullable
    public Version getUntil() {
        return this.until;
    }

    @NotNull
    public Ore[] getOres() {
        return this.ores.clone();
    }

    @NotNull
    public Dimension getDimension() {
        return this.dimension;
    }

}
