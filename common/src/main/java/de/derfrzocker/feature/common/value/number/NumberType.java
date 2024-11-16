package de.derfrzocker.feature.common.value.number;

import de.derfrzocker.feature.api.ValueType;

public abstract class NumberType<V extends NumberValue<V, T, O>, T extends NumberType<V, T, O>, O extends Number> implements ValueType<V, T, O> {
}
