package de.derfrzocker.feature.common.ruletest;

import com.mojang.serialization.Codec;
import de.derfrzocker.feature.api.RuleTest;
import de.derfrzocker.feature.api.RuleTestType;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class BlockStateMatchRuleTestType implements RuleTestType {
    public static final BlockStateMatchRuleTestType INSTANCE = new BlockStateMatchRuleTestType();
    private static final NamespacedKey KEY = NamespacedKey.minecraft("blockstate_match");
    private static final Codec<BlockStateMatchRuleTest> CODEC = Codec.STRING
            .xmap(Bukkit::createBlockData, BlockData::getAsString)
            .xmap(BlockStateMatchRuleTest::new, BlockStateMatchRuleTest::getBlockData);

    private BlockStateMatchRuleTestType() {
    }

    @Override
    public @NotNull Codec<RuleTest> getCodec() {
        return CODEC.xmap(Function.identity(), value -> (BlockStateMatchRuleTest) value);
    }

    @NotNull
    @Override
    public NamespacedKey getKey() {
        return KEY;
    }
}
