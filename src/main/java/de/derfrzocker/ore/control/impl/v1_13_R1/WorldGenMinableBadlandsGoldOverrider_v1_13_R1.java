package de.derfrzocker.ore.control.impl.v1_13_R1;

import de.derfrzocker.ore.control.OreControl;
import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.Ore;
import de.derfrzocker.ore.control.api.Setting;
import de.derfrzocker.ore.control.utils.OreControlUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_13_R1.*;

import java.util.Random;

@RequiredArgsConstructor
public class WorldGenMinableBadlandsGoldOverrider_v1_13_R1 extends WorldGenMinable {

    @NonNull
    private final Biome biome;

    @Override
    public boolean a(GeneratorAccess generatorAccess, ChunkGenerator<? extends GeneratorSettings> chunkGenerator, Random random, BlockPosition blockPosition, WorldGenFeatureOreConfiguration worldGenFeatureOreConfiguration) {
        return OreControl.getService().
                getWorldOreConfig(generatorAccess.getMinecraftWorld().getWorld()).
                map(value -> super.a(generatorAccess, chunkGenerator, random, blockPosition, new WorldGenFeatureOreConfiguration(WorldGenFeatureOreConfiguration.a, worldGenFeatureOreConfiguration.d, OreControlUtil.getAmount(Ore.GOLD_BADLANDS, Setting.VEIN_SIZE, value, biome)))).
                orElseGet(() -> super.a(generatorAccess, chunkGenerator, random, blockPosition, worldGenFeatureOreConfiguration));
    }

}
