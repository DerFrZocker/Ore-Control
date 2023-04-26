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

package de.derfrzocker.feature.common.value.number.integer.clamped;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.derfrzocker.feature.api.Registries;
import de.derfrzocker.feature.common.value.number.FixedFloatValue;
import de.derfrzocker.feature.common.value.number.FloatType;
import de.derfrzocker.feature.common.value.number.FloatValue;
import de.derfrzocker.feature.common.value.number.IntegerType;
import de.derfrzocker.feature.common.value.number.IntegerValue;
import de.derfrzocker.feature.common.value.number.integer.FixedDoubleToIntegerValue;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ClampedNormalIntegerType extends IntegerType {

    public static final NamespacedKey KEY = NamespacedKey.fromString("feature:clamped_normal_integer");
    private static ClampedNormalIntegerType type = null;
    private final Codec<ClampedNormalIntegerValue> codec;

    public ClampedNormalIntegerType(Registries registries) {
        if (type != null) {
            throw new IllegalStateException("ClampedNormalIntegerType was already created!");
        }

        codec = RecordCodecBuilder.create((builder) -> builder.group(
                registries.getValueTypeRegistry(FloatType.class).dispatch("mean_type", FloatValue::getValueType, FloatType::getCodec).
                        optionalFieldOf("mean").forGetter(config -> Optional.ofNullable(config.getMean())),
                registries.getValueTypeRegistry(FloatType.class).dispatch("deviation_type", FloatValue::getValueType, FloatType::getCodec).
                        optionalFieldOf("deviation").forGetter(config -> Optional.ofNullable(config.getDeviation())),
                registries.getValueTypeRegistry(IntegerType.class).dispatch("min_inclusive_type", IntegerValue::getValueType, IntegerType::getCodec).
                        optionalFieldOf("min_inclusive").forGetter(config -> Optional.ofNullable(config.getMinInclusive())),
                registries.getValueTypeRegistry(IntegerType.class).dispatch("max_inclusive_type", IntegerValue::getValueType, IntegerType::getCodec).
                        optionalFieldOf("max_inclusive").forGetter(config -> Optional.ofNullable(config.getMaxInclusive()))
        ).apply(builder, (mean, deviation, minInclusive, maxInclusive) -> new ClampedNormalIntegerValue(mean.orElse(null), deviation.orElse(null), minInclusive.orElse(null), maxInclusive.orElse(null))));

        type = this;
    }

    public static ClampedNormalIntegerType type() {
        return type;
    }

    @Override
    public Codec<IntegerValue> getCodec() {
        return codec.xmap(value -> value, value -> (ClampedNormalIntegerValue) value);
    }

    @Override
    public ClampedNormalIntegerValue createNewValue() {
        return new ClampedNormalIntegerValue(new FixedFloatValue(0), new FixedFloatValue(0), new FixedDoubleToIntegerValue(0), new FixedDoubleToIntegerValue(1));
    }

    @NotNull
    @Override
    public NamespacedKey getKey() {
        return KEY;
    }
}
