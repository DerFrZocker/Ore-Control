package de.derfrzocker.feature.common.value.number;

import de.derfrzocker.feature.common.AbstractValue;

public abstract class NumberValue<V extends NumberValue<V, T, O>, T extends NumberType<V, T, O>, O extends Number> extends AbstractValue<V, T, O> {

    @Override
    public abstract NumberValue<V, T, O> clone();
}
