package de.derfrzocker.feature.common.value.target;

import com.mojang.datafixers.kinds.Traversable;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.derfrzocker.feature.api.Registries;
import de.derfrzocker.feature.api.RuleTest;
import de.derfrzocker.feature.api.RuleTestType;
import de.derfrzocker.feature.api.util.MessageTraversAble;
import de.derfrzocker.feature.api.util.SaveAble;
import de.derfrzocker.feature.common.util.MessageTraversUtil;
import de.derfrzocker.spigot.utils.Pair;
import org.bukkit.Bukkit;
import org.bukkit.block.data.BlockData;

import java.util.ArrayList;
import java.util.List;

public class TargetBlockState implements MessageTraversAble, SaveAble, Cloneable {

    private boolean dirty = false;
    private RuleTest ruleTest;
    private BlockData blockData;

    public TargetBlockState(RuleTest ruleTest, BlockData blockData) {
        this.ruleTest = ruleTest;
        this.blockData = blockData;
    }

    static Codec<TargetBlockState> createCodec(Registries registries) {
        return RecordCodecBuilder.create((builder) -> builder.group(
                registries.getRuleTestTypeRegistry().dispatch("rule_test_key", RuleTest::getType, RuleTestType::getCodec)
                        .fieldOf("rule_test")
                        .forGetter(TargetBlockState::getRuleTest),
                Codec.STRING
                        .xmap(Bukkit::createBlockData, BlockData::getAsString)
                        .fieldOf("block_data")
                        .forGetter(TargetBlockState::getBlockData)
        ).apply(builder, TargetBlockState::new));
    }

    public RuleTest getRuleTest() {
        return ruleTest;
    }

    public void setRuleTest(RuleTest ruleTest) {
        this.ruleTest = ruleTest;
        dirty = true;
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
        if (dirty) {
            return true;
        }

        return ruleTest != null && ruleTest.isDirty();
    }

    @Override
    public void saved() {
        dirty = false;

        if (ruleTest != null) {
            ruleTest.saved();
        }
    }

    @Override
    public TargetBlockState clone() {
        return new TargetBlockState(ruleTest == null ? null : ruleTest.clone(), blockData);
    }

    @Override
    public List<String> traverse(StringFormatter formatter, int depth, String key) {
        List<String> result = new ArrayList<>();

        result.addAll(MessageTraversUtil.single(formatter, depth + 1, "block-data", blockData));
        result.addAll(MessageTraversUtil.multiple(formatter, depth, key, new Pair<>("rule-test", getRuleTest())));

        return result;
    }
}
