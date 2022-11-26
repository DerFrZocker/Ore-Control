package de.derfrzocker.feature.common;

import de.derfrzocker.feature.api.Value;
import de.derfrzocker.feature.api.ValueLocation;
import de.derfrzocker.feature.api.ValueType;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractValue<V extends Value<V, T, O>, T extends ValueType<V, T, O>, O> implements Value<V, T, O> {

    private ValueLocation valueLocation = ValueLocation.UNKNOWN;

    @Override
    public @NotNull ValueLocation getValueLocation() {
        return valueLocation;
    }

    @Override
    public void setValueLocation(@NotNull ValueLocation valueLocation) {
        this.valueLocation = valueLocation;
    }

    public abstract AbstractValue<V, T, O> clone();
}
