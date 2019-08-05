package de.derfrzocker.ore.control.impl.v1_14_R1;

import com.mojang.datafixers.Dynamic;
import de.derfrzocker.ore.control.api.*;
import lombok.NonNull;
import net.minecraft.server.v1_14_R1.*;
import org.bukkit.Bukkit;

import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

@SuppressWarnings("Duplicates")
public class WorldGenDecoratorNetherHeightNormalOverrider_v1_14_R1 extends WorldGenDecoratorNetherHeight {

    @NonNull
    private final Biome biome;

    private OreControlService service;

    public WorldGenDecoratorNetherHeightNormalOverrider_v1_14_R1(final Function<Dynamic<?>, ? extends WorldGenFeatureChanceDecoratorCountConfiguration> dynamicFunction, final Biome biome) {
        super(dynamicFunction);
        this.biome = biome;
    }

    @Override
    public <C extends WorldGenFeatureConfiguration> boolean a(final GeneratorAccess generatorAccess, final ChunkGenerator<? extends GeneratorSettingsDefault> chunkGenerator, final Random random, final BlockPosition blockPosition, final WorldGenFeatureChanceDecoratorCountConfiguration worldGenFeatureChanceDecoratorCountConfiguration, final WorldGenFeatureConfigured<C> worldGenFeatureConfigured) {
        final OreControlService tempService = Bukkit.getServicesManager().load(OreControlService.class);

        if (service == null && tempService == null)
            throw new NullPointerException("The Bukkit Service has no OreControlService and no OreControlService is cached!");

        if (tempService != null && service != tempService)
            service = tempService;

        final Optional<WorldOreConfig> oreConfig = service.getWorldOreConfig(generatorAccess.getMinecraftWorld().getWorld());

        final Ore ore = NMSUtil_v1_14_R1.getOre(((WorldGenFeatureOreConfiguration) worldGenFeatureConfigured.b).c.getBlock());

        if (ore != null && oreConfig.isPresent() && !service.isActivated(ore, oreConfig.get(), biome))
            return true;

        if (oreConfig.isPresent()) {
            int veinsPerBiome = service.getValue(ore, Setting.VEINS_PER_BIOME, oreConfig.get(), biome);

            if (veinsPerBiome > 0) {
                ChunkCoordIntPair chunkCoordIntPair = new ChunkCoordIntPair(blockPosition.getX(), blockPosition.getZ());
                Set<ChunkCoordIntPair> chunkCoordIntPairs = null;

                try {
                    chunkCoordIntPairs = NMSUtil_v1_14_R1.getChunkCoordIntPair(chunkGenerator.getWorldChunkManager(), blockPosition.getX(), blockPosition.getZ());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ChunkCoordIntPair[] coordIntPairs = chunkCoordIntPairs.toArray(new ChunkCoordIntPair[0]);

                Random random1 = NMSUtil_v1_14_R1.getRandom(chunkGenerator.getSeed(), coordIntPairs[0].x, coordIntPairs[0].z);

                int veinsAmount = 0;

                if (coordIntPairs.length == 1) {
                    veinsAmount = veinsPerBiome;
                } else {
                    for (int i = 0; i < veinsPerBiome; i++) {
                        int randomInt = random1.nextInt((coordIntPairs.length - 1));
                        ChunkCoordIntPair coordIntPair = coordIntPairs[randomInt];
                        if (coordIntPair.equals(chunkCoordIntPair))
                            veinsAmount++;
                    }
                }

                if (veinsAmount == 0)
                    return true;

                return super.a(generatorAccess, chunkGenerator, random, blockPosition, new WorldGenFeatureChanceDecoratorCountConfiguration(
                                veinsAmount,
                                service.getValue(ore, Setting.MINIMUM_HEIGHT, oreConfig.get(), biome),
                                service.getValue(ore, Setting.HEIGHT_SUBTRACT_VALUE, oreConfig.get(), biome),
                                service.getValue(ore, Setting.HEIGHT_RANGE, oreConfig.get(), biome)),
                        new WorldGenFeatureConfigured<>(worldGenFeatureConfigured.a, NMSUtil_v1_14_R1.getFeatureConfiguration(oreConfig.get(), ore, worldGenFeatureConfigured.b, biome)));
            }
        }

        try {
            return oreConfig.
                    map(worldOreConfig -> super.a(generatorAccess, chunkGenerator, random, blockPosition, NMSUtil_v1_14_R1.getCountConfiguration(worldOreConfig, ore, worldGenFeatureChanceDecoratorCountConfiguration, biome),
                            new WorldGenFeatureConfigured<>(worldGenFeatureConfigured.a, NMSUtil_v1_14_R1.getFeatureConfiguration(oreConfig.get(), ore, worldGenFeatureConfigured.b, biome)))).
                    orElseGet(() -> super.a(generatorAccess, chunkGenerator, random, blockPosition, worldGenFeatureChanceDecoratorCountConfiguration, worldGenFeatureConfigured));
        } catch (Exception e) {
            if (ore == null || !oreConfig.isPresent())
                throw e;

            throw new RuntimeException("Error while generate Chunk" +
                    " Name: " + oreConfig.get().getName() +
                    " Ore: " + ore +
                    " Biome: " + biome +
                    " VEIN_SIZE: " + service.getValue(ore, Setting.VEIN_SIZE, oreConfig.get(), biome) +
                    " VEINS_PER_CHUNK: " + service.getValue(ore, Setting.VEINS_PER_CHUNK, oreConfig.get(), biome) +
                    " HEIGHT_RANGE: " + service.getValue(ore, Setting.HEIGHT_RANGE, oreConfig.get(), biome) +
                    " MINIMUM_HEIGHT: " + service.getValue(ore, Setting.MINIMUM_HEIGHT, oreConfig.get(), biome) +
                    " HEIGHT_SUBTRACT_VALUE: " + service.getValue(ore, Setting.HEIGHT_SUBTRACT_VALUE, oreConfig.get(), biome)
                    , e);
        }
    }

}
