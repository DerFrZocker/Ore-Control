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

package de.derfrzocker.feature.common.value.number;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.derfrzocker.feature.api.util.Parser;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

public class FixedFloatType extends FloatType {

    public static final NamespacedKey KEY = NamespacedKey.fromString("feature:fixed_float");
    public static final FixedFloatType INSTANCE = new FixedFloatType();
    public static final Parser<FloatValue> PARSER = new Parser<>() {

        @Override
        public JsonElement toJson(FloatValue v) {
            FixedFloatValue value = (FixedFloatValue) v;
            JsonObject jsonObject = new JsonObject();

            jsonObject.addProperty("value", value.getValue());

            return jsonObject;
        }

        @Override
        public FixedFloatValue fromJson(JsonElement jsonElement) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();

            float value = 0f;
            if (jsonObject.has("value")) {
                value = jsonObject.get("value").getAsFloat();
            }

            return new FixedFloatValue(value);
        }
    };

    private FixedFloatType() {
    }

    @Override
    public Parser<FloatValue> getParser() {
        return PARSER;
    }

    @Override
    public FixedFloatValue createNewValue() {
        return new FixedFloatValue(0);
    }

    @NotNull
    @Override
    public NamespacedKey getKey() {
        return KEY;
    }
}
