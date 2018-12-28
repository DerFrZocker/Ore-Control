package de.derfrzocker.ore.control.impl.v1_13_R2;

import de.derfrzocker.ore.control.OreControl;
import de.derfrzocker.ore.control.api.WorldOreConfig;
import net.minecraft.server.v1_13_R2.*;

import java.util.Random;

@SuppressWarnings("Duplicates")
public class WorldGenMinableNormalOverrider_v1_13_R2 extends WorldGenMinable {

    @Override
    public boolean a(GeneratorAccess generatorAccess, ChunkGenerator<? extends GeneratorSettings> chunkGenerator, Random random, BlockPosition blockPosition, WorldGenFeatureOreConfiguration worldGenFeatureOreConfiguration) {
        return OreControl.getService().
                getWorldOreConfig(generatorAccess.getMinecraftWorld().getWorld()).
                map(worldOreConfig -> super.a(generatorAccess, chunkGenerator, random, blockPosition, new WorldGenFeatureOreConfiguration(WorldGenFeatureOreConfiguration.a, worldGenFeatureOreConfiguration.d, getVault(worldOreConfig, worldGenFeatureOreConfiguration.d.getBlock(), worldGenFeatureOreConfiguration)))).
                orElseGet(() -> super.a(generatorAccess, chunkGenerator, random, blockPosition, worldGenFeatureOreConfiguration));
    }

    private int getVault(WorldOreConfig config, Block block, WorldGenFeatureOreConfiguration worldGen) {
        if (block == Blocks.DIAMOND_ORE)
            return config.getDiamondSettings().getVeinSize();
        if (block == Blocks.COAL_ORE)
            return config.getCoalSettings().getVeinSize();
        if (block == Blocks.IRON_ORE)
            return config.getIronSettings().getVeinSize();
        if (block == Blocks.REDSTONE_ORE)
            return config.getRedstoneSettings().getVeinSize();
        if (block == Blocks.GOLD_ORE)
            return config.getGoldSettings().getVeinSize();
        if (block == Blocks.LAPIS_ORE)
            return config.getLapisSettings().getVeinSize();

        return worldGen.c;
    }

}
