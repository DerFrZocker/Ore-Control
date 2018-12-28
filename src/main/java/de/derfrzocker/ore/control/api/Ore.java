package de.derfrzocker.ore.control.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;

@RequiredArgsConstructor
@Getter
public enum Ore {

    DIAMOND(Material.DIAMOND_ORE), COAL(Material.COAL_ORE), GOLD(Material.GOLD_ORE), GOLD_BADLANDS(Material.GOLD_ORE), LAPIS(Material.LAPIS_ORE), IRON(Material.IRON_ORE), REDSTONE(Material.REDSTONE_ORE), EMERALD(Material.EMERALD_ORE);

    private final Material material;

    public String[] getSettings() {
        if (this == LAPIS)
            return new String[]{"vein_size", "veins_per_chunk", "height_range", "height_center"};

        if (this == EMERALD)
            return new String[]{"minimum_ores_per_chunk", "ores_per_chunk_range", "height_range", "minimum_height"};

        return new String[]{"vein_size", "veins_per_chunk", "minimum_height", "height_range", "height_subtract_value"};
    }

}
