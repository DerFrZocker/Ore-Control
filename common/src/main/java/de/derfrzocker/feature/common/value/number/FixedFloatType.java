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
