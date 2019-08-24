package de.derfrzocker.ore.control.impl.v1_14_R1;

import com.mojang.datafixers.Dynamic;
import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.Ore;
import de.derfrzocker.ore.control.api.OreControlService;
import de.derfrzocker.spigot.utils.ChunkCoordIntPair;
import lombok.NonNull;
import net.minecraft.server.v1_14_R1.*;

import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("Duplicates")
public class WorldGenDecoratorEmeraldOverrider_v1_14_R1 extends WorldGenDecoratorEmerald {

    @NonNull
    private final Biome biome;

    @NonNull
    private final Supplier<OreControlService> serviceSupplier;

    public WorldGenDecoratorEmeraldOverrider_v1_14_R1(final Function<Dynamic<?>, ? extends WorldGenFeatureDecoratorEmptyConfiguration> dynamicFunction, final Biome biome, final Supplier<OreControlService> serviceSupplier) {
        super(dynamicFunction);
        this.biome = biome;
        this.serviceSupplier = serviceSupplier;
    }

    @Override
    public <C extends WorldGenFeatureConfiguration> boolean a(final GeneratorAccess generatorAccess, final ChunkGenerator<? extends GeneratorSettingsDefault> chunkGenerator, final Random random, final BlockPosition blockPosition, final WorldGenFeatureDecoratorEmptyConfiguration worldGenFeatureDecoratorEmptyConfiguration, final WorldGenFeatureConfigured<C> worldGenFeatureConfigured) {
        return serviceSupplier.get().getNMSService().generate(generatorAccess.getMinecraftWorld().getWorld(), biome, Ore.EMERALD, new ChunkCoordIntPair(blockPosition.getX() >> 4, blockPosition.getZ() >> 4), worldGenFeatureDecoratorEmptyConfiguration, worldGenFeatureConfigured,
                (location, Integer) -> worldGenFeatureConfigured.a.generate(generatorAccess, chunkGenerator, random, new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ()), worldGenFeatureConfigured.b),
                (configuration, featureConfiguration) -> super.a(generatorAccess, chunkGenerator, random, blockPosition, (WorldGenFeatureDecoratorEmptyConfiguration) configuration, (WorldGenFeatureConfigured<?>) featureConfiguration)
                , random);
    }

}
