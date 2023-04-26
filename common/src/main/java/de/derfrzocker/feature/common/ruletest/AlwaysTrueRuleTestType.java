package de.derfrzocker.feature.common.ruletest;

import com.mojang.serialization.Codec;
import de.derfrzocker.feature.api.RuleTest;
import de.derfrzocker.feature.api.RuleTestType;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class AlwaysTrueRuleTestType implements RuleTestType {

    public static final AlwaysTrueRuleTestType INSTANCE = new AlwaysTrueRuleTestType();
    private static final NamespacedKey KEY = NamespacedKey.minecraft("always_true");
    private static final Codec<AlwaysTrueRuleTest> CODEC = Codec.unit(() -> AlwaysTrueRuleTest.INSTANCE);

    private AlwaysTrueRuleTestType() {
    }

    @Override
    public @NotNull Codec<RuleTest> getCodec() {
        return CODEC.xmap(Function.identity(), value -> (AlwaysTrueRuleTest) value);
    }

    @NotNull
    @Override
    public NamespacedKey getKey() {
        return KEY;
    }
}
