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
    public @NotNull List<@NotNull String> traverse(@NotNull StringFormatter formatter, int depth, @NotNull TraversKey key) {
        return MessageTraversUtil.multiple(formatter, depth, key, TraversKey.ofRuleTest(getType().getKey()),
                new Pair<>("block", MessageTraversUtil.asTraversAble(getMaterial())));
    }
}
