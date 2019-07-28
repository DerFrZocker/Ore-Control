package de.derfrzocker.ore.control.impl.v_14_R1;

import com.mojang.datafixers.Dynamic;
import de.derfrzocker.ore.control.api.*;
import lombok.NonNull;
import net.minecraft.server.v1_14_R1.*;
import org.bukkit.Bukkit;

import java.util.Optional;
import java.util.Random;
import java.util.function.Function;

@SuppressWarnings("Duplicates")
public class WorldGenDecoratorNetherHeightNormalOverrider_v1_14_R1 extends WorldGenDecoratorNetherHeight {

    @NonNull
    private final Biome biome;

    public WorldGenDecoratorNetherHeightNormalOverrider_v1_14_R1(final Function<Dynamic<?>, ? extends WorldGenFeatureChanceDecoratorCountConfiguration> dynamicFunction, final Biome biome) {
        super(dynamicFunction);
        this.biome = biome;
    }

    @Override
    public <C extends WorldGenFeatureConfiguration> boolean a(final GeneratorAccess generatorAccess, final ChunkGenerator<? extends GeneratorSettingsDefault> chunkGenerator, final Random random, final BlockPosition blockPosition, final WorldGenFeatureChanceDecoratorCountConfiguration worldGenFeatureChanceDecoratorCountConfiguration, final WorldGenFeatureConfigured<C> worldGenFeatureConfigured) {
        final OreControlService service = Bukkit.getServicesManager().load(OreControlService.class);
        final Optional<WorldOreConfig> oreConfig = service.getWorldOreConfig(generatorAccess.getMinecraftWorld().getWorld());

        final Ore ore = NMSUtil_v1_14_R1.getOre(((WorldGenFeatureOreConfiguration) worldGenFeatureConfigured.b).c.getBlock());

        if (ore != null && oreConfig.isPresent() && !service.isActivated(ore, oreConfig.get(), biome))
            return true;

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
