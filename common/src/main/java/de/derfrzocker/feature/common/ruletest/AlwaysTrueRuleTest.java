package de.derfrzocker.feature.common.ruletest;

import de.derfrzocker.feature.api.RuleTest;
import de.derfrzocker.feature.api.RuleTestType;
import de.derfrzocker.feature.api.ValueLocation;
import de.derfrzocker.feature.api.util.traverser.message.StringFormatter;
import de.derfrzocker.feature.api.util.traverser.message.TraversKey;
import de.derfrzocker.feature.common.util.MessageTraversUtil;
import de.derfrzocker.spigot.utils.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class AlwaysTrueRuleTest implements RuleTest {

    private ValueLocation valueLocation = ValueLocation.UNKNOWN;

    @Override
    public RuleTestType getType() {
        return AlwaysTrueRuleTestType.INSTANCE;
    }

    @Override
    public RuleTest clone() {
        return this;
    }

    @Override
    public boolean isDirty() {
        return false;
    }

    @Override
    public void saved() {

    }

    @Override
    public @NotNull List<@NotNull String> traverse(@NotNull StringFormatter formatter, int depth, @NotNull TraversKey key) {
        return MessageTraversUtil.multiple(formatter, depth, key, TraversKey.ofRuleTest(getType().getKey()));
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
