package de.derfrzocker.feature.common.ruletest;

import de.derfrzocker.feature.api.RuleTest;
import de.derfrzocker.feature.api.RuleTestType;
import de.derfrzocker.feature.api.util.traverser.message.StringFormatter;
import de.derfrzocker.feature.api.util.traverser.message.TraversKey;
import de.derfrzocker.feature.common.util.MessageTraversUtil;
import de.derfrzocker.spigot.utils.Pair;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

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
    public @NotNull List<@NotNull String> traverse(@NotNull StringFormatter formatter, int depth, @NotNull TraversKey key) {
        return MessageTraversUtil.multiple(formatter, depth, key, TraversKey.ofValueSetting("block-match"),
                new Pair<>("material", MessageTraversUtil.asTraversAble(getMaterial())));
    }
}
