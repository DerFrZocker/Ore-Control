package de.derfrzocker.ore.control.impl.v1_13_R2;

import de.derfrzocker.ore.control.OreControl;
import net.minecraft.server.v1_13_R2.*;

import java.util.Random;

public class WorldGenDecoratorHeightAverageOverrider_v1_13_R2 extends WorldGenDecoratorHeightAverage {

    @Override
    public <C extends WorldGenFeatureConfiguration> boolean a(GeneratorAccess generatorAccess, ChunkGenerator<? extends GeneratorSettings> chunkGenerator, Random random, BlockPosition blockPosition, WorldGenDecoratorHeightAverageConfiguration worldGenDecoratorHeightAverageConfiguration, WorldGenerator<C> worldGenerator, C c) {
        return OreControl.getService().
                getWorldOreConfig(generatorAccess.getMinecraftWorld().getWorld()).
                map(worldOreConfig -> super.a(generatorAccess, chunkGenerator, random, blockPosition, new WorldGenDecoratorHeightAverageConfiguration(worldOreConfig.getLapisSettings().getVeinsPerChunk(), worldOreConfig.getLapisSettings().getHeightCenter(), worldOreConfig.getLapisSettings().getHeightRange()), worldGenerator, c)).
                orElseGet(() -> super.a(generatorAccess, chunkGenerator, random, blockPosition, worldGenDecoratorHeightAverageConfiguration, worldGenerator, c));
    }
}
