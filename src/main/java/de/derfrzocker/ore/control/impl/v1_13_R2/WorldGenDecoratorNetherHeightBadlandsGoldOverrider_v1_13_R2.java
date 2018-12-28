package de.derfrzocker.ore.control.impl.v1_13_R2;

import de.derfrzocker.ore.control.OreControl;
import net.minecraft.server.v1_13_R2.*;

import java.util.Random;

public class WorldGenDecoratorNetherHeightBadlandsGoldOverrider_v1_13_R2 extends WorldGenDecoratorNetherHeight {

    @Override
    public <C extends WorldGenFeatureConfiguration> boolean a(GeneratorAccess generatorAccess, ChunkGenerator<? extends GeneratorSettings> chunkGenerator, Random random, BlockPosition blockPosition, WorldGenFeatureChanceDecoratorCountConfiguration worldGenFeatureChanceDecoratorCountConfiguration, WorldGenerator<C> worldGenerator, C c) {
        return OreControl.getService().
                getWorldOreConfig(generatorAccess.getMinecraftWorld().getWorld()).
                map(worldOreConfig -> super.a(generatorAccess, chunkGenerator, random, blockPosition, new WorldGenFeatureChanceDecoratorCountConfiguration(worldOreConfig.getBadlandsGoldSettings().getVeinsPerChunk(), worldOreConfig.getBadlandsGoldSettings().getMinimumHeight(), worldOreConfig.getBadlandsGoldSettings().getHeightSubtractValue(), worldOreConfig.getBadlandsGoldSettings().getHeightRange()), worldGenerator, c)).
                orElseGet(() -> super.a(generatorAccess, chunkGenerator, random, blockPosition, worldGenFeatureChanceDecoratorCountConfiguration, worldGenerator, c));
    }

}
