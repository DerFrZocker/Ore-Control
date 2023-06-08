package de.derfrzocker.feature.common.ruletest;

import de.derfrzocker.feature.api.RuleTest;
import de.derfrzocker.feature.api.RuleTestType;
import de.derfrzocker.feature.api.ValueLocation;
import de.derfrzocker.feature.api.util.traverser.message.StringFormatter;
import de.derfrzocker.feature.api.util.traverser.message.TraversKey;
import de.derfrzocker.feature.common.util.MessageTraversUtil;
import de.derfrzocker.spigot.utils.Pair;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TagMatchRuleTest implements RuleTest {

    private NamespacedKey tag;
    private boolean dirty;
    private ValueLocation valueLocation = ValueLocation.UNKNOWN;

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
    public @NotNull List<@NotNull String> traverse(@NotNull StringFormatter formatter, int depth, @NotNull TraversKey key) {
        return MessageTraversUtil.multiple(formatter, depth, key, TraversKey.ofRuleTest(getType().getKey()),
                new Pair<>("tag", MessageTraversUtil.asTraversAble(getTag())));
    }

    @Override
    public @NotNull ValueLocation getValueLocation() {
        return valueLocation;
    }

    @Override
    public void setValueLocation(@NotNull ValueLocation valueLocation) {
        this.valueLocation = valueLocation;
    }
}
