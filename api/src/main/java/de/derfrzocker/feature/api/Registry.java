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

package de.derfrzocker.feature.api;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class Registry<V extends Keyed> implements Codec<V> {

    public static final Codec<NamespacedKey> KEY_CODEC = Codec.STRING.comapFlatMap(value -> {
        if (value == null || value.isEmpty()) {
            return DataResult.error("Value is null or empty");
        }
        NamespacedKey namespacedKey = NamespacedKey.fromString(value);
        if (namespacedKey == null) {
            return DataResult.error("Value " + value + " could not be parsed to a NamespaceKey");
        }

        return DataResult.success(namespacedKey);
    }, NamespacedKey::toString);

    private final Map<NamespacedKey, V> values = new LinkedHashMap<>();

    public Optional<V> get(NamespacedKey key) {
        return Optional.ofNullable(values.get(key));
    }

    public void register(V value) {
        values.put(value.getKey(), value);
    }

    public Map<NamespacedKey, V> getValues() {
        return values;
    }

    @Override
    public <T> DataResult<Pair<V, T>> decode(DynamicOps<T> ops, T input) {
        return KEY_CODEC.
                decode(ops, input).
                flatMap(result -> get(result.getFirst()).
                        map(v -> DataResult.success(Pair.of(v, result.getSecond()), Lifecycle.experimental())).
                        orElseGet(() -> DataResult.error("No value for key " + result.getFirst() + " registered")));
    }

    @Override
    public <T> DataResult<T> encode(V input, DynamicOps<T> ops, T prefix) {
        return ops.mergeToPrimitive(prefix, ops.createString(input.getKey().toString())).setLifecycle(Lifecycle.experimental());
    }
}
