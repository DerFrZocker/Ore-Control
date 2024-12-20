package de.derfrzocker.feature.common.value.number.integer.clamped;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.derfrzocker.feature.api.Registries;
import de.derfrzocker.feature.api.util.Parser;
import de.derfrzocker.feature.common.value.number.IntegerType;
import de.derfrzocker.feature.common.value.number.IntegerValue;
import de.derfrzocker.feature.common.value.number.integer.FixedDoubleToIntegerValue;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

public class ClampedIntegerType extends IntegerType {

    public static final NamespacedKey KEY = NamespacedKey.fromString("feature:clamped_integer");
    private static ClampedIntegerType type = null;
    private final Parser<IntegerValue> parser;

    public ClampedIntegerType(Registries registries) {
        if (type != null) {
            throw new IllegalStateException("ClampedIntegerType was already created!");
        }

        parser = new Parser<>() {
            @Override
            public JsonElement toJson(IntegerValue v) {
                ClampedIntegerValue value = (ClampedIntegerValue) v;
                JsonObject jsonObject = new JsonObject();

                if (value.getSource() != null) {
                    JsonObject entry = value.getSource().getValueType().getParser().toJson(value.getSource()).getAsJsonObject();
                    entry.addProperty("source_type", value.getSource().getValueType().getKey().toString());
                    jsonObject.add("source", entry);
                }

                if (value.getMinInclusive() != null) {
                    JsonObject entry = value.getMinInclusive().getValueType().getParser().toJson(value.getMinInclusive()).getAsJsonObject();
                    entry.addProperty("min_inclusive_type", value.getMinInclusive().getValueType().getKey().toString());
                    jsonObject.add("min_inclusive", entry);
                }

                if (value.getMaxInclusive() != null) {
                    JsonObject entry = value.getMaxInclusive().getValueType().getParser().toJson(value.getMaxInclusive()).getAsJsonObject();
                    entry.addProperty("max_inclusive_type", value.getMaxInclusive().getValueType().getKey().toString());
                    jsonObject.add("max_inclusive", entry);
                }

                return jsonObject;
            }

            @Override
            public ClampedIntegerValue fromJson(JsonElement jsonElement) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();

                IntegerValue source = null;
                if (jsonObject.has("source")) {
                    JsonObject entry = jsonObject.getAsJsonObject("source");
                    source = registries.getValueTypeRegistry(IntegerType.class).get(NamespacedKey.fromString(entry.getAsJsonPrimitive("source_type").getAsString())).get().getParser().fromJson(entry);
                }

                IntegerValue minInclusive = null;
                if (jsonObject.has("min_inclusive")) {
                    JsonObject entry = jsonObject.getAsJsonObject("min_inclusive");
                    minInclusive = registries.getValueTypeRegistry(IntegerType.class).get(NamespacedKey.fromString(entry.getAsJsonPrimitive("min_inclusive_type").getAsString())).get().getParser().fromJson(entry);
                }

                IntegerValue maxInclusive = null;
                if (jsonObject.has("max_inclusive")) {
                    JsonObject entry = jsonObject.getAsJsonObject("max_inclusive");
                    maxInclusive = registries.getValueTypeRegistry(IntegerType.class).get(NamespacedKey.fromString(entry.getAsJsonPrimitive("max_inclusive_type").getAsString())).get().getParser().fromJson(entry);
                }

                return new ClampedIntegerValue(source, minInclusive, maxInclusive);
            }
        };

        type = this;
    }

    public static ClampedIntegerType type() {
        return type;
    }

    @Override
    public Parser<IntegerValue> getParser() {
        return parser;
    }

    @Override
    public ClampedIntegerValue createNewValue() {
        return new ClampedIntegerValue(new FixedDoubleToIntegerValue(0), new FixedDoubleToIntegerValue(0), new FixedDoubleToIntegerValue(1));
    }

    @NotNull
    @Override
    public NamespacedKey getKey() {
        return KEY;
    }
}
