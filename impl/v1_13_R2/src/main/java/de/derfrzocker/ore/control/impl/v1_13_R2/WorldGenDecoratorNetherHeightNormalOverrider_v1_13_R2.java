package de.derfrzocker.ore.control.impl.v1_13_R2;

import de.derfrzocker.ore.control.api.*;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_13_R2.*;
import org.bukkit.Bukkit;

import java.util.Optional;
import java.util.Random;

@SuppressWarnings("Duplicates")
@RequiredArgsConstructor
public class WorldGenDecoratorNetherHeightNormalOverrider_v1_13_R2 extends WorldGenDecoratorNetherHeight {

    @NonNull
    private final Biome biome;

    @Override
    public <C extends WorldGenFeatureConfiguration> boolean a(final GeneratorAccess generatorAccess, final ChunkGenerator<? extends GeneratorSettings> chunkGenerator, final Random random, final BlockPosition blockPosition, final WorldGenFeatureChanceDecoratorCountConfiguration worldGenFeatureChanceDecoratorCountConfiguration, final WorldGenerator<C> worldGenerator, C c) {
        final OreControlService service = Bukkit.getServicesManager().load(OreControlService.class);
        final Optional<WorldOreConfig> oreConfig = service.getWorldOreConfig(generatorAccess.getMinecraftWorld().getWorld());

        final Ore ore = NMSUtil_v1_13_R2.getOre(((WorldGenFeatureOreConfiguration) c).d.getBlock());

        if (ore != null && oreConfig.isPresent() && !service.isActivated(ore, oreConfig.get(), biome))
            return true;

        try {
            return oreConfig.
                    map(worldOreConfig -> super.a(generatorAccess, chunkGenerator, random, blockPosition, NMSUtil_v1_13_R2.getCountConfiguration(worldOreConfig, ore, worldGenFeatureChanceDecoratorCountConfiguration, biome), worldGenerator, NMSUtil_v1_13_R2.getFeatureConfiguration(worldOreConfig, ore, c, biome))).
                    orElseGet(() -> super.a(generatorAccess, chunkGenerator, random, blockPosition, worldGenFeatureChanceDecoratorCountConfiguration, worldGenerator, c));
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
