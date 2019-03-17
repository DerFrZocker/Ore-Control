package de.derfrzocker.ore.control.impl.v1_13_R1;

import de.derfrzocker.ore.control.OreControl;
import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.Ore;
import de.derfrzocker.ore.control.api.WorldOreConfig;
import de.derfrzocker.ore.control.utils.OreControlUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_13_R1.*;

import java.util.Optional;
import java.util.Random;

@SuppressWarnings("Duplicates")
@RequiredArgsConstructor
public class WorldGenDecoratorNetherHeightBadlandsGoldOverrider_v1_13_R1 extends WorldGenDecoratorNetherHeight {

    @NonNull
    private final Biome biome;

    @Override
    public <C extends WorldGenFeatureConfiguration> boolean a(final GeneratorAccess generatorAccess, final ChunkGenerator<? extends GeneratorSettings> chunkGenerator, final Random random, final BlockPosition blockPosition, final WorldGenFeatureChanceDecoratorCountConfiguration worldGenFeatureChanceDecoratorCountConfiguration, final WorldGenerator<C> worldGenerator, final C c) {
        final Optional<WorldOreConfig> oreConfig = OreControl.getService().getWorldOreConfig(generatorAccess.getMinecraftWorld().getWorld());

        if (oreConfig.isPresent() && !OreControlUtil.isActivated(Ore.GOLD_BADLANDS, oreConfig.get(), biome))
            return true;

        return oreConfig.
                map(worldOreConfig -> super.a(generatorAccess, chunkGenerator, random, blockPosition, NMSUtil_v1_13_R1.getCountConfiguration(worldOreConfig, Ore.GOLD_BADLANDS, worldGenFeatureChanceDecoratorCountConfiguration, biome), worldGenerator,
                        NMSUtil_v1_13_R1.getFeatureConfiguration(oreConfig.get(), Ore.GOLD_BADLANDS, c, biome))).
                orElseGet(() -> super.a(generatorAccess, chunkGenerator, random, blockPosition, worldGenFeatureChanceDecoratorCountConfiguration, worldGenerator, c));
    }

}
