package de.derfrzocker.feature.common.ruletest;

import de.derfrzocker.feature.api.RuleTest;
import de.derfrzocker.feature.api.RuleTestType;
import de.derfrzocker.feature.common.util.MessageTraversUtil;
import org.bukkit.Material;

import java.util.List;

public class BlockMatchRuleTest implements RuleTest {

    private Material material;
    private boolean dirty;

    public BlockMatchRuleTest(Material material) {
        this.material = material;
    }

    @Override
    public RuleTestType getType() {
        return BlockMatchRuleTestType.INSTANCE;
    }

    @Override
    public RuleTest clone() {
        return new BlockMatchRuleTest(material);
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
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
        return MessageTraversUtil.single(formatter, depth, "block-match", getMaterial());
    }
}
