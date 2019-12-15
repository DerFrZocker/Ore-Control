package de.derfrzocker.ore.control.impl.v_15_R1;

import com.mojang.datafixers.Dynamic;
import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.Ore;
import de.derfrzocker.ore.control.api.OreControlService;
import de.derfrzocker.spigot.utils.ChunkCoordIntPair;
import lombok.NonNull;
import net.minecraft.server.v1_15_R1.*;

import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("Duplicates")
public class WorldGenDecoratorEmeraldOverrider_v1_15_R1 extends WorldGenDecoratorEmerald {

    @NonNull
    private final Biome biome;

    @NonNull
    private final Supplier<OreControlService> serviceSupplier;

    public WorldGenDecoratorEmeraldOverrider_v1_15_R1(final Function<Dynamic<?>, ? extends WorldGenFeatureEmptyConfiguration2> dynamicFunction, final Biome biome, final Supplier<OreControlService> serviceSupplier) {
        super(dynamicFunction);
        this.biome = biome;
        this.serviceSupplier = serviceSupplier;
    }

    @Override
    public <FC extends WorldGenFeatureConfiguration, F extends WorldGenerator<FC>> boolean a(final GeneratorAccess generatorAccess, final ChunkGenerator<? extends GeneratorSettingsDefault> chunkGenerator, final Random random, final BlockPosition blockPosition, final WorldGenFeatureEmptyConfiguration2 worldGenFeatureDecoratorEmptyConfiguration, final WorldGenFeatureConfigured<FC, F> worldGenFeatureConfigured) {
        return serviceSupplier.get().getNMSService().generate(generatorAccess.getMinecraftWorld().getWorld(), biome, Ore.EMERALD, new ChunkCoordIntPair(blockPosition.getX() >> 4, blockPosition.getZ() >> 4), worldGenFeatureDecoratorEmptyConfiguration, worldGenFeatureConfigured,
                (location, Integer) -> worldGenFeatureConfigured.b.generate(generatorAccess, chunkGenerator, random, new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ()), worldGenFeatureConfigured.c),
                (configuration, featureConfiguration) -> super.a(generatorAccess, chunkGenerator, random, blockPosition, (WorldGenFeatureEmptyConfiguration2) configuration, (WorldGenFeatureConfigured<?, ?>) featureConfiguration)
                , random);
    }

}
