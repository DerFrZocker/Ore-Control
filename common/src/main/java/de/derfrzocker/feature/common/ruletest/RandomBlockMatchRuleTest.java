package de.derfrzocker.feature.common.ruletest;

import de.derfrzocker.feature.api.RuleTest;
import de.derfrzocker.feature.api.RuleTestType;
import de.derfrzocker.feature.common.util.MessageTraversUtil;
import org.bukkit.Material;

import java.util.List;

public class RandomBlockMatchRuleTest implements RuleTest {
    private Material material;
    private float probability;
    private boolean dirty;

    public RandomBlockMatchRuleTest(Material material, float probability) {
        this.material = material;
        this.probability = probability;
    }

    @Override
    public RuleTestType getType() {
        return RandomBlockMatchRuleTestType.INSTANCE;
    }

    @Override
    public RuleTest clone() {
        return new RandomBlockMatchRuleTest(material, probability);
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
        dirty = true;
    }

    public float getProbability() {
        return probability;
    }

    public void setProbability(float probability) {
        this.probability = probability;
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
