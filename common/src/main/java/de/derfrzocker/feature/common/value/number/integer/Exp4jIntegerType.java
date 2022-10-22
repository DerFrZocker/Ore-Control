package de.derfrzocker.feature.common.value.number.integer;

import com.mojang.serialization.Codec;
import de.derfrzocker.feature.common.value.number.IntegerType;
import de.derfrzocker.feature.common.value.number.IntegerValue;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

public class Exp4jIntegerType extends IntegerType {

    public static final NamespacedKey KEY = NamespacedKey.fromString("feature:exp4f_integer");
    public static final Exp4jIntegerType INSTANCE = new Exp4jIntegerType();
    public static final Codec<Exp4jIntegerValue> CODEC = Codec.STRING.xmap(Exp4jIntegerValue::new, Exp4jIntegerValue::getExpressionString);

    private Exp4jIntegerType() {
    }

    @Override
    public Codec<IntegerValue> getCodec() {
        return CODEC.xmap(value -> value, value -> (Exp4jIntegerValue) value);
    }

    @Override
    public Class<Integer> getTypeClass() {
        return Integer.class;
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
