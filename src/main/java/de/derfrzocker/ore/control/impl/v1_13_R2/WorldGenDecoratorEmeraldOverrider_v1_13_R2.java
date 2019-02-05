package de.derfrzocker.ore.control.impl.v1_13_R2;

import de.derfrzocker.ore.control.OreControl;
import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.Ore;
import de.derfrzocker.ore.control.api.Setting;
import de.derfrzocker.ore.control.api.WorldOreConfig;
import de.derfrzocker.ore.control.utils.OreControlUtil;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_13_R2.*;

import java.util.Optional;
import java.util.Random;

@SuppressWarnings("Duplicates")
@RequiredArgsConstructor
public class WorldGenDecoratorEmeraldOverrider_v1_13_R2 extends WorldGenDecoratorEmerald {

    private final Biome biome;


    @Override
    public <C extends WorldGenFeatureConfiguration> boolean a(GeneratorAccess generatorAccess, ChunkGenerator<? extends GeneratorSettings> chunkGenerator, Random random, BlockPosition blockPosition, WorldGenFeatureDecoratorEmptyConfiguration worldGenFeatureDecoratorEmptyConfiguration, WorldGenerator<C> worldGenerator, C c) {
        Optional<WorldOreConfig> config = OreControl.getService().getWorldOreConfig(generatorAccess.getMinecraftWorld().getWorld());

        int var1 = 3, var2 = 6, var3 = 28, var4 = 4;

        if (config.isPresent()) {
            if (!OreControlUtil.isActivated(Ore.EMERALD, config.get(), biome))
                return true;

            try {
                var1 = OreControlUtil.getAmount(Ore.EMERALD, Setting.MINIMUM_ORES_PER_CHUNK, config.get(), biome);
                var2 = OreControlUtil.getAmount(Ore.EMERALD, Setting.ORES_PER_CHUNK_RANGE, config.get(), biome);
                var3 = OreControlUtil.getAmount(Ore.EMERALD, Setting.HEIGHT_RANGE, config.get(), biome);
                var4 = OreControlUtil.getAmount(Ore.EMERALD, Setting.MINIMUM_HEIGHT, config.get(), biome);
            } catch (IllegalArgumentException ignored) {
            }
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
