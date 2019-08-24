package de.derfrzocker.ore.control.impl.v1_14_R1;

import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.NMSUtil;
import de.derfrzocker.ore.control.api.Ore;
import de.derfrzocker.ore.control.api.OreControlService;
import de.derfrzocker.spigot.utils.ChunkCoordIntPair;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_14_R1.*;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;

import java.util.function.Supplier;

@SuppressWarnings("Duplicates")
@RequiredArgsConstructor
public class NMSUtil_v1_14_R1 implements NMSUtil {

    @NonNull
    private final Supplier<OreControlService> serviceSupplier;

    @Override
    public void replaceNMS() {
        new NMSReplacer_v1_14_R1(serviceSupplier).replaceNMS();
    }

    @Override
    public Biome getBiome(final @NonNull World world, final @NonNull ChunkCoordIntPair chunkCoordIntPair) {
        final BiomeBase biomeBase = ((CraftWorld) world).getHandle().getChunkProvider().getChunkGenerator().getWorldChunkManager().getBiome(chunkCoordIntPair.getX() << 4, chunkCoordIntPair.getZ() << 4);

        return Biome.valueOf(IRegistry.BIOME.getKey(biomeBase).getKey().toUpperCase());
    }

    @Override
    public Object createFeatureConfiguration(final @NonNull Object defaultFeatureConfiguration, final @NonNull int veinsSize) {
        final WorldGenFeatureConfigured<WorldGenFeatureOreConfiguration> worldGenFeatureConfigured = (WorldGenFeatureConfigured<WorldGenFeatureOreConfiguration>) defaultFeatureConfiguration;
        final WorldGenFeatureOreConfiguration worldGenFeatureOreConfiguration = new WorldGenFeatureOreConfiguration(WorldGenFeatureOreConfiguration.Target.NATURAL_STONE, (worldGenFeatureConfigured.b).c, veinsSize);

        return new WorldGenFeatureConfigured<>(worldGenFeatureConfigured.a, worldGenFeatureOreConfiguration);
    }

    @Override
    public Object createCountConfiguration(final int veinsPerChunk, final int minimumHeight, final int heightSubtractValue, final int heightRange) {
        return new WorldGenFeatureChanceDecoratorCountConfiguration(veinsPerChunk, minimumHeight, heightSubtractValue, heightRange);
    }

    @Override
    public Object createHeightAverageConfiguration(final int veinsPerChunk, final int heightCenter, final int heightRange) {
        return new WorldGenDecoratorHeightAverageConfiguration(veinsPerChunk, heightCenter, heightRange);
    }

    @Override
    public Ore getOre(final @NonNull Object object) {
        if (object == Blocks.DIAMOND_ORE)
            return Ore.DIAMOND;
        if (object == Blocks.COAL_ORE)
            return Ore.COAL;
        if (object == Blocks.IRON_ORE)
            return Ore.IRON;
        if (object == Blocks.REDSTONE_ORE)
            return Ore.REDSTONE;
        if (object == Blocks.GOLD_ORE)
            return Ore.GOLD;
        if (object == Blocks.DIRT)
            return Ore.DIRT;
        if (object == Blocks.GRAVEL)
            return Ore.GRAVEL;
        if (object == Blocks.GRANITE)
            return Ore.GRANITE;
        if (object == Blocks.DIORITE)
            return Ore.DIORITE;
        if (object == Blocks.ANDESITE)
            return Ore.ANDESITE;

        return null;
    }
}
