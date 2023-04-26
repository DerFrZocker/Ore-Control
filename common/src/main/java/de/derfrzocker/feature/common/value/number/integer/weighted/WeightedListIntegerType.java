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

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.derfrzocker.feature.api.Registries;
import de.derfrzocker.feature.common.value.number.IntegerType;
import de.derfrzocker.feature.common.value.number.IntegerValue;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class WeightedListIntegerType extends IntegerType {

    public static final NamespacedKey KEY = NamespacedKey.fromString("feature:weighted_list_integer");
    private static WeightedListIntegerType type = null;
    private final Codec<WeightedListIntegerValue> codec;

    public WeightedListIntegerType(Registries registries) {
        if (type != null) {
            throw new IllegalStateException("WeightedListIntegerType was already created!");
        }

        codec = RecordCodecBuilder.create((builder) -> builder.group(
                Codec.list(Codec.mapPair(
                        registries.getValueTypeRegistry(IntegerType.class).dispatchMap("data_type", IntegerValue::getValueType, IntegerType::getCodec).fieldOf("data"),
                        registries.getValueTypeRegistry(IntegerType.class).dispatchMap("weight_type", IntegerValue::getValueType, IntegerType::getCodec).fieldOf("weight")).codec()
                ).optionalFieldOf("distribution").forGetter(config -> {
                    List<Pair<IntegerValue, IntegerValue>> list = new ArrayList<>();
                    for (Map.Entry<IntegerValue, IntegerValue> entry : config.getDistribution().entrySet()) {
                        list.add(new Pair<>(entry.getKey(), entry.getValue()));
                    }
                    if (list.isEmpty()) {
                        return Optional.empty();
                    }
                    return Optional.of(list);
                })
        ).apply(builder, distribution -> {
            if (distribution.isEmpty()) {
                return new WeightedListIntegerValue(null);
            }
            Map<IntegerValue, IntegerValue> values = new LinkedHashMap<>();
            for (Pair<IntegerValue, IntegerValue> value : distribution.get()) {
                values.put(value.getFirst(), value.getSecond());
            }
            return new WeightedListIntegerValue(values);
        }));

        type = this;
    }

    public static WeightedListIntegerType type() {
        return type;
    }

    @Override
    public Codec<IntegerValue> getCodec() {
        return codec.xmap(value -> value, value -> (WeightedListIntegerValue) value);
    }

    @Override
    public WeightedListIntegerValue createNewValue() {
        return new WeightedListIntegerValue(Collections.emptyMap());
    }

    @NotNull
    @Override
    public NamespacedKey getKey() {
        return KEY;
    }
}
