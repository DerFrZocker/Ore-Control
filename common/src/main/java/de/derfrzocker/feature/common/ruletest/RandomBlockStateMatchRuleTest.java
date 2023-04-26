package de.derfrzocker.feature.common.ruletest;

import de.derfrzocker.feature.api.RuleTest;
import de.derfrzocker.feature.api.RuleTestType;
import de.derfrzocker.feature.common.util.MessageTraversUtil;
import org.bukkit.block.data.BlockData;

import java.util.List;

public class RandomBlockStateMatchRuleTest implements RuleTest {
    private BlockData blockData;
    private float probability;
    private boolean dirty;

    public RandomBlockStateMatchRuleTest(BlockData blockData, float probability) {
        this.blockData = blockData;
        this.probability = probability;
    }

    @Override
    public RuleTestType getType() {
        return RandomBlockStateMatchRuleTestType.INSTANCE;
    }

    @Override
    public RuleTest clone() {
        return new RandomBlockStateMatchRuleTest(blockData, probability);
    }

    public BlockData getBlockData() {
        return blockData;
    }

    public void setBlockData(BlockData blockData) {
        this.blockData = blockData;
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
        return MessageTraversUtil.single(formatter, depth, "block-match", getBlockData());
    }
}
