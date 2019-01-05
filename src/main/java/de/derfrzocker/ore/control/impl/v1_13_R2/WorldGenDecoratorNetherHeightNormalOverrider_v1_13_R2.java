package de.derfrzocker.ore.control.impl.v1_13_R2;

import de.derfrzocker.ore.control.OreControl;
import de.derfrzocker.ore.control.api.*;
import de.derfrzocker.ore.control.utils.OreControlUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_13_R2.*;

import java.util.Random;

@SuppressWarnings("Duplicates")
@RequiredArgsConstructor
public class WorldGenDecoratorNetherHeightNormalOverrider_v1_13_R2 extends WorldGenDecoratorNetherHeight {

    @NonNull
    private final Biome biome;

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
            return OreControlUtil.getOreSettings(Ore.DIAMOND, config, biome);
        if (block == Blocks.COAL_ORE)
            return OreControlUtil.getOreSettings(Ore.COAL, config, biome);
        if (block == Blocks.IRON_ORE)
            return OreControlUtil.getOreSettings(Ore.IRON, config, biome);
        if (block == Blocks.REDSTONE_ORE)
            return OreControlUtil.getOreSettings(Ore.REDSTONE, config, biome);
        if (block == Blocks.GOLD_ORE)
            return OreControlUtil.getOreSettings(Ore.GOLD, config, biome);
        if (block == Blocks.DIRT)
            return OreControlUtil.getOreSettings(Ore.DIRT, config, biome);
        if (block == Blocks.GRAVEL)
            return OreControlUtil.getOreSettings(Ore.GRAVEL, config, biome);
        if (block == Blocks.GRANITE)
            return OreControlUtil.getOreSettings(Ore.GRANITE, config, biome);
        if (block == Blocks.DIORITE)
            return OreControlUtil.getOreSettings(Ore.DIORITE, config, biome);
        if (block == Blocks.ANDESITE)
            return OreControlUtil.getOreSettings(Ore.ANDESITE, config, biome);

        return null;
    }

}
