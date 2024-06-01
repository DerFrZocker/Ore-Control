package de.derfrzocker.feature.common.value.number.integer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.derfrzocker.feature.api.util.Parser;
import de.derfrzocker.feature.common.value.number.IntegerType;
import de.derfrzocker.feature.common.value.number.IntegerValue;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

public class Exp4jIntegerType extends IntegerType {

    public static final NamespacedKey KEY = NamespacedKey.fromString("feature:exp4f_integer");
    public static final Exp4jIntegerType INSTANCE = new Exp4jIntegerType();
    public static final Parser<IntegerValue> PARSER = new Parser<>() {
        @Override
        public JsonElement toJson(IntegerValue v) {
            Exp4jIntegerValue value = (Exp4jIntegerValue) v;
            JsonObject jsonObject = new JsonObject();

            jsonObject.addProperty("value", value.getExpressionString());

            return jsonObject;
        }

        @Override
        public Exp4jIntegerValue fromJson(JsonElement jsonElement) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();

            String value = null;
            if (jsonObject.has("value")) {
                value = jsonObject.get("value").getAsString();
            }

            return new Exp4jIntegerValue(value);
        }
    };

    private Exp4jIntegerType() {
    }

    @Override
    public Parser<IntegerValue> getParser() {
        return PARSER;
    }

    @Override
    public Exp4jIntegerValue createNewValue() {
        return new Exp4jIntegerValue("0");
    }

    @NotNull
    @Override
    public NamespacedKey getKey() {
        return KEY;
    }
}
