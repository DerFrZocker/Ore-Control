package de.derfrzocker.ore.control.impl.v1_13_R2;

import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.NMSUtil;
import de.derfrzocker.ore.control.api.Ore;
import de.derfrzocker.ore.control.api.OreControlService;
import de.derfrzocker.spigot.utils.ChunkCoordIntPair;
import lombok.NonNull;
import net.minecraft.server.v1_13_R2.*;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_13_R2.CraftWorld;

@SuppressWarnings("Duplicates")
public class NMSUtil_v1_13_R2 implements NMSUtil {

    private static OreControlService service;

    static OreControlService getOreControlService() {
        final OreControlService tempService = Bukkit.getServicesManager().load(OreControlService.class);

        if (service == null && tempService == null)
            throw new NullPointerException("The Bukkit Service has no OreControlService and no OreControlService is cached!");

        if (tempService != null && service != tempService)
            service = tempService;

        return service;
    }

    @Override
    public void replaceNMS() {
        new NMSReplacer1_13_R2().replaceNMS();
    }

    @Override
    public Biome getBiome(final @NonNull World world, final @NonNull ChunkCoordIntPair chunkCoordIntPair) {
        final BiomeBase biomeBase = ((CraftWorld) world).getHandle().getChunkProvider().getChunkGenerator().getWorldChunkManager().getBiome(new BlockPosition(chunkCoordIntPair.getX() << 4, 0, chunkCoordIntPair.getZ() << 4), null);

        return Biome.valueOf(IRegistry.BIOME.getKey(biomeBase).getKey().toUpperCase());
    }

    @Override
    public Object createFeatureConfiguration(final @NonNull Object defaultFeatureConfiguration, final int veinsSize) {
        return new WorldGenFeatureOreConfiguration(WorldGenFeatureOreConfiguration.a, ((WorldGenFeatureOreConfiguration) defaultFeatureConfiguration).d, veinsSize);
    }

    @Override
    public Object createCountConfiguration(int veinsPerChunk, int minimumHeight, int heightSubtractValue, int heightRange) {
        return new WorldGenFeatureChanceDecoratorCountConfiguration(veinsPerChunk, minimumHeight, heightSubtractValue, heightRange);
    }

    @Override
    public Object createHeightAverageConfiguration(int veinsPerChunk, int heightCenter, int heightRange) {
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
