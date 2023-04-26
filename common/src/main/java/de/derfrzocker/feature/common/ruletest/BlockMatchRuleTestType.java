package de.derfrzocker.feature.common.ruletest;

import com.mojang.serialization.Codec;
import de.derfrzocker.feature.api.RuleTest;
import de.derfrzocker.feature.api.RuleTestType;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class BlockMatchRuleTestType implements RuleTestType {

    public static final BlockMatchRuleTestType INSTANCE = new BlockMatchRuleTestType();
    private static final NamespacedKey KEY = NamespacedKey.minecraft("block_match");
    private static final Codec<BlockMatchRuleTest> CODEC = Codec.STRING
            .xmap(NamespacedKey::fromString, NamespacedKey::toString)
            .xmap(Registry.MATERIAL::get, Material::getKey)
            .xmap(BlockMatchRuleTest::new, BlockMatchRuleTest::getMaterial);

    private BlockMatchRuleTestType() {
    }

    @Override
    public @NotNull Codec<RuleTest> getCodec() {
        return CODEC.xmap(Function.identity(), value -> (BlockMatchRuleTest) value);
    }

    @NotNull
    @Override
    public NamespacedKey getKey() {
        return KEY;
    }
}
