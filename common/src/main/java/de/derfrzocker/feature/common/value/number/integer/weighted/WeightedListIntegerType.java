package de.derfrzocker.feature.common.value.number.integer.weighted;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.derfrzocker.feature.api.Registries;
import de.derfrzocker.feature.common.value.number.IntegerType;
import de.derfrzocker.feature.common.value.number.IntegerValue;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class WeightedListIntegerType extends IntegerType {

    public static final NamespacedKey KEY = NamespacedKey.fromString("feature:weighted_list_integer");
    private static WeightedListIntegerType type = null;
    private final Codec<WeightedListIntegerValue> codec;

    public WeightedListIntegerType(Registries registries) {
        if (type != null) {
            throw new IllegalStateException("WeightedListIntegerType was already created!");
        }

        codec = RecordCodecBuilder.create((builder) -> builder.group(
                Codec.list(Codec.mapPair(
                        registries.getValueTypeRegistry(IntegerType.class).dispatchMap("data_type", IntegerValue::getValueType, IntegerType::getCodec).fieldOf("data"),
                        registries.getValueTypeRegistry(IntegerType.class).dispatchMap("weight_type", IntegerValue::getValueType, IntegerType::getCodec).fieldOf("weight")).codec()
                ).optionalFieldOf("distribution").forGetter(config -> {
                    List<Pair<IntegerValue, IntegerValue>> list = new ArrayList<>();
                    for (Map.Entry<IntegerValue, IntegerValue> entry : config.getDistribution().entrySet()) {
                        list.add(new Pair<>(entry.getKey(), entry.getValue()));
                    }
                    if (list.isEmpty()) {
                        return Optional.empty();
                    }
                    return Optional.of(list);
                })
        ).apply(builder, distribution -> {
            if (distribution.isEmpty()) {
                return new WeightedListIntegerValue(null);
            }
            Map<IntegerValue, IntegerValue> values = new LinkedHashMap<>();
            for (Pair<IntegerValue, IntegerValue> value : distribution.get()) {
                values.put(value.getFirst(), value.getSecond());
            }
            return new WeightedListIntegerValue(values);
        }));

        type = this;
    }

    public static WeightedListIntegerType type() {
        return type;
    }

    @Override
    public Codec<IntegerValue> getCodec() {
        return codec.xmap(value -> value, value -> (WeightedListIntegerValue) value);
    }

    @Override
    public Class<Integer> getTypeClass() {
        return Integer.class;
    }

    @NotNull
    @Override
    public NamespacedKey getKey() {
        return KEY;
    }
}
