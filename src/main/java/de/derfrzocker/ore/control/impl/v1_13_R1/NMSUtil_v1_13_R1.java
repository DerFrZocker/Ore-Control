package de.derfrzocker.ore.control.impl.v1_13_R1;

import de.derfrzocker.ore.control.api.*;
import de.derfrzocker.ore.control.utils.OreControlUtil;
import net.minecraft.server.v1_13_R1.*;

@SuppressWarnings("Duplicates")
public class NMSUtil_v1_13_R1 {

    static WorldGenFeatureChanceDecoratorCountConfiguration getCountConfiguration(WorldOreConfig config, Ore ore, WorldGenFeatureChanceDecoratorCountConfiguration countConfiguration, Biome biome) {
        if (ore == null)
            return countConfiguration;

        OreSettings settings = OreControlUtil.getOreSettings(ore, config, biome);

        return new WorldGenFeatureChanceDecoratorCountConfiguration(
                settings.getValue(Setting.VEINS_PER_CHUNK).orElse(Setting.VEINS_PER_CHUNK.getMinimumValue()),
                settings.getValue(Setting.MINIMUM_HEIGHT).orElse(Setting.MINIMUM_HEIGHT.getMinimumValue()),
                settings.getValue(Setting.HEIGHT_SUBTRACT_VALUE).orElse(Setting.HEIGHT_SUBTRACT_VALUE.getMinimumValue()),
                settings.getValue(Setting.HEIGHT_RANGE).orElse(Setting.HEIGHT_RANGE.getMinimumValue()));
    }

    static <C extends WorldGenFeatureConfiguration> C getFeatureConfiguration(WorldOreConfig config, Ore ore, C c, Biome biome) {
        if (ore == null)
            return c;

        OreSettings settings = OreControlUtil.getOreSettings(ore, config, biome);

        return (C) new WorldGenFeatureOreConfiguration(WorldGenFeatureOreConfiguration.a, ((WorldGenFeatureOreConfiguration) c).d, settings.getValue(Setting.VEIN_SIZE).orElse(Setting.VEIN_SIZE.getMinimumValue()));
    }

    static Ore getOre(Block block) {
        if (block == Blocks.DIAMOND_ORE)
            return Ore.DIAMOND;
        if (block == Blocks.COAL_ORE)
            return Ore.COAL;
        if (block == Blocks.IRON_ORE)
            return Ore.IRON;
        if (block == Blocks.REDSTONE_ORE)
            return Ore.REDSTONE;
        if (block == Blocks.GOLD_ORE)
            return Ore.GOLD;
        if (block == Blocks.DIRT)
            return Ore.DIRT;
        if (block == Blocks.GRAVEL)
            return Ore.GRAVEL;
        if (block == Blocks.GRANITE)
            return Ore.GRANITE;
        if (block == Blocks.DIORITE)
            return Ore.DIORITE;
        if (block == Blocks.ANDESITE)
            return Ore.ANDESITE;

        return null;
    }

}
