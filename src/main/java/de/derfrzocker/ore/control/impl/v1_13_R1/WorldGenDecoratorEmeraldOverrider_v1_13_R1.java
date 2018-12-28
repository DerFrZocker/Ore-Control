package de.derfrzocker.ore.control.impl.v1_13_R1;

import de.derfrzocker.ore.control.OreControl;
import de.derfrzocker.ore.control.api.WorldOreConfig;
import net.minecraft.server.v1_13_R1.*;

import java.util.Optional;
import java.util.Random;

@SuppressWarnings("Duplicates")
public class WorldGenDecoratorEmeraldOverrider_v1_13_R1 extends WorldGenDecoratorEmerald {

    @Override
    public <C extends WorldGenFeatureConfiguration> boolean a(GeneratorAccess generatorAccess, ChunkGenerator<? extends GeneratorSettings> chunkGenerator, Random random, BlockPosition blockPosition, WorldGenFeatureDecoratorEmptyConfiguration worldGenFeatureDecoratorEmptyConfiguration, WorldGenerator<C> worldGenerator, C c) {
        Optional<WorldOreConfig> config = OreControl.getService().getWorldOreConfig(generatorAccess.getMinecraftWorld().getWorld());

        int var1 = 3, var2 = 6, var3 = 28, var4 = 4;

        if (config.isPresent()) {
            var1 = config.get().getEmeraldSettings().getMinimumOresPerChunk();
            var2 = config.get().getEmeraldSettings().getOresPerChunkRange();
            var3 = config.get().getEmeraldSettings().getHeightRange();
            var4 = config.get().getEmeraldSettings().getMinimumHeight();
        }

        int var5 = var1 + random.nextInt(var2);

        for (int var6 = 0; var6 < var5; ++var6) {
            int var7 = random.nextInt(16);
            int var8 = random.nextInt(var3) + var4;
            int var9 = random.nextInt(16);
            worldGenerator.generate(generatorAccess, chunkGenerator, random, blockPosition.a(var7, var8, var9), c);
        }

        return true;
    }
}
