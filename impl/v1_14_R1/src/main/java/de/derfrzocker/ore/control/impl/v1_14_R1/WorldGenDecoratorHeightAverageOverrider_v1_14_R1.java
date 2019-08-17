package de.derfrzocker.ore.control.impl.v1_14_R1;

import com.mojang.datafixers.Dynamic;
import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.Ore;
import de.derfrzocker.spigot.utils.ChunkCoordIntPair;
import lombok.NonNull;
import net.minecraft.server.v1_14_R1.*;

import java.util.Random;
import java.util.function.Function;

@SuppressWarnings("Duplicates")
public class WorldGenDecoratorHeightAverageOverrider_v1_14_R1 extends WorldGenDecoratorHeightAverage {

    @NonNull
    private final Biome biome;

    public WorldGenDecoratorHeightAverageOverrider_v1_14_R1(final Function<Dynamic<?>, ? extends WorldGenDecoratorHeightAverageConfiguration> dynamicFunction, final Biome biome) {
        super(dynamicFunction);
        this.biome = biome;
    }

    @Override
    public <C extends WorldGenFeatureConfiguration> boolean a(final GeneratorAccess generatorAccess, final ChunkGenerator<? extends GeneratorSettingsDefault> chunkGenerator, final Random random, final BlockPosition blockPosition, final WorldGenDecoratorHeightAverageConfiguration worldGenDecoratorHeightAverageConfiguration, final WorldGenFeatureConfigured<C> worldGenFeatureConfigured) {
        return NMSUtil_v1_14_R1.getOreControlService().getNMSService().generate(generatorAccess.getMinecraftWorld().getWorld(), biome, Ore.LAPIS, new ChunkCoordIntPair(blockPosition.getX() >> 4, blockPosition.getZ() >> 4), worldGenDecoratorHeightAverageConfiguration, worldGenFeatureConfigured,
                null,
                (configuration, featureConfiguration) -> super.a(generatorAccess, chunkGenerator, random, blockPosition, (WorldGenDecoratorHeightAverageConfiguration) configuration, (WorldGenFeatureConfigured<?>) featureConfiguration)
                , random);
    }

}
