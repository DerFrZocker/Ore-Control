package de.derfrzocker.ore.control.impl.v1_13_R2;

import de.derfrzocker.ore.control.OreControl;
import de.derfrzocker.ore.control.api.OreSettings;
import de.derfrzocker.ore.control.api.WorldOreConfig;
import net.minecraft.server.v1_13_R2.*;

import java.util.Random;

@SuppressWarnings("Duplicates")
public class WorldGenDecoratorNetherHeightNormalOverrider_v1_13_R2 extends WorldGenDecoratorNetherHeight {

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

        return new WorldGenFeatureChanceDecoratorCountConfiguration(settings.getVeinsPerChunk(), settings.getMinimumHeight(), settings.getHeightSubtractValue(), settings.getHeightRange());
    }

    private OreSettings getSettings(Block block, WorldOreConfig config) {
        if (block == Blocks.DIAMOND_ORE)
            return config.getDiamondSettings();
        if (block == Blocks.COAL_ORE)
            return config.getCoalSettings();
        if (block == Blocks.IRON_ORE)
            return config.getIronSettings();
        if (block == Blocks.REDSTONE_ORE)
            return config.getRedstoneSettings();
        if (block == Blocks.GOLD_ORE)
            return config.getGoldSettings();

        return null;
    }

}
