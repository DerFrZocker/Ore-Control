package de.derfrzocker.feature.common.value.number.integer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.derfrzocker.feature.api.util.Parser;
import de.derfrzocker.feature.common.value.number.IntegerType;
import de.derfrzocker.feature.common.value.number.IntegerValue;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

public class FixedDoubleToIntegerType extends IntegerType {

    public static final NamespacedKey KEY = NamespacedKey.fromString("feature:fixed_double_to_integer");
    public static final FixedDoubleToIntegerType INSTANCE = new FixedDoubleToIntegerType();
    public static final Parser<IntegerValue> PARSER = new Parser<>() {
        @Override
        public JsonElement toJson(IntegerValue v) {
            FixedDoubleToIntegerValue value = (FixedDoubleToIntegerValue) v;
            JsonObject jsonObject = new JsonObject();

            jsonObject.addProperty("value", value.getValue());

            return jsonObject;
        }

        @Override
        public FixedDoubleToIntegerValue fromJson(JsonElement jsonElement) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();

            double value = 0d;
            if (jsonObject.has("value")) {
                value = jsonObject.get("value").getAsDouble();
            }

            return new FixedDoubleToIntegerValue(value);
        }
    };

    private FixedDoubleToIntegerType() {
    }

    @Override
    public Parser<IntegerValue> getParser() {
        return PARSER;
    }

    @Override
    public FixedDoubleToIntegerValue createNewValue() {
        return new FixedDoubleToIntegerValue(0);
    }

    @NotNull
    @Override
    public NamespacedKey getKey() {
        return KEY;
    }
}
