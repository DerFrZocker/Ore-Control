package de.derfrzocker.feature.common.value.target;

import de.derfrzocker.feature.api.ValueLocation;
import de.derfrzocker.feature.api.util.traverser.message.StringFormatter;
import de.derfrzocker.feature.api.util.traverser.message.TraversKey;
import de.derfrzocker.feature.common.util.MessageTraversUtil;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class FixedTargetListValue extends TargetListValue {

    private final List<TargetBlockState> value;
    private boolean dirty = false;

    public FixedTargetListValue(List<TargetBlockState> value) {
        this.value = value;
    }

    @Override
    public FixedTargetListType getValueType() {
        return FixedTargetListType.type();
    }

    @Override
    public List<TargetBlockState> getValue(@NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull BlockVector position, @NotNull LimitedRegion limitedRegion) {
        return value;
    }

    public List<TargetBlockState> getValue() {
        return value;
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    @Override
    public void saved() {
        dirty = false;
    }

    @Override
    public FixedTargetListValue clone() {
        if (value == null) {
            return new FixedTargetListValue(null);
        }

        List<TargetBlockState> values = new ArrayList<>();

        for (TargetBlockState targetBlockState : value) {
            values.add(targetBlockState.clone());
        }

        return new FixedTargetListValue(values);
    }

    @Override
    public void setValueLocation(@NotNull ValueLocation valueLocation) {
        super.setValueLocation(valueLocation);
        // #31: Check for null
        if (value != null) {
            for (TargetBlockState state : getValue()) {
                state.setValueLocation(valueLocation);
            }
        }
    }

    @Override
    public @NotNull List<@NotNull String> traverse(@NotNull StringFormatter formatter, int depth, @NotNull TraversKey key) {
        List<String> result = new LinkedList<>();

        MessageTraversUtil.addIfNotNull(result, formatter.format(depth, key, null));
        MessageTraversUtil.addIfNotNull(result, formatter.format(depth + 1, TraversKey.ofValueType(getValueType().getKey()), null));

        // #31: Check for null
        if (value != null) {
            for (TargetBlockState state : getValue()) {
                // TODO: 5/3/23 Maybe remove the "entry" line
                List<String> states = state.traverse(formatter, depth + 2, TraversKey.ofValueSetting("entry"));
                result.addAll(states);
            }
        }

        return result;
    }
}
