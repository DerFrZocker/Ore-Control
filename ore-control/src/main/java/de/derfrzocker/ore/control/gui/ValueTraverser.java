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

package de.derfrzocker.ore.control.gui;

import de.derfrzocker.feature.api.Configuration;
import de.derfrzocker.feature.api.Setting;
import de.derfrzocker.feature.api.Value;
import de.derfrzocker.feature.common.value.number.FixedFloatType;
import de.derfrzocker.feature.common.value.number.FixedFloatValue;
import de.derfrzocker.feature.common.value.number.IntegerValue;
import de.derfrzocker.feature.common.value.number.integer.FixedDoubleToIntegerType;
import de.derfrzocker.feature.common.value.number.integer.FixedDoubleToIntegerValue;
import de.derfrzocker.feature.common.value.number.integer.FixedIntegerType;
import de.derfrzocker.feature.common.value.number.integer.FixedIntegerValue;
import de.derfrzocker.feature.common.value.number.integer.biased.BiasedToBottomIntegerType;
import de.derfrzocker.feature.common.value.number.integer.biased.BiasedToBottomIntegerValue;
import de.derfrzocker.feature.common.value.number.integer.clamped.ClampedIntegerType;
import de.derfrzocker.feature.common.value.number.integer.clamped.ClampedIntegerValue;
import de.derfrzocker.feature.common.value.number.integer.trapezoid.TrapezoidIntegerType;
import de.derfrzocker.feature.common.value.number.integer.trapezoid.TrapezoidIntegerValue;
import de.derfrzocker.feature.common.value.number.integer.uniform.UniformIntegerType;
import de.derfrzocker.feature.common.value.number.integer.uniform.UniformIntegerValue;
import de.derfrzocker.feature.common.value.number.integer.weighted.WeightedListIntegerType;
import de.derfrzocker.feature.common.value.number.integer.weighted.WeightedListIntegerValue;
import de.derfrzocker.feature.common.value.offset.AboveBottomOffsetIntegerType;
import de.derfrzocker.feature.common.value.offset.AboveBottomOffsetIntegerValue;
import de.derfrzocker.feature.common.value.offset.BelowTopOffsetIntegerType;
import de.derfrzocker.feature.common.value.offset.BelowTopOffsetIntegerValue;
import org.bukkit.NamespacedKey;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

// TODO clean up / make more flexible
public class ValueTraverser {

    public String traverse(Configuration configuration) {
        StringBuilder stringBuilder = new StringBuilder();
        boolean first = true;
        for (Setting setting : configuration.getSettings()) {
            if (first) {
                first = false;
            } else {
                stringBuilder.append("%%new-line%");
            }
            stringBuilder.append(traverse(configuration.getValue(setting), "§r §7>§r§f " + getTranslationSettingKey(setting.name()) + ": "));
        }

        return stringBuilder.toString();
    }

    public String traverse(Value<?, ?, ?> value, String key) {
        List<String> list = traverse(value, key, " ", "§r§7>§r§f ");
        StringBuilder stringBuilder = new StringBuilder();
        boolean first = true;
        for (String string : list) {
            if (first) {
                first = false;
            } else {
                stringBuilder.append("%%new-line%");
            }
            stringBuilder.append(string);
        }

        return stringBuilder.toString();
    }

