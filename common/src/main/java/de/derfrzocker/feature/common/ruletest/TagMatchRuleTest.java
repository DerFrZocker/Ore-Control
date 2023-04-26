package de.derfrzocker.feature.common.ruletest;

import de.derfrzocker.feature.api.RuleTest;
import de.derfrzocker.feature.api.RuleTestType;
import de.derfrzocker.feature.common.util.MessageTraversUtil;
import org.bukkit.NamespacedKey;

import java.util.List;

public class TagMatchRuleTest implements RuleTest {

    private NamespacedKey tag;
    private boolean dirty;

    public TagMatchRuleTest(NamespacedKey tag) {
        this.tag = tag;
    }

    @Override
    public RuleTestType getType() {
        return TagMatchRuleTestType.INSTANCE;
    }

    @Override
    public RuleTest clone() {
        return new TagMatchRuleTest(tag);
    }

    public NamespacedKey getTag() {
        return tag;
    }

    public void setTag(NamespacedKey tag) {
        this.tag = tag;
        dirty = true;
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    @Override
    public void saved() {
        dirty = false;
    }

    @Override
    public List<String> traverse(StringFormatter formatter, int depth, String key) {
        return MessageTraversUtil.single(formatter, depth, "block-match", getTag());
    }
}
