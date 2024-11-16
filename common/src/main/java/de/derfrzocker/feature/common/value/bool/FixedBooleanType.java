package de.derfrzocker.feature.common.value.bool;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.derfrzocker.feature.api.util.Parser;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

public class FixedBooleanType extends BooleanType {

    public static final NamespacedKey KEY = NamespacedKey.fromString("feature:fixed_boolean");
    public static final FixedBooleanType INSTANCE = new FixedBooleanType();
    public static final Parser<BooleanValue> PARSER = new Parser<>() {
        @Override
        public JsonElement toJson(BooleanValue v) {
            FixedBooleanValue value = (FixedBooleanValue) v;
            JsonObject jsonObject = new JsonObject();

            jsonObject.addProperty("value", value.getValue());

            return jsonObject;
        }

        @Override
        public FixedBooleanValue fromJson(JsonElement jsonElement) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();

            boolean value = false;
            if (jsonObject.has("value")) {
                value = jsonObject.get("value").getAsBoolean();
            }

            return new FixedBooleanValue(value);
        }
    };

    private FixedBooleanType() {
    }

    @Override
    public Parser<BooleanValue> getParser() {
        return PARSER;
    }

    @Override
    public FixedBooleanValue createNewValue() {
        return new FixedBooleanValue(false);
    }

    @NotNull
    @Override
    public NamespacedKey getKey() {
        return KEY;
    }
}
