package de.derfrzocker.feature.common.value.number.integer.weighted;

import de.derfrzocker.feature.api.ValueLocation;
import de.derfrzocker.feature.api.util.traverser.message.StringFormatter;
import de.derfrzocker.feature.api.util.traverser.message.TraversKey;
import de.derfrzocker.feature.common.util.MessageTraversUtil;
import de.derfrzocker.feature.common.value.number.IntegerValue;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class WeightedListIntegerValue extends IntegerValue {

    private final Map<IntegerValue, IntegerValue> distribution = new LinkedHashMap<>();
    private boolean dirty = false;

    public WeightedListIntegerValue(Map<IntegerValue, IntegerValue> distribution) {
        if (distribution != null) {
            this.distribution.putAll(distribution);
        }
    }

    @Override
    public WeightedListIntegerType getValueType() {
        return WeightedListIntegerType.type();
    }

    @Override
    public Integer getValue(@NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull BlockVector position, @NotNull LimitedRegion limitedRegion) {
        List<IntegerValue> list = new ArrayList<>();

        for (Map.Entry<IntegerValue, IntegerValue> value : distribution.entrySet()) {
            int amount = value.getValue().getValue(worldInfo, random, position, limitedRegion);
            for (int i = 0; i < amount; i++) {
                list.add(value.getKey());
            }
        }

        return list.get(random.nextInt(list.size())).getValue(worldInfo, random, position, limitedRegion);
    }

    @Override
    public boolean isDirty() {
        if (dirty) {
            return true;
        }

        for (Map.Entry<IntegerValue, IntegerValue> value : distribution.entrySet()) {
            if (value.getValue().isDirty() || value.getKey().isDirty()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void saved() {
        dirty = false;

        for (Map.Entry<IntegerValue, IntegerValue> value : distribution.entrySet()) {
            value.getKey().saved();
            value.getValue().saved();
        }
    }

    @Override
    public WeightedListIntegerValue clone() {
        Map<IntegerValue, IntegerValue> values = new LinkedHashMap<>();

        for (Map.Entry<IntegerValue, IntegerValue> value : distribution.entrySet()) {
            values.put(value.getKey().clone(), value.getValue().clone());
        }

        return new WeightedListIntegerValue(values);
    }

    public Map<IntegerValue, IntegerValue> getDistribution() {
        return distribution;
    }

    @Override
    public void setValueLocation(@NotNull ValueLocation valueLocation) {
        super.setValueLocation(valueLocation);
        for (Map.Entry<IntegerValue, IntegerValue> entry : getDistribution().entrySet()) {
            entry.getKey().setValueLocation(valueLocation);
            entry.getValue().setValueLocation(valueLocation);
        }
    }

    @Override
    public @NotNull List<@NotNull String> traverse(@NotNull StringFormatter formatter, int depth, @NotNull TraversKey key) {
        List<String> result = new LinkedList<>();

        MessageTraversUtil.addIfNotNull(result, formatter.format(depth, key, null));
        MessageTraversUtil.addIfNotNull(result, formatter.format(depth + 1, TraversKey.ofValueType(getValueType().getKey()), null));

        for (Map.Entry<IntegerValue, IntegerValue> entry : getDistribution().entrySet()) {
            IntegerValue dataValue = entry.getKey();
            IntegerValue weightValue = entry.getValue();
            List<String> data = dataValue.traverse(formatter, depth + 2, TraversKey.ofValueSetting("data"));
            List<String> weight = weightValue.traverse(formatter, depth + 2, TraversKey.ofValueSetting("weight"));
            result.addAll(data);
            result.addAll(weight);
        }

        return result;
    }
}