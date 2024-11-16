package de.derfrzocker.feature.common.value.target;

import de.derfrzocker.feature.common.AbstractValue;

import java.util.List;

public abstract class TargetListValue extends AbstractValue<TargetListValue, TargetListType, List<TargetBlockState>> {

    @Override
    public abstract TargetListValue clone();
}
