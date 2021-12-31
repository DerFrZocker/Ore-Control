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

package de.derfrzocker.feature.impl.v1_18_R1.value.intprovider;

import com.mojang.serialization.Codec;
import net.minecraft.util.valueproviders.IntProvider;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

public class FixedIntProviderType extends IntProviderType {
    public static final NamespacedKey KEY = NamespacedKey.fromString("feature:fixed_int_provider");
    public static final FixedIntProviderType INSTANCE = new FixedIntProviderType();
    public static final Codec<FixedIntProviderValue> CODEC = IntProvider.CODEC.xmap(FixedIntProviderValue::new, FixedIntProviderValue::getValue);

    private FixedIntProviderType() {
    }

    @Override
    public Codec<IntProviderValue> getCodec() {
        return CODEC.xmap(value -> value, value -> (FixedIntProviderValue) value);
    }

    @Override
    public Class<IntProvider> getTypeClass() {
        return IntProvider.class;
    }

    @NotNull
    @Override
    public NamespacedKey getKey() {
        return KEY;
    }
}
