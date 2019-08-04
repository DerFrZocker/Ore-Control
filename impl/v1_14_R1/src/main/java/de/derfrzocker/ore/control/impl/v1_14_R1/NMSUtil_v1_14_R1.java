package de.derfrzocker.ore.control.impl.v1_14_R1;

import de.derfrzocker.ore.control.api.*;
import net.minecraft.server.v1_14_R1.*;
import org.bukkit.Bukkit;

@SuppressWarnings("Duplicates")
public class NMSUtil_v1_14_R1 {

    static WorldGenFeatureChanceDecoratorCountConfiguration getCountConfiguration(final WorldOreConfig config, final Ore ore, WorldGenFeatureChanceDecoratorCountConfiguration countConfiguration, final Biome biome) {
        if (ore == null)
            return countConfiguration;

        final OreControlService service = Bukkit.getServicesManager().load(OreControlService.class);

        return new WorldGenFeatureChanceDecoratorCountConfiguration(
                service.getValue(ore, Setting.VEINS_PER_CHUNK, config, biome),
                service.getValue(ore, Setting.MINIMUM_HEIGHT, config, biome),
                service.getValue(ore, Setting.HEIGHT_SUBTRACT_VALUE, config, biome),
                service.getValue(ore, Setting.HEIGHT_RANGE, config, biome));
    }

    static <C extends WorldGenFeatureConfiguration> C getFeatureConfiguration(final WorldOreConfig config, final Ore ore, final C c, final Biome biome) {
        if (ore == null)
            return c;

        final OreControlService service = Bukkit.getServicesManager().load(OreControlService.class);

        return (C) new WorldGenFeatureOreConfiguration(WorldGenFeatureOreConfiguration.Target.NATURAL_STONE, ((WorldGenFeatureOreConfiguration) c).c, service.getValue(ore, Setting.VEIN_SIZE, config, biome));
    }

    static Ore getOre(final Block block) {
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
