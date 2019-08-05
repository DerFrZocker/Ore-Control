package de.derfrzocker.ore.control.impl.v1_13_R2;

import de.derfrzocker.ore.control.api.*;
import net.minecraft.server.v1_13_R2.*;
import org.bukkit.Bukkit;

@SuppressWarnings("Duplicates")
public class NMSUtil_v1_13_R2 {

    private static OreControlService service; //TODO better method

    static OreControlService getOreControlService(){
        final OreControlService tempService = Bukkit.getServicesManager().load(OreControlService.class);

        if (service == null && tempService == null)
            throw new NullPointerException("The Bukkit Service has no OreControlService and no OreControlService is cached!");

        if (tempService != null && service != tempService)
            service = tempService;

        return service;
    }

    static WorldGenFeatureChanceDecoratorCountConfiguration getCountConfiguration(final WorldOreConfig config, final Ore ore, WorldGenFeatureChanceDecoratorCountConfiguration countConfiguration, final Biome biome) {
        if (ore == null)
            return countConfiguration;

        final OreControlService service = getOreControlService();

        return new WorldGenFeatureChanceDecoratorCountConfiguration(
                service.getValue(ore, Setting.VEINS_PER_CHUNK, config, biome),
                service.getValue(ore, Setting.MINIMUM_HEIGHT, config, biome),
                service.getValue(ore, Setting.HEIGHT_SUBTRACT_VALUE, config, biome),
                service.getValue(ore, Setting.HEIGHT_RANGE, config, biome));
    }

    static <C extends WorldGenFeatureConfiguration> C getFeatureConfiguration(final WorldOreConfig config, final Ore ore, final C c, final Biome biome) {
        if (ore == null)
            return c;

        final OreControlService  service = getOreControlService();

        return (C) new WorldGenFeatureOreConfiguration(WorldGenFeatureOreConfiguration.a, ((WorldGenFeatureOreConfiguration) c).d, service.getValue(ore, Setting.VEIN_SIZE, config, biome));
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
