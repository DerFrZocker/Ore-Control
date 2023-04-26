package de.derfrzocker.feature.common.ruletest;

import com.mojang.serialization.Codec;
import de.derfrzocker.feature.api.RuleTest;
import de.derfrzocker.feature.api.RuleTestType;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class TagMatchRuleTestType implements RuleTestType {
    public static final TagMatchRuleTestType INSTANCE = new TagMatchRuleTestType();
    private static final NamespacedKey KEY = NamespacedKey.minecraft("tag_match");
    private static final Codec<TagMatchRuleTest> CODEC = Codec.STRING
            .xmap(NamespacedKey::fromString, NamespacedKey::toString)
            .xmap(TagMatchRuleTest::new, TagMatchRuleTest::getTag);

    private TagMatchRuleTestType() {
    }

    @Override
    public @NotNull Codec<RuleTest> getCodec() {
        return CODEC.xmap(Function.identity(), value -> (TagMatchRuleTest) value);
    }

    @NotNull
    @Override
    public NamespacedKey getKey() {
        return KEY;
    }
}
