/*
 * MIT License
 *
 * Copyright (c) 2019 - 2022 Marvin (DerFrZocker)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package de.derfrzocker.feature.common.value.number.integer.weighted;

import de.derfrzocker.feature.api.ValueLocation;
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
    public List<String> traverse(StringFormatter formatter, int depth, String key) {
        List<String> result = new LinkedList<>();
        result.add(formatter.format(depth, key, null));
        for (Map.Entry<IntegerValue, IntegerValue> entry : getDistribution().entrySet()) {
            IntegerValue dataValue = entry.getKey();
            IntegerValue weightValue = entry.getValue();
            List<String> data = dataValue.traverse(formatter, depth + 1, "data");
            List<String> weight = weightValue.traverse(formatter, depth + 1, "weight");
            result.addAll(data);
            result.addAll(weight);
        }

        return result;
    }
}