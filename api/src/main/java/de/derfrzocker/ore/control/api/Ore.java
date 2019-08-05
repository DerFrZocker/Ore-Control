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
    ANDESITE(Material.ANDESITE);

    private final Material material;

    public Setting[] getSettings() {
        if (this == LAPIS)
            return new Setting[]{Setting.VEIN_SIZE, Setting.VEINS_PER_CHUNK, Setting.HEIGHT_RANGE, Setting.HEIGHT_CENTER, Setting.VEINS_PER_BIOME};

        if (this == EMERALD)
            return new Setting[]{Setting.MINIMUM_ORES_PER_CHUNK, Setting.ORES_PER_CHUNK_RANGE, Setting.HEIGHT_RANGE, Setting.MINIMUM_HEIGHT, Setting.VEINS_PER_BIOME};

        return new Setting[]{Setting.VEIN_SIZE, Setting.VEINS_PER_CHUNK, Setting.MINIMUM_HEIGHT, Setting.HEIGHT_RANGE, Setting.HEIGHT_SUBTRACT_VALUE, Setting.VEINS_PER_BIOME};
    }

}
