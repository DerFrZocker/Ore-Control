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
public class WorldGenDecoratorEmeraldOverrider_v1_14_R1 extends WorldGenDecoratorEmerald {

    @NonNull
    private final Biome biome;

    private OreControlService service;

    public WorldGenDecoratorEmeraldOverrider_v1_14_R1(final Function<Dynamic<?>, ? extends WorldGenFeatureDecoratorEmptyConfiguration> dynamicFunction, final Biome biome) {
        super(dynamicFunction);
        this.biome = biome;
    }

    @Override
    public <C extends WorldGenFeatureConfiguration> boolean a(final GeneratorAccess generatorAccess, final ChunkGenerator<? extends GeneratorSettingsDefault> chunkGenerator, final Random random, final BlockPosition blockPosition, final WorldGenFeatureDecoratorEmptyConfiguration worldGenFeatureDecoratorEmptyConfiguration, final WorldGenFeatureConfigured<C> worldGenFeatureConfigured) {
        final OreControlService tempService = Bukkit.getServicesManager().load(OreControlService.class);

        if (service == null && tempService == null)
            throw new NullPointerException("The Bukkit Service has no OreControlService and no OreControlService is cached!");

        if (tempService != null && service != tempService)
            this.service = tempService;

        final Optional<WorldOreConfig> oreConfig = service.getWorldOreConfig(generatorAccess.getMinecraftWorld().getWorld());

        int var1 = 3, var2 = 6, var3 = 28, var4 = 4;

        if (oreConfig.isPresent()) {
            if (!service.isActivated(Ore.EMERALD, oreConfig.get(), biome))
                return true;

            var1 = service.getValue(Ore.EMERALD, Setting.MINIMUM_ORES_PER_CHUNK, oreConfig.get(), biome);
            var2 = service.getValue(Ore.EMERALD, Setting.ORES_PER_CHUNK_RANGE, oreConfig.get(), biome);
            var3 = service.getValue(Ore.EMERALD, Setting.HEIGHT_RANGE, oreConfig.get(), biome);
            var4 = service.getValue(Ore.EMERALD, Setting.MINIMUM_HEIGHT, oreConfig.get(), biome);
        }

        try {
            final int var5 = var1 + random.nextInt(var2);

            for (int var6 = 0; var6 < var5; ++var6) {
                final int var7 = random.nextInt(16);
                final int var8 = random.nextInt(var3) + var4;
                final int var9 = random.nextInt(16);
                worldGenFeatureConfigured.a.generate(generatorAccess, chunkGenerator, random, blockPosition.b(var7, var8, var9), worldGenFeatureConfigured.b);
            }

            return true;
        } catch (Exception e) {
            if (!oreConfig.isPresent())
                throw e;

            throw new RuntimeException("Error while generate Chunk" +
                    " Name: " + oreConfig.get().getName() +
                    " Ore: " + Ore.EMERALD +
                    " Biome: " + biome +
                    " MINIMUM_HEIGHT: " + service.getValue(Ore.EMERALD, Setting.MINIMUM_HEIGHT, oreConfig.get(), biome) +
                    " HEIGHT_RANGE: " + service.getValue(Ore.EMERALD, Setting.HEIGHT_RANGE, oreConfig.get(), biome) +
                    " ORES_PER_CHUNK_RANGE: " + service.getValue(Ore.EMERALD, Setting.ORES_PER_CHUNK_RANGE, oreConfig.get(), biome) +
                    " MINIMUM_ORES_PER_CHUNK: " + service.getValue(Ore.EMERALD, Setting.MINIMUM_ORES_PER_CHUNK, oreConfig.get(), biome)
                    , e);
        }
    }
}