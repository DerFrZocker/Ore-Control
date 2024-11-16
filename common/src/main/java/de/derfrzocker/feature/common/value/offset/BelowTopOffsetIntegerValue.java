package de.derfrzocker.feature.common.value.offset;

import de.derfrzocker.feature.api.ValueLocation;
import de.derfrzocker.feature.api.util.traverser.message.StringFormatter;
import de.derfrzocker.feature.api.util.traverser.message.TraversKey;
import de.derfrzocker.feature.common.util.MessageTraversUtil;
import de.derfrzocker.feature.common.value.number.IntegerValue;
import de.derfrzocker.spigot.utils.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class BelowTopOffsetIntegerValue extends IntegerValue {

    private IntegerValue base;
    private boolean dirty = false;

    public BelowTopOffsetIntegerValue(IntegerValue base) {
        this.base = base;
    }

    @Override
    public BelowTopOffsetIntegerType getValueType() {
        return BelowTopOffsetIntegerType.type();
    }

    @Override
    public boolean isDirty() {
        if (dirty) {
            return true;
        }

        return base != null && base.isDirty();
    }

    @Override
    public void saved() {
        dirty = false;

        if (base != null) {
            base.saved();
        }
    }

    public IntegerValue getBase() {
        return base;
    }

    public void setBase(IntegerValue integerValue) {
        integerValue.setValueLocation(getValueLocation());
        this.base = integerValue;
        dirty = true;
    }

    @Override
    public void setValueLocation(@NotNull ValueLocation valueLocation) {
        super.setValueLocation(valueLocation);
        getBase().setValueLocation(valueLocation);
    }

    @Override
    public @NotNull List<@NotNull String> traverse(@NotNull StringFormatter formatter, int depth, @NotNull TraversKey key) {
        return MessageTraversUtil.multiple(formatter, depth, key, TraversKey.ofValueType(getValueType().getKey()),
                new Pair<>("base", getBase()));
    }
}
