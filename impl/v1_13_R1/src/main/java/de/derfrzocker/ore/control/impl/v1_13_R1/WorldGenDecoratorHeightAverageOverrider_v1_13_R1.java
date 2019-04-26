package de.derfrzocker.ore.control.impl.v1_13_R1;

import de.derfrzocker.ore.control.api.*;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_13_R1.*;
import org.bukkit.Bukkit;

import java.util.Optional;
import java.util.Random;

@RequiredArgsConstructor
public class WorldGenDecoratorHeightAverageOverrider_v1_13_R1 extends WorldGenDecoratorHeightAverage {

    @NonNull
    private final Biome biome;

    @Override
    public <C extends WorldGenFeatureConfiguration> boolean a(final GeneratorAccess generatorAccess, final ChunkGenerator<? extends GeneratorSettings> chunkGenerator, final Random random, final BlockPosition blockPosition, final WorldGenDecoratorHeightAverageConfiguration worldGenDecoratorHeightAverageConfiguration, final WorldGenerator<C> worldGenerator, final C c) {
        final OreControlService service = Bukkit.getServicesManager().load(OreControlService.class);
        final Optional<WorldOreConfig> oreConfig = service.getWorldOreConfig(generatorAccess.getMinecraftWorld().getWorld());

        if (oreConfig.isPresent() && !service.isActivated(Ore.LAPIS, oreConfig.get(), biome))
            return true;

        return oreConfig.
                map(worldOreConfig -> super.a(generatorAccess, chunkGenerator, random, blockPosition, new WorldGenDecoratorHeightAverageConfiguration(
                                service.getValue(Ore.LAPIS, Setting.VEINS_PER_CHUNK, worldOreConfig, biome),
                                service.getValue(Ore.LAPIS, Setting.HEIGHT_CENTER, worldOreConfig, biome),
                                service.getValue(Ore.LAPIS, Setting.HEIGHT_RANGE, worldOreConfig, biome)),
                        worldGenerator, NMSUtil_v1_13_R1.getFeatureConfiguration(oreConfig.get(), Ore.LAPIS, c, biome))).
                orElseGet(() -> super.a(generatorAccess, chunkGenerator, random, blockPosition, worldGenDecoratorHeightAverageConfiguration, worldGenerator, c));
    }
}