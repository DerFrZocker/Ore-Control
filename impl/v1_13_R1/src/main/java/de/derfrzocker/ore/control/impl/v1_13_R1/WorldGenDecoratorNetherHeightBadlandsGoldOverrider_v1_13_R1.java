package de.derfrzocker.ore.control.impl.v1_13_R1;

import de.derfrzocker.ore.control.api.*;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_13_R1.*;
import org.bukkit.Bukkit;

import java.util.Optional;
import java.util.Random;

@SuppressWarnings("Duplicates")
@RequiredArgsConstructor
public class WorldGenDecoratorNetherHeightBadlandsGoldOverrider_v1_13_R1 extends WorldGenDecoratorNetherHeight {

    @NonNull
    private final Biome biome;

    private OreControlService service;

    @Override
    public <C extends WorldGenFeatureConfiguration> boolean a(final GeneratorAccess generatorAccess, final ChunkGenerator<? extends GeneratorSettings> chunkGenerator, final Random random, final BlockPosition blockPosition, final WorldGenFeatureChanceDecoratorCountConfiguration worldGenFeatureChanceDecoratorCountConfiguration, final WorldGenerator<C> worldGenerator, final C c) {
        final OreControlService tempService = Bukkit.getServicesManager().load(OreControlService.class);

        if (service == null && tempService == null)
            throw new NullPointerException("The Bukkit Service has no OreControlService and no OreControlService is cached!");

        if (tempService != null && service != tempService)
            service = tempService;

        final Optional<WorldOreConfig> oreConfig = service.getWorldOreConfig(generatorAccess.getMinecraftWorld().getWorld());

        if (oreConfig.isPresent() && !service.isActivated(Ore.GOLD_BADLANDS, oreConfig.get(), biome))
            return true;

        try {

            return oreConfig.
                    map(worldOreConfig -> super.a(generatorAccess, chunkGenerator, random, blockPosition, NMSUtil_v1_13_R1.getCountConfiguration(worldOreConfig, Ore.GOLD_BADLANDS, worldGenFeatureChanceDecoratorCountConfiguration, biome), worldGenerator,
                            NMSUtil_v1_13_R1.getFeatureConfiguration(oreConfig.get(), Ore.GOLD_BADLANDS, c, biome))).
                    orElseGet(() -> super.a(generatorAccess, chunkGenerator, random, blockPosition, worldGenFeatureChanceDecoratorCountConfiguration, worldGenerator, c));
        } catch (Exception e) {
            if (!oreConfig.isPresent())
                throw e;

            throw new RuntimeException("Error while generate Chunk" +
                    " Name: " + oreConfig.get().getName() +
                    " Ore: " + Ore.GOLD_BADLANDS +
                    " Biome: " + biome +
                    " VEIN_SIZE: " + service.getValue(Ore.GOLD_BADLANDS, Setting.VEIN_SIZE, oreConfig.get(), biome) +
                    " VEINS_PER_CHUNK: " + service.getValue(Ore.GOLD_BADLANDS, Setting.VEINS_PER_CHUNK, oreConfig.get(), biome) +
                    " HEIGHT_RANGE: " + service.getValue(Ore.GOLD_BADLANDS, Setting.HEIGHT_RANGE, oreConfig.get(), biome) +
                    " MINIMUM_HEIGHT: " + service.getValue(Ore.GOLD_BADLANDS, Setting.MINIMUM_HEIGHT, oreConfig.get(), biome) +
                    " HEIGHT_SUBTRACT_VALUE: " + service.getValue(Ore.GOLD_BADLANDS, Setting.HEIGHT_SUBTRACT_VALUE, oreConfig.get(), biome)
                    , e);
        }
    }

}
