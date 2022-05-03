package de.derfrzocker.feature.common.value.bool;

import com.mojang.serialization.Codec;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

public class FixedBooleanType extends BooleanType {

    public static final NamespacedKey KEY = NamespacedKey.fromString("feature:fixed_boolean");
    public static final FixedBooleanType INSTANCE = new FixedBooleanType();
    public static final Codec<FixedBooleanValue> CODEC = Codec.BOOL.xmap(FixedBooleanValue::new, FixedBooleanValue::getValue);

    private FixedBooleanType() {
    }

    @Override
    public Codec<BooleanValue> getCodec() {
        return CODEC.xmap(value -> value, value -> (FixedBooleanValue) value);
    }

    @Override
    public Class<Boolean> getTypeClass() {
        return Boolean.class;
    }

    @NotNull
    @Override
    public NamespacedKey getKey() {
        return KEY;
    }
}
