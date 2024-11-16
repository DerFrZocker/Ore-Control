package de.derfrzocker.feature.common.value.number.integer.trapezoid;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.derfrzocker.feature.api.Registries;
import de.derfrzocker.feature.api.util.Parser;
import de.derfrzocker.feature.common.value.number.IntegerType;
import de.derfrzocker.feature.common.value.number.IntegerValue;
import de.derfrzocker.feature.common.value.number.integer.FixedDoubleToIntegerValue;
import de.derfrzocker.feature.common.value.number.integer.clamped.ClampedIntegerValue;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class TrapezoidIntegerType extends IntegerType {

    public static final NamespacedKey KEY = NamespacedKey.fromString("feature:trapezoid_integer");
    private static TrapezoidIntegerType type = null;
    private final Parser<IntegerValue> parser;

    public TrapezoidIntegerType(Registries registries) {
        if (type != null) {
            throw new IllegalStateException("TrapezoidIntegerType was already created!");
        }

        parser = new Parser<>() {
            @Override
            public JsonElement toJson(IntegerValue v) {
                TrapezoidIntegerValue value = (TrapezoidIntegerValue) v;
                JsonObject jsonObject = new JsonObject();

                if (value.getPlateau() != null) {
                    JsonObject entry = value.getPlateau().getValueType().getParser().toJson(value.getPlateau()).getAsJsonObject();
                    entry.addProperty("plateau_type", value.getPlateau().getValueType().getKey().toString());
                    jsonObject.add("plateau", entry);
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
            public TrapezoidIntegerValue fromJson(JsonElement jsonElement) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();

                IntegerValue plateau = null;
                if (jsonObject.has("plateau")) {
                    JsonObject entry = jsonObject.getAsJsonObject("plateau");
                    plateau = registries.getValueTypeRegistry(IntegerType.class).get(NamespacedKey.fromString(entry.getAsJsonPrimitive("plateau_type").getAsString())).get().getParser().fromJson(entry);
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

                return new TrapezoidIntegerValue(plateau, minInclusive, maxInclusive);
            }
        };

        type = this;
    }

    public static TrapezoidIntegerType type() {
        return type;
    }

    @Override
    public Parser<IntegerValue> getParser() {
        return parser;
    }

    @Override
    public TrapezoidIntegerValue createNewValue() {
        return new TrapezoidIntegerValue(new FixedDoubleToIntegerValue(0), new FixedDoubleToIntegerValue(1), new FixedDoubleToIntegerValue(0));
    }

    @NotNull
    @Override
    public NamespacedKey getKey() {
        return KEY;
    }
}
