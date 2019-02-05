package de.derfrzocker.ore.control.impl.v1_13_R2;

import de.derfrzocker.ore.control.OreControl;
import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.Ore;
import de.derfrzocker.ore.control.api.WorldOreConfig;
import de.derfrzocker.ore.control.utils.OreControlUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_13_R2.*;

import java.util.Optional;
import java.util.Random;

@SuppressWarnings("Duplicates")
@RequiredArgsConstructor
public class WorldGenDecoratorNetherHeightNormalOverrider_v1_13_R2 extends WorldGenDecoratorNetherHeight {

    @NonNull
    private final Biome biome;

    @Override
    public <C extends WorldGenFeatureConfiguration> boolean a(GeneratorAccess generatorAccess, ChunkGenerator<? extends GeneratorSettings> chunkGenerator, Random random, BlockPosition blockPosition, WorldGenFeatureChanceDecoratorCountConfiguration worldGenFeatureChanceDecoratorCountConfiguration, WorldGenerator<C> worldGenerator, C c) {
        Optional<WorldOreConfig> oreConfig = OreControl.getService().getWorldOreConfig(generatorAccess.getMinecraftWorld().getWorld());

        Ore ore = NMSUtil_v1_13_R2.getOre(((WorldGenFeatureOreConfiguration) c).d.getBlock());

        if (ore != null && oreConfig.isPresent() && !OreControlUtil.isActivated(ore, oreConfig.get(), biome))
            return true;

        return oreConfig.
                map(worldOreConfig -> super.a(generatorAccess, chunkGenerator, random, blockPosition, NMSUtil_v1_13_R2.getCountConfiguration(worldOreConfig, ore, worldGenFeatureChanceDecoratorCountConfiguration, biome), worldGenerator, NMSUtil_v1_13_R2.getFeatureConfiguration(worldOreConfig, ore, c, biome))).
                orElseGet(() -> super.a(generatorAccess, chunkGenerator, random, blockPosition, worldGenFeatureChanceDecoratorCountConfiguration, worldGenerator, c));
    }

}
