package de.derfrzocker.feature.common.ruletest;

import de.derfrzocker.feature.api.RuleTest;
import de.derfrzocker.feature.api.RuleTestType;
import de.derfrzocker.feature.common.util.MessageTraversUtil;

import java.util.List;

public final class AlwaysTrueRuleTest implements RuleTest {

    public static final AlwaysTrueRuleTest INSTANCE = new AlwaysTrueRuleTest();

    private AlwaysTrueRuleTest() {
    }

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
    public List<String> traverse(StringFormatter formatter, int depth, String key) {
        return MessageTraversUtil.single(formatter, depth, "always-true", null);
    }
}
