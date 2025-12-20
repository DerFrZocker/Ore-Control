package de.derfrzocker.feature.impl.v1_21_R7.value.offset;

import de.derfrzocker.feature.common.value.number.IntegerValue;
import de.derfrzocker.feature.common.value.offset.AboveBottomOffsetIntegerValue;
import java.util.Random;
import net.minecraft.world.level.WorldGenLevel;
import org.bukkit.craftbukkit.v1_21_R7.generator.CraftLimitedRegion;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;

public class NMSAboveBottomOffsetIntegerValue extends AboveBottomOffsetIntegerValue {

    public NMSAboveBottomOffsetIntegerValue(IntegerValue base) {
        super(base);
    }

    @Override
    public Integer getValue(@NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull BlockVector position, @NotNull LimitedRegion limitedRegion) {
        int baseValue = getBase() == null ? 0 : getBase().getValue(worldInfo, random, position, limitedRegion);

        WorldGenLevel level = ((CraftLimitedRegion) limitedRegion).getHandle();

        return Math.max(level.getMinY(), level.getLevel().getChunkSource().getGenerator().getMinY()) + baseValue;
    }

    @Override
    public AboveBottomOffsetIntegerValue clone() {
        return new NMSAboveBottomOffsetIntegerValue(getBase() == null ? null : getBase().clone());
    }
}
