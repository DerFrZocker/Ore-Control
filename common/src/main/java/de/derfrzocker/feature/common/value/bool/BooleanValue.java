package de.derfrzocker.feature.common.value.bool;

import de.derfrzocker.feature.common.AbstractValue;

public abstract class BooleanValue extends AbstractValue<BooleanValue, BooleanType, Boolean> {

    @Override
    public abstract BooleanValue clone();
}
