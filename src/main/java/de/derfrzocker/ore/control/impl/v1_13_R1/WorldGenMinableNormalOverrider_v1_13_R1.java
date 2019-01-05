package de.derfrzocker.ore.control.impl.v1_13_R1;

import de.derfrzocker.ore.control.OreControl;
import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.Ore;
import de.derfrzocker.ore.control.api.Setting;
import de.derfrzocker.ore.control.api.WorldOreConfig;
import de.derfrzocker.ore.control.utils.OreControlUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_13_R1.*;

import java.util.Random;

@SuppressWarnings("Duplicates")
@RequiredArgsConstructor
public class WorldGenMinableNormalOverrider_v1_13_R1 extends WorldGenMinable {

    @NonNull
    private final Biome biome;

    @Override
    public boolean a(GeneratorAccess generatorAccess, ChunkGenerator<? extends GeneratorSettings> chunkGenerator, Random random, BlockPosition blockPosition, WorldGenFeatureOreConfiguration worldGenFeatureOreConfiguration) {
        return OreControl.getService().
                getWorldOreConfig(generatorAccess.getMinecraftWorld().getWorld()).
                map(worldOreConfig -> super.a(generatorAccess, chunkGenerator, random, blockPosition, new WorldGenFeatureOreConfiguration(WorldGenFeatureOreConfiguration.a, worldGenFeatureOreConfiguration.d, getVault(worldOreConfig, worldGenFeatureOreConfiguration.d.getBlock(), worldGenFeatureOreConfiguration)))).
                orElseGet(() -> super.a(generatorAccess, chunkGenerator, random, blockPosition, worldGenFeatureOreConfiguration));
    }

    private int getVault(WorldOreConfig config, Block block, WorldGenFeatureOreConfiguration worldGen) {
        try {
            if (block == Blocks.DIAMOND_ORE)
                return OreControlUtil.getAmount(Ore.DIAMOND, Setting.VEIN_SIZE, config, biome);
            if (block == Blocks.COAL_ORE)
                return OreControlUtil.getAmount(Ore.COAL, Setting.VEIN_SIZE, config, biome);
            if (block == Blocks.IRON_ORE)
                return OreControlUtil.getAmount(Ore.IRON, Setting.VEIN_SIZE, config, biome);
            if (block == Blocks.REDSTONE_ORE)
                return OreControlUtil.getAmount(Ore.REDSTONE, Setting.VEIN_SIZE, config, biome);
            if (block == Blocks.GOLD_ORE)
                return OreControlUtil.getAmount(Ore.GOLD, Setting.VEIN_SIZE, config, biome);
            if (block == Blocks.LAPIS_ORE)
                return OreControlUtil.getAmount(Ore.LAPIS, Setting.VEIN_SIZE, config, biome);
            if (block == Blocks.DIRT)
                return OreControlUtil.getAmount(Ore.DIRT, Setting.VEIN_SIZE, config, biome);
            if (block == Blocks.GRAVEL)
                return OreControlUtil.getAmount(Ore.GRAVEL, Setting.VEIN_SIZE, config, biome);
            if (block == Blocks.GRANITE)
                return OreControlUtil.getAmount(Ore.GRANITE, Setting.VEIN_SIZE, config, biome);
            if (block == Blocks.DIORITE)
                return OreControlUtil.getAmount(Ore.DIORITE, Setting.VEIN_SIZE, config, biome);
            if (block == Blocks.ANDESITE)
                return OreControlUtil.getAmount(Ore.ANDESITE, Setting.VEIN_SIZE, config, biome);
        } catch (IllegalArgumentException ignored) {
        }
        return worldGen.c;
    }

}
