package de.derfrzocker.ore.control.impl.v1_13_R2;

import de.derfrzocker.ore.control.OreControl;
import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.Ore;
import de.derfrzocker.ore.control.api.Setting;
import de.derfrzocker.ore.control.utils.OreControlUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_13_R2.*;

import java.util.Random;

@RequiredArgsConstructor
public class WorldGenDecoratorNetherHeightBadlandsGoldOverrider_v1_13_R2 extends WorldGenDecoratorNetherHeight {

    @NonNull
    private final Biome biome;

    @Override
    public <C extends WorldGenFeatureConfiguration> boolean a(GeneratorAccess generatorAccess, ChunkGenerator<? extends GeneratorSettings> chunkGenerator, Random random, BlockPosition blockPosition, WorldGenFeatureChanceDecoratorCountConfiguration worldGenFeatureChanceDecoratorCountConfiguration, WorldGenerator<C> worldGenerator, C c) {
        return OreControl.getService().
                getWorldOreConfig(generatorAccess.getMinecraftWorld().getWorld()).
                map(value -> OreControlUtil.getOreSettings(Ore.GOLD_BADLANDS, value, biome)).
                map(oreSettings -> super.a(generatorAccess, chunkGenerator, random, blockPosition, new WorldGenFeatureChanceDecoratorCountConfiguration(oreSettings.getValue(Setting.VEINS_PER_CHUNK).orElse(0), oreSettings.getValue(Setting.MINIMUM_HEIGHT).orElse(0), oreSettings.getValue(Setting.HEIGHT_SUBTRACT_VALUE).orElse(0), oreSettings.getValue(Setting.HEIGHT_RANGE).orElse(0)), worldGenerator, c)).
                orElseGet(() -> super.a(generatorAccess, chunkGenerator, random, blockPosition, worldGenFeatureChanceDecoratorCountConfiguration, worldGenerator, c));
    }

}
