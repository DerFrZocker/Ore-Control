package de.derfrzocker.feature.impl.v1_21_R3.value.offset;

import de.derfrzocker.feature.common.value.number.IntegerValue;
import de.derfrzocker.feature.common.value.offset.BelowTopOffsetIntegerValue;
import java.util.Random;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import org.bukkit.craftbukkit.generator.CraftLimitedRegion;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;

public class NMSBelowTopOffsetIntegerValue extends BelowTopOffsetIntegerValue {

    public NMSBelowTopOffsetIntegerValue(IntegerValue base) {
        super(base);
    }

    @Override
    public Integer getValue(@NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull BlockVector position, @NotNull LimitedRegion limitedRegion) {
        int baseValue = getBase() == null ? 0 : getBase().getValue(worldInfo, random, position, limitedRegion);

        WorldGenLevel level = ((CraftLimitedRegion) limitedRegion).getHandle();
        ChunkGenerator chunkGenerator = level.getLevel().getChunkSource().getGenerator();

        return Math.min(level.getHeight(), chunkGenerator.getGenDepth()) - 1 + Math.max(level.getMinY(), chunkGenerator.getMinY()) - baseValue;
    }

    @Override
    public NMSBelowTopOffsetIntegerValue clone() {
        return new NMSBelowTopOffsetIntegerValue(getBase() == null ? null : getBase().clone());
    }
}
