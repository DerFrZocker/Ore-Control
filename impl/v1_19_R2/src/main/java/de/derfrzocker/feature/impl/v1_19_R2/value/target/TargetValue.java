package de.derfrzocker.feature.impl.v1_19_R2.value.target;

import de.derfrzocker.feature.common.AbstractValue;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;

public abstract class TargetValue extends AbstractValue<TargetValue, TargetType, OreConfiguration.TargetBlockState> {

    @Override
    public abstract TargetValue clone();
}
