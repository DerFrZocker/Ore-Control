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

public class AboveBottomOffsetIntegerType extends IntegerType {

    public static final NamespacedKey KEY = NamespacedKey.fromString("feature:above_bottom_offset_integer");
    private static AboveBottomOffsetIntegerType type = null;
    private final Parser<IntegerValue> parser;
    private final Function<IntegerValue, AboveBottomOffsetIntegerValue> newValue;

    public AboveBottomOffsetIntegerType(Registries registries, Function<IntegerValue, AboveBottomOffsetIntegerValue> newValue) {
        if (type != null) {
            throw new IllegalStateException("AboveBottomOffsetIntegerType was already created!");
        }

        parser = new Parser<>() {
            @Override
            public JsonElement toJson(IntegerValue v) {
                AboveBottomOffsetIntegerValue value = (AboveBottomOffsetIntegerValue) v;
                JsonObject jsonObject = new JsonObject();

                if (value.getBase() != null) {
                    JsonObject entry = value.getBase().getValueType().getParser().toJson(value.getBase()).getAsJsonObject();
                    entry.addProperty("base_type", value.getBase().getValueType().getKey().toString());
                    jsonObject.add("base", entry);
                }

                return jsonObject;
            }

            @Override
            public AboveBottomOffsetIntegerValue fromJson(JsonElement jsonElement) {
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

    public static AboveBottomOffsetIntegerType type() {
        return type;
    }

    @Override
    public Parser<IntegerValue> getParser() {
        return parser;
    }

    @Override
    public AboveBottomOffsetIntegerValue createNewValue() {
        return newValue.apply(new FixedDoubleToIntegerValue(0));
    }

    @NotNull
    @Override
    public NamespacedKey getKey() {
        return KEY;
    }
}

