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

import java.util.Optional;
import java.util.Random;

@SuppressWarnings("Duplicates")
@RequiredArgsConstructor
public class WorldGenDecoratorNetherHeightBadlandsGoldOverrider_v1_13_R1 extends WorldGenDecoratorNetherHeight {

    @NonNull
    private final Biome biome;

    @Override
    public <C extends WorldGenFeatureConfiguration> boolean a(GeneratorAccess generatorAccess, ChunkGenerator<? extends GeneratorSettings> chunkGenerator, Random random, BlockPosition blockPosition, WorldGenFeatureChanceDecoratorCountConfiguration worldGenFeatureChanceDecoratorCountConfiguration, WorldGenerator<C> worldGenerator, C c) {
        Optional<WorldOreConfig> oreConfig = OreControl.getService().getWorldOreConfig(generatorAccess.getMinecraftWorld().getWorld());

        if (oreConfig.isPresent() && !OreControlUtil.isActivated(Ore.LAPIS, oreConfig.get(), biome))
            return true;

        return oreConfig.
                map(value -> OreControlUtil.getOreSettings(Ore.GOLD_BADLANDS, value, biome)).
                map(oreSettings -> super.a(generatorAccess, chunkGenerator, random, blockPosition, new WorldGenFeatureChanceDecoratorCountConfiguration(
                                oreSettings.getValue(Setting.VEINS_PER_CHUNK).orElse(Setting.VEINS_PER_CHUNK.getMinimumValue()),
                                oreSettings.getValue(Setting.MINIMUM_HEIGHT).orElse(Setting.MINIMUM_HEIGHT.getMinimumValue()),
                                oreSettings.getValue(Setting.HEIGHT_SUBTRACT_VALUE).orElse(Setting.HEIGHT_SUBTRACT_VALUE.getMinimumValue()),
                                oreSettings.getValue(Setting.HEIGHT_RANGE).orElse(Setting.HEIGHT_RANGE.getMinimumValue())), worldGenerator,
                        (C) new WorldGenFeatureOreConfiguration(WorldGenFeatureOreConfiguration.a, ((WorldGenFeatureOreConfiguration) c).d, oreSettings.getValue(Setting.VEIN_SIZE).orElse(Setting.VEIN_SIZE.getMinimumValue())))).
                orElseGet(() -> super.a(generatorAccess, chunkGenerator, random, blockPosition, worldGenFeatureChanceDecoratorCountConfiguration, worldGenerator, c));
    }

}
