package de.derfrzocker.feature.common.ruletest;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.derfrzocker.feature.api.RuleTest;
import de.derfrzocker.feature.api.RuleTestType;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class RandomBlockStateMatchRuleTestType implements RuleTestType {
    public static final RandomBlockStateMatchRuleTestType INSTANCE = new RandomBlockStateMatchRuleTestType();
    private static final NamespacedKey KEY = NamespacedKey.minecraft("random_blockstate_match");
    private static final Codec<RandomBlockStateMatchRuleTest> CODEC = RecordCodecBuilder.create(builder -> {
        return builder.group(Codec.STRING
                                .xmap(Bukkit::createBlockData, BlockData::getAsString)
                                .fieldOf("block_state")
                                .forGetter(RandomBlockStateMatchRuleTest::getBlockData),
                        Codec.FLOAT.fieldOf("probability")
                                .forGetter(RandomBlockStateMatchRuleTest::getProbability))
                .apply(builder, RandomBlockStateMatchRuleTest::new);
    });

    private RandomBlockStateMatchRuleTestType() {
    }

    @Override
    public @NotNull Codec<RuleTest> getCodec() {
        return CODEC.xmap(Function.identity(), value -> (RandomBlockStateMatchRuleTest) value);
    }

    @NotNull
    @Override
    public NamespacedKey getKey() {
        return KEY;
    }
}
