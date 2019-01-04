package de.derfrzocker.ore.control.impl.v1_13_R2;

import de.derfrzocker.ore.control.OreControl;
import de.derfrzocker.ore.control.api.Ore;
import de.derfrzocker.ore.control.api.Setting;
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
            return config.getOreSettings(Ore.DIAMOND).getValue(Setting.VEIN_SIZE).orElse(0);
        if (block == Blocks.COAL_ORE)
            return config.getOreSettings(Ore.COAL).getValue(Setting.VEIN_SIZE).orElse(0);
        if (block == Blocks.IRON_ORE)
            return config.getOreSettings(Ore.IRON).getValue(Setting.VEIN_SIZE).orElse(0);
        if (block == Blocks.REDSTONE_ORE)
            return config.getOreSettings(Ore.REDSTONE).getValue(Setting.VEIN_SIZE).orElse(0);
        if (block == Blocks.GOLD_ORE)
            return config.getOreSettings(Ore.GOLD).getValue(Setting.VEIN_SIZE).orElse(0);
        if (block == Blocks.LAPIS_ORE)
            return config.getOreSettings(Ore.LAPIS).getValue(Setting.VEIN_SIZE).orElse(0);
        if (block == Blocks.DIRT)
            return config.getOreSettings(Ore.DIRT).getValue(Setting.VEIN_SIZE).orElse(0);
        if (block == Blocks.GRAVEL)
            return config.getOreSettings(Ore.GRAVEL).getValue(Setting.VEIN_SIZE).orElse(0);
        if (block == Blocks.GRANITE)
            return config.getOreSettings(Ore.GRANITE).getValue(Setting.VEIN_SIZE).orElse(0);
        if (block == Blocks.DIORITE)
            return config.getOreSettings(Ore.DIORITE).getValue(Setting.VEIN_SIZE).orElse(0);
        if (block == Blocks.ANDESITE)
            return config.getOreSettings(Ore.ANDESITE).getValue(Setting.VEIN_SIZE).orElse(0);

        return worldGen.c;
    }

}