    private List<String> traverse(Value<?, ?, ?> value, String key, String spaces, String arrow) {
        if (value.getValueType() == FixedDoubleToIntegerType.INSTANCE) {
            return Collections.singletonList(key + ((FixedDoubleToIntegerValue) value).getValue());
        }
        if (value.getValueType() == FixedIntegerType.INSTANCE) {
            return Collections.singletonList(key + ((FixedIntegerValue) value).getValue());
        }
        if (value.getValueType() == FixedFloatType.INSTANCE) {
            return Collections.singletonList(key + ((FixedFloatValue) value).getValue());
        }
        if (value.getValueType() == BiasedToBottomIntegerType.type()) {
            BiasedToBottomIntegerValue val = (BiasedToBottomIntegerValue) value;
            IntegerValue minValue = val.getMinInclusive();
            IntegerValue maxValue = val.getMaxInclusive();
            List<String> min = traverse(minValue, getKey("min-inclusive", spaces + " ", arrow), spaces + " ", arrow);
            List<String> max = traverse(maxValue, getKey("max-inclusive", spaces + " ", arrow), spaces + " ", arrow);
            List<String> values = new LinkedList<>();
            values.add(key);
            values.addAll(min);
            values.addAll(max);
            return values;
        }
        if (value.getValueType() == ClampedIntegerType.type()) {
            ClampedIntegerValue val = (ClampedIntegerValue) value;
            IntegerValue sourceValue = val.getSource();
            IntegerValue minValue = val.getMinInclusive();
            IntegerValue maxValue = val.getMaxInclusive();
            List<String> source = traverse(sourceValue, getKey("source", spaces + " ", arrow), spaces + " ", arrow);
            List<String> min = traverse(minValue, getKey("min-inclusive", spaces + " ", arrow), spaces + " ", arrow);
            List<String> max = traverse(maxValue, getKey("max-inclusive", spaces + " ", arrow), spaces + " ", arrow);
            List<String> values = new LinkedList<>();
            values.add(key);
            values.addAll(source);
            values.addAll(min);
            values.addAll(max);
            return values;
        }
        if (value.getValueType() == TrapezoidIntegerType.type()) {
            TrapezoidIntegerValue val = (TrapezoidIntegerValue) value;
            IntegerValue minValue = val.getMinInclusive();
            IntegerValue maxValue = val.getMaxInclusive();
            IntegerValue plateauValue = val.getPlateau();
            List<String> min = traverse(minValue, getKey("min-inclusive", spaces + " ", arrow), spaces + " ", arrow);
            List<String> max = traverse(maxValue, getKey("max-inclusive", spaces + " ", arrow), spaces + " ", arrow);
            List<String> plateau = traverse(plateauValue, getKey("plateau", spaces + " ", arrow), spaces + " ", arrow);
            List<String> values = new LinkedList<>();
            values.add(key);
            values.addAll(min);
            values.addAll(max);
            values.addAll(plateau);
            return values;
        }
        if (value.getValueType() == UniformIntegerType.type()) {
            UniformIntegerValue val = (UniformIntegerValue) value;
            IntegerValue minValue = val.getMinInclusive();
            IntegerValue maxValue = val.getMaxInclusive();
            List<String> min = traverse(minValue, getKey("min-inclusive", spaces + " ", arrow), spaces + " ", arrow);
            List<String> max = traverse(maxValue, getKey("max-inclusive", spaces + " ", arrow), spaces + " ", arrow);
            List<String> values = new LinkedList<>();
            values.add(key);
            values.addAll(min);
            values.addAll(max);
            return values;
        }
        if (value.getValueType() == AboveBottomOffsetIntegerType.type()) {
            AboveBottomOffsetIntegerValue val = (AboveBottomOffsetIntegerValue) value;
            IntegerValue baseValue = val.getBase();
            List<String> base = traverse(baseValue, getKey("base", spaces + " ", arrow), spaces + " ", arrow);
            List<String> values = new LinkedList<>();
            values.add(key);
            values.addAll(base);
            return values;
        }
        if (value.getValueType() == BelowTopOffsetIntegerType.type()) {
            BelowTopOffsetIntegerValue val = (BelowTopOffsetIntegerValue) value;
            IntegerValue baseValue = val.getBase();
            List<String> base = traverse(baseValue, getKey("base", spaces + " ", arrow), spaces + " ", arrow);
            List<String> values = new LinkedList<>();
            values.add(key);
            values.addAll(base);
            return values;
        }
        if (value.getValueType() == WeightedListIntegerType.type()) {
            WeightedListIntegerValue val = (WeightedListIntegerValue) value;
            List<String> values = new LinkedList<>();
            values.add(key);
            for (Map.Entry<IntegerValue, IntegerValue> entry : val.getDistribution().entrySet()) {
                IntegerValue dataValue = entry.getKey();
                IntegerValue weightValue = entry.getValue();
                List<String> data = traverse(dataValue, getKey("data", spaces + " ", arrow), spaces + " ", arrow);
                List<String> weight = traverse(weightValue, getKey("weight", spaces + " ", arrow), spaces + " ", arrow);
                values.addAll(data);
                values.addAll(weight);
            }

            return values;
        }

        return Collections.singletonList(key + "UNKNOWN");
    }

    private String getKey(String key, String spaces, String arrow) {
        return spaces + arrow + getTranslationValueSettingKey(key) + ": ";
    }

    private String getKeyValue(Value<?, ?, ?> value, String spaces, String arrow) {
        return spaces + arrow + getTranslationKey("value-types", value.getValueType().getKey()) + ": ";
    }

    private String getTranslationKey(String value, NamespacedKey key) {
        return "%%translation:[" + value + "." + key.getNamespace() + "." + key.getKey() + "]%";
    }

    private String getTranslationValueSettingKey(String value) {
        return "%%translation:[" + "value-settings." + value + ".name]%";
    }

    private String getTranslationSettingKey(String value) {
        return "%%translation:[" + "settings." + value + ".name]%";
    }
}
