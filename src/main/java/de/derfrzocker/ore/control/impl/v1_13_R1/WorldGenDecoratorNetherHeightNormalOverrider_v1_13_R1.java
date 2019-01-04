package de.derfrzocker.ore.control.impl.v1_13_R1;

import de.derfrzocker.ore.control.OreControl;
import de.derfrzocker.ore.control.api.Ore;
import de.derfrzocker.ore.control.api.OreSettings;
import de.derfrzocker.ore.control.api.Setting;
import de.derfrzocker.ore.control.api.WorldOreConfig;
import net.minecraft.server.v1_13_R1.*;

import java.util.Random;

@SuppressWarnings("Duplicates")
public class WorldGenDecoratorNetherHeightNormalOverrider_v1_13_R1 extends WorldGenDecoratorNetherHeight {

    @Override
    public <C extends WorldGenFeatureConfiguration> boolean a(GeneratorAccess generatorAccess, ChunkGenerator<? extends GeneratorSettings> chunkGenerator, Random random, BlockPosition blockPosition, WorldGenFeatureChanceDecoratorCountConfiguration worldGenFeatureChanceDecoratorCountConfiguration, WorldGenerator<C> worldGenerator, C c) {
        return OreControl.getService().
                getWorldOreConfig(generatorAccess.getMinecraftWorld().getWorld()).
                map(worldOreConfig -> super.a(generatorAccess, chunkGenerator, random, blockPosition, getCountConfiguration(worldOreConfig, ((WorldGenFeatureOreConfiguration) c).d.getBlock(), worldGenFeatureChanceDecoratorCountConfiguration), worldGenerator, c)).
                orElseGet(() -> super.a(generatorAccess, chunkGenerator, random, blockPosition, worldGenFeatureChanceDecoratorCountConfiguration, worldGenerator, c));
    }

    private WorldGenFeatureChanceDecoratorCountConfiguration getCountConfiguration(WorldOreConfig config, Block block, WorldGenFeatureChanceDecoratorCountConfiguration countConfiguration) {
        OreSettings settings = getSettings(block, config);

        if (settings == null)
            return countConfiguration;

        return new WorldGenFeatureChanceDecoratorCountConfiguration(settings.getValue(Setting.VEINS_PER_CHUNK).orElse(0), settings.getValue(Setting.MINIMUM_HEIGHT).orElse(0), settings.getValue(Setting.HEIGHT_SUBTRACT_VALUE).orElse(0), settings.getValue(Setting.HEIGHT_RANGE).orElse(0));
    }

    private OreSettings getSettings(Block block, WorldOreConfig config) {
        if (block == Blocks.DIAMOND_ORE)
            return config.getOreSettings(Ore.DIAMOND);
        if (block == Blocks.COAL_ORE)
            return config.getOreSettings(Ore.COAL);
        if (block == Blocks.IRON_ORE)
            return config.getOreSettings(Ore.IRON);
        if (block == Blocks.REDSTONE_ORE)
            return config.getOreSettings(Ore.REDSTONE);
        if (block == Blocks.GOLD_ORE)
            return config.getOreSettings(Ore.GOLD);
        if (block == Blocks.DIRT)
            return config.getOreSettings(Ore.DIRT);
        if (block == Blocks.GRAVEL)
            return config.getOreSettings(Ore.GRAVEL);
        if (block == Blocks.GRANITE)
            return config.getOreSettings(Ore.GRANITE);
        if (block == Blocks.DIORITE)
            return config.getOreSettings(Ore.DIORITE);
        if (block == Blocks.ANDESITE)
            return config.getOreSettings(Ore.ANDESITE);

        return null;
    }

}
