package de.derfrzocker.feature.common.value.bool;

import de.derfrzocker.feature.api.Value;

public abstract class BooleanValue implements Value<BooleanValue, BooleanType, Boolean> {

    @Override
    public abstract BooleanValue clone();
}
