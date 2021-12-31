/*
 * MIT License
 *
 * Copyright (c) 2019 - 2021 Marvin (DerFrZocker)
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

package de.derfrzocker.feature.common.value.number;

import com.mojang.serialization.Codec;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

public class FixedFloatType extends FloatType {

    public static final NamespacedKey KEY = NamespacedKey.fromString("feature:fixed_float");
    public static final FixedFloatType INSTANCE = new FixedFloatType();
    public static final Codec<FixedFloatValue> CODEC = Codec.FLOAT.xmap(FixedFloatValue::new, FixedFloatValue::getValue);

    private FixedFloatType() {
    }

    @Override
    public Codec<FloatValue> getCodec() {
        return CODEC.xmap(value -> value, value -> (FixedFloatValue) value);
    }

    @Override
    public Class<Float> getTypeClass() {
        return Float.class;
    }

    @NotNull
    @Override
    public NamespacedKey getKey() {
        return KEY;
    }
}
