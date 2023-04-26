package de.derfrzocker.feature.common.ruletest;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.derfrzocker.feature.api.RuleTest;
import de.derfrzocker.feature.api.RuleTestType;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class RandomBlockMatchRuleTestType implements RuleTestType {
    public static final RandomBlockMatchRuleTestType INSTANCE = new RandomBlockMatchRuleTestType();
    private static final NamespacedKey KEY = NamespacedKey.minecraft("random_block_match");
    private static final Codec<RandomBlockMatchRuleTest> CODEC = RecordCodecBuilder.create(builder -> {
        return builder.group(Codec.STRING
                                .xmap(NamespacedKey::fromString, NamespacedKey::toString)
                                .xmap(Registry.MATERIAL::get, Material::getKey)
                                .fieldOf("block")
                                .forGetter(RandomBlockMatchRuleTest::getMaterial),
                        Codec.FLOAT.fieldOf("probability")
                                .forGetter(RandomBlockMatchRuleTest::getProbability))
                .apply(builder, RandomBlockMatchRuleTest::new);
    });

    private RandomBlockMatchRuleTestType() {
    }

    @Override
    public @NotNull Codec<RuleTest> getCodec() {
        return CODEC.xmap(Function.identity(), value -> (RandomBlockMatchRuleTest) value);
    }

    @NotNull
    @Override
    public NamespacedKey getKey() {
        return KEY;
    }
}
