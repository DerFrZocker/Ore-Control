package de.derfrzocker.ore.control.impl.v1_13_R1;

import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.Ore;
import de.derfrzocker.ore.control.api.OreControlService;
import de.derfrzocker.spigot.utils.ChunkCoordIntPair;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_13_R1.*;

import java.util.Random;
import java.util.function.Supplier;

@SuppressWarnings("Duplicates")
@RequiredArgsConstructor
public class WorldGenDecoratorHeightAverageOverrider_v1_13_R1 extends WorldGenDecoratorHeightAverage {

    @NonNull
    private final Biome biome;

    @NonNull
    private final Supplier<OreControlService> serviceSupplier;

    @Override
    public <C extends WorldGenFeatureConfiguration> boolean a(final GeneratorAccess generatorAccess, final ChunkGenerator<? extends GeneratorSettings> chunkGenerator, final Random random, final BlockPosition blockPosition, final WorldGenDecoratorHeightAverageConfiguration worldGenDecoratorHeightAverageConfiguration, final WorldGenerator<C> worldGenerator, final C worldGenFeatureConfigured) {
        return serviceSupplier.get().getNMSService().generate(generatorAccess.getMinecraftWorld().getWorld(), biome, Ore.LAPIS, new ChunkCoordIntPair(blockPosition.getX() >> 4, blockPosition.getZ() >> 4), worldGenDecoratorHeightAverageConfiguration, worldGenFeatureConfigured,
                null,
                (configuration, featureConfiguration) -> super.a(generatorAccess, chunkGenerator, random, blockPosition, (WorldGenDecoratorHeightAverageConfiguration) configuration, worldGenerator, (C) featureConfiguration)
                , random);
    }
}
