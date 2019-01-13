package de.derfrzocker.ore.control.api;

import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
public enum Biome {

    OCEAN,
    PLAINS,
    DESERT,
    MOUNTAINS(Ore.EMERALD),
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
    MOUNTAIN_EDGE(Ore.EMERALD),
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
    WOODED_MOUNTAINS(Ore.EMERALD),
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
    GRAVELLY_MOUNTAINS(Ore.EMERALD),
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
    MODIFIED_GRAVELLY_MOUNTAINS(Ore.EMERALD),
    SHATTERED_SAVANNA,
    SHATTERED_SAVANNA_PLATEAU,
    ERODED_BADLANDS(Ore.GOLD_BADLANDS),
    MODIFIED_WOODED_BADLANDS_PLATEAU(Ore.GOLD_BADLANDS),
    MODIFIED_BADLANDS_PLATEAU(Ore.GOLD_BADLANDS);

    private Ore ore = null;

    public Ore[] getOres() {
        Set<Ore> ores = Sets.newHashSet(Ore.values());

        if (ore == null) {
            ores.remove(Ore.GOLD_BADLANDS);
            ores.remove(Ore.EMERALD);
            return ores.toArray(new Ore[0]);
        }

        ores.remove(ore == Ore.GOLD_BADLANDS ? Ore.GOLD_BADLANDS : Ore.EMERALD);

        return ores.toArray(new Ore[0]);
    }

}
