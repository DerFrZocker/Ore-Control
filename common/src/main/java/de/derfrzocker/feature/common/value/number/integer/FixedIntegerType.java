package de.derfrzocker.feature.common.value.number.integer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.derfrzocker.feature.api.util.Parser;
import de.derfrzocker.feature.common.value.number.IntegerType;
import de.derfrzocker.feature.common.value.number.IntegerValue;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

public class FixedIntegerType extends IntegerType {

    public static final NamespacedKey KEY = NamespacedKey.fromString("feature:fixed_integer");
    public static final FixedIntegerType INSTANCE = new FixedIntegerType();
    public static final Parser<IntegerValue> PARSER = new Parser<>() {
        @Override
        public JsonElement toJson(IntegerValue v) {
            FixedIntegerValue value = (FixedIntegerValue) v;
            JsonObject jsonObject = new JsonObject();

            jsonObject.addProperty("value", value.getValue());

            return jsonObject;
        }

        @Override
        public FixedIntegerValue fromJson(JsonElement jsonElement) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();

            int value = 0;
            if (jsonObject.has("value")) {
                value = jsonObject.get("value").getAsInt();
            }

            return new FixedIntegerValue(value);
        }
    };

    private FixedIntegerType() {
    }

    @Override
    public Parser<IntegerValue> getParser() {
        return PARSER;
    }

    @Override
    public FixedIntegerValue createNewValue() {
        return new FixedIntegerValue(0);
    }

    @NotNull
    @Override
    public NamespacedKey getKey() {
        return KEY;
    }
}
