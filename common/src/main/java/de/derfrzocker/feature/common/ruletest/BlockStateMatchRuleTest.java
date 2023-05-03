package de.derfrzocker.feature.common.ruletest;

import de.derfrzocker.feature.api.RuleTest;
import de.derfrzocker.feature.api.RuleTestType;
import de.derfrzocker.feature.api.util.traverser.message.StringFormatter;
import de.derfrzocker.feature.api.util.traverser.message.TraversKey;
import de.derfrzocker.feature.common.util.MessageTraversUtil;
import de.derfrzocker.spigot.utils.Pair;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BlockStateMatchRuleTest implements RuleTest {

    private BlockData blockData;
    private boolean dirty;

    public BlockStateMatchRuleTest(BlockData blockData) {
        this.blockData = blockData;
    }

    @Override
    public RuleTestType getType() {
        return BlockStateMatchRuleTestType.INSTANCE;
    }

    @Override
    public RuleTest clone() {
        return new BlockStateMatchRuleTest(blockData);
    }

    public BlockData getBlockData() {
        return blockData;
    }

    public void setBlockData(BlockData blockData) {
        this.blockData = blockData;
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
                new Pair<>("block-state", MessageTraversUtil.asTraversAble(getBlockData())));
    }
}
