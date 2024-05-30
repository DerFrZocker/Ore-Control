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

package de.derfrzocker.feature.common.value.target;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.derfrzocker.feature.api.Registries;
import de.derfrzocker.feature.api.util.Parser;
import java.util.List;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class FixedTargetListType extends TargetListType {

    public static final NamespacedKey KEY = NamespacedKey.fromString("feature:fixed_target_list");
    private static FixedTargetListType type = null;
    private final Parser<TargetListValue> parser;

    public FixedTargetListType(Registries registries) {
        if (type != null) {
            throw new IllegalStateException("FixedTargetListType was already created!");
        }

        parser = new Parser<>() {
            private final Parser<TargetBlockState> targetBlockStateParser = TargetBlockState.createParser(registries);
            @Override
            public JsonElement toJson(TargetListValue v) {
                FixedTargetListValue value = (FixedTargetListValue) v;
                JsonObject jsonObject = new JsonObject();

                if (value.getValue() != null) {
                    JsonArray array = new JsonArray();
                    for (TargetBlockState targetBlockState : value.getValue()) {
                        array.add(targetBlockStateParser.toJson(targetBlockState));
                    }
                }

                return jsonObject;
            }

            @Override
            public FixedTargetListValue fromJson(JsonElement jsonElement) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();

                List<TargetBlockState> targetBlockStates = null;
                if (jsonObject.has("value")) {
                    targetBlockStates = new ArrayList<>();
                    for (JsonElement element : jsonObject.getAsJsonArray("value")) {
                        targetBlockStates.add(targetBlockStateParser.fromJson(element));
                    }
                }

                return new FixedTargetListValue(targetBlockStates);
            }
        };
        type = this;
    }

    public static FixedTargetListType type() {
        return type;
    }

    @Override
    public Parser<TargetListValue> getParser() {
        return parser;
    }

    @Override
    public TargetListValue createNewValue() {
        return new FixedTargetListValue(new ArrayList<>());
    }

    @NotNull
    @Override
    public NamespacedKey getKey() {
        return KEY;
    }
}
