package de.derfrzocker.ore.control.impl.v1_13_R1;

import de.derfrzocker.ore.control.OreControl;
import net.minecraft.server.v1_13_R1.*;

import java.util.Random;

public class WorldGenMinableBadlandsGoldOverrider_v1_13_R1 extends WorldGenMinable {

    @Override
    public boolean a(GeneratorAccess generatorAccess, ChunkGenerator<? extends GeneratorSettings> chunkGenerator, Random random, BlockPosition blockPosition, WorldGenFeatureOreConfiguration worldGenFeatureOreConfiguration) {
        return OreControl.getService().
                getWorldOreConfig(generatorAccess.getMinecraftWorld().getWorld()).
                map(worldOreConfig -> super.a(generatorAccess, chunkGenerator, random, blockPosition, new WorldGenFeatureOreConfiguration(WorldGenFeatureOreConfiguration.a, worldGenFeatureOreConfiguration.d, worldOreConfig.getBadlandsGoldSettings().getVeinSize()))).
                orElseGet(() -> super.a(generatorAccess, chunkGenerator, random, blockPosition, worldGenFeatureOreConfiguration));
    }

}
