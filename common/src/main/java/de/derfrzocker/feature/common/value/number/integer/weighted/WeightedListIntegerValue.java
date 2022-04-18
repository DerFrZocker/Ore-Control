package de.derfrzocker.feature.common.value.number.integer.weighted;

import de.derfrzocker.feature.common.value.number.IntegerValue;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class WeightedListIntegerValue extends IntegerValue {

    private Map<IntegerValue, IntegerValue> distribution = new LinkedHashMap<>();
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
}