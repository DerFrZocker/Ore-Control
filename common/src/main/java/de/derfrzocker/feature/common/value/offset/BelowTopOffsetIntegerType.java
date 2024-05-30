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

package de.derfrzocker.feature.common.value.offset;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.derfrzocker.feature.api.Registries;
import de.derfrzocker.feature.api.util.Parser;
import de.derfrzocker.feature.common.value.number.IntegerType;
import de.derfrzocker.feature.common.value.number.IntegerValue;
import de.derfrzocker.feature.common.value.number.integer.FixedDoubleToIntegerValue;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class BelowTopOffsetIntegerType extends IntegerType {

    public static final NamespacedKey KEY = NamespacedKey.fromString("feature:below_top_offset_integer");
    private static BelowTopOffsetIntegerType type = null;
    private final Parser<IntegerValue> parser;
    private final Function<IntegerValue, BelowTopOffsetIntegerValue> newValue;

    public BelowTopOffsetIntegerType(Registries registries, Function<IntegerValue, BelowTopOffsetIntegerValue> newValue) {
        if (type != null) {
            throw new IllegalStateException("BelowTopOffsetIntegerType was already created!");
        }

        parser = new Parser<>() {
            @Override
            public JsonElement toJson(IntegerValue v) {
                BelowTopOffsetIntegerValue value = (BelowTopOffsetIntegerValue) v;
                JsonObject jsonObject = new JsonObject();

                if (value.getBase() != null) {
                    JsonObject entry = value.getBase().getValueType().getParser().toJson(value.getBase()).getAsJsonObject();
                    entry.addProperty("base_type", value.getBase().getValueType().getKey().toString());
                    jsonObject.add("base", entry);
                }

                return jsonObject;
            }

            @Override
            public BelowTopOffsetIntegerValue fromJson(JsonElement jsonElement) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();

                IntegerValue base = null;
                if (jsonObject.has("base")) {
                    JsonObject entry = jsonObject.getAsJsonObject("base");
                    base = registries.getValueTypeRegistry(IntegerType.class).get(NamespacedKey.fromString(entry.getAsJsonPrimitive("base_type").getAsString())).get().getParser().fromJson(entry);
                }

                return newValue.apply(base);
            }
        };

        this.newValue = newValue;

        type = this;
    }

    public static BelowTopOffsetIntegerType type() {
        return type;
    }

    @Override
    public Parser<IntegerValue> getParser() {
        return parser;
    }

    @Override
    public BelowTopOffsetIntegerValue createNewValue() {
        return newValue.apply(new FixedDoubleToIntegerValue(0));
    }

    @NotNull
    @Override
    public NamespacedKey getKey() {
        return KEY;
    }
}

