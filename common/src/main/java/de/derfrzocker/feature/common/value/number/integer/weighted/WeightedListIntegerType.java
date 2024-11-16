package de.derfrzocker.feature.common.value.number.integer.weighted;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.derfrzocker.feature.api.Registries;
import de.derfrzocker.feature.api.util.Parser;
import de.derfrzocker.feature.common.value.number.IntegerType;
import de.derfrzocker.feature.common.value.number.IntegerValue;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class WeightedListIntegerType extends IntegerType {

    public static final NamespacedKey KEY = NamespacedKey.fromString("feature:weighted_list_integer");
    private static WeightedListIntegerType type = null;
    private final Parser<IntegerValue> parser;

    public WeightedListIntegerType(Registries registries) {
        if (type != null) {
            throw new IllegalStateException("WeightedListIntegerType was already created!");
        }

        parser = new Parser<>() {
            @Override
            public JsonElement toJson(IntegerValue v) {
                WeightedListIntegerValue value = (WeightedListIntegerValue) v;
                JsonObject jsonObject = new JsonObject();

                if (value.getDistribution() != null) {
                    JsonArray array = new JsonArray();
                    for (Map.Entry<IntegerValue, IntegerValue> entry : value.getDistribution().entrySet()) {
                        JsonObject object = new JsonObject();

                        JsonObject key = entry.getKey().getValueType().getParser().toJson(entry.getKey()).getAsJsonObject();
                        key.addProperty("data_type", entry.getKey().getValueType().getKey().toString());
                        object.add("data", key);

                        JsonObject val = entry.getValue().getValueType().getParser().toJson(entry.getValue()).getAsJsonObject();
                        key.addProperty("weight_type", entry.getValue().getValueType().getKey().toString());
                        object.add("weight", val);

                        array.add(object);
                    }

                    jsonObject.add("distribution", array);
                }

                return jsonObject;
            }

            @Override
            public WeightedListIntegerValue fromJson(JsonElement jsonElement) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();

                Map<IntegerValue, IntegerValue> values = null;
                if (jsonObject.has("distribution")) {
                    values = new LinkedHashMap<>();
                    for (JsonElement element : jsonObject.get("distribution").getAsJsonArray()) {
                        JsonObject object = element.getAsJsonObject();

                        JsonObject keyObject = object.getAsJsonObject("data");
                        IntegerValue key = registries.getValueTypeRegistry(IntegerType.class).get(NamespacedKey.fromString(keyObject.getAsJsonPrimitive("data_type").getAsString())).get().getParser().fromJson(keyObject);


                        JsonObject valueObject = object.getAsJsonObject("weight");
                        IntegerValue value = registries.getValueTypeRegistry(IntegerType.class).get(NamespacedKey.fromString(valueObject.getAsJsonPrimitive("weight_type").getAsString())).get().getParser().fromJson(valueObject);

                        values.put(key, value);
                    }
                }

                return new WeightedListIntegerValue(values);
            }
        };

        type = this;
    }

    public static WeightedListIntegerType type() {
        return type;
    }

    @Override
    public Parser<IntegerValue> getParser() {
        return parser;
    }

    @Override
    public WeightedListIntegerValue createNewValue() {
        return new WeightedListIntegerValue(Collections.emptyMap());
    }

    @NotNull
    @Override
    public NamespacedKey getKey() {
        return KEY;
    }
}
