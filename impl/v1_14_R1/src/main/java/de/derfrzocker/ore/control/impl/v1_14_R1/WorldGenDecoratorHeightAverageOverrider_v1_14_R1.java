package de.derfrzocker.ore.control.impl.v1_14_R1;

import com.mojang.datafixers.Dynamic;
import de.derfrzocker.ore.control.api.*;
import lombok.NonNull;
import net.minecraft.server.v1_14_R1.*;
import org.bukkit.Bukkit;

import java.util.Optional;
import java.util.Random;
import java.util.function.Function;

@SuppressWarnings("Duplicates")
public class WorldGenDecoratorHeightAverageOverrider_v1_14_R1 extends WorldGenDecoratorHeightAverage {

    @NonNull
    private final Biome biome;

    private OreControlService service;

    public WorldGenDecoratorHeightAverageOverrider_v1_14_R1(final Function<Dynamic<?>, ? extends WorldGenDecoratorHeightAverageConfiguration> dynamicFunction, final Biome biome) {
        super(dynamicFunction);
        this.biome = biome;
    }

    @Override
    public <C extends WorldGenFeatureConfiguration> boolean a(final GeneratorAccess generatorAccess, final ChunkGenerator<? extends GeneratorSettingsDefault> chunkGenerator, final Random random, final BlockPosition blockPosition, final WorldGenDecoratorHeightAverageConfiguration worldGenDecoratorHeightAverageConfiguration, final WorldGenFeatureConfigured<C> worldGenFeatureConfigured) {
        final OreControlService tempService = Bukkit.getServicesManager().load(OreControlService.class);

        if (service == null && tempService == null)
            throw new NullPointerException("The Bukkit Service has no OreControlService and no OreControlService is cached!");

        if (tempService != null && service != tempService)
            this.service = tempService;

        final Optional<WorldOreConfig> oreConfig = service.getWorldOreConfig(generatorAccess.getMinecraftWorld().getWorld());

        if (oreConfig.isPresent() && !service.isActivated(Ore.LAPIS, oreConfig.get(), biome))
            return true;

        try {
            return oreConfig.map(worldOreConfig -> super.a(generatorAccess, chunkGenerator, random, blockPosition, new WorldGenDecoratorHeightAverageConfiguration(
                    service.getValue(Ore.LAPIS, Setting.VEINS_PER_CHUNK, worldOreConfig, biome),
                    service.getValue(Ore.LAPIS, Setting.HEIGHT_CENTER, worldOreConfig, biome),
                    service.getValue(Ore.LAPIS, Setting.HEIGHT_RANGE, worldOreConfig, biome)), new WorldGenFeatureConfigured<>(worldGenFeatureConfigured.a, NMSUtil_v1_14_R1.getFeatureConfiguration(oreConfig.get(), Ore.LAPIS, worldGenFeatureConfigured.b, biome)))).
                    orElseGet(() -> super.a(generatorAccess, chunkGenerator, random, blockPosition, worldGenDecoratorHeightAverageConfiguration, worldGenFeatureConfigured));
        } catch (Exception e) {
            if (!oreConfig.isPresent())
                throw e;

            throw new RuntimeException("Error while generate Chunk" +
                    " Name: " + oreConfig.get().getName() +
                    " Ore: " + Ore.LAPIS +
                    " Biome: " + biome +
                    " VEINS_PER_CHUNK: " + service.getValue(Ore.LAPIS, Setting.VEINS_PER_CHUNK, oreConfig.get(), biome) +
                    " HEIGHT_CENTER: " + service.getValue(Ore.LAPIS, Setting.HEIGHT_CENTER, oreConfig.get(), biome) +
                    " HEIGHT_RANGE: " + service.getValue(Ore.LAPIS, Setting.HEIGHT_RANGE, oreConfig.get(), biome) +
                    " VEIN_SIZE: " + service.getValue(Ore.LAPIS, Setting.VEIN_SIZE, oreConfig.get(), biome)
                    , e);
        }
    }

}
