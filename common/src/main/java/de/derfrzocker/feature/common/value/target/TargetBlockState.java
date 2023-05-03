package de.derfrzocker.feature.common.value.target;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.derfrzocker.feature.api.Registries;
import de.derfrzocker.feature.api.RuleTest;
import de.derfrzocker.feature.api.RuleTestType;
import de.derfrzocker.feature.api.util.traverser.message.MessageTraversAble;
import de.derfrzocker.feature.api.util.SaveAble;
import de.derfrzocker.feature.api.util.traverser.message.StringFormatter;
import de.derfrzocker.feature.api.util.traverser.message.TraversKey;
import de.derfrzocker.feature.common.util.MessageTraversUtil;
import de.derfrzocker.spigot.utils.Pair;
import org.bukkit.Bukkit;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TargetBlockState implements MessageTraversAble, SaveAble, Cloneable {

    private boolean dirty = false;
    private RuleTest target;
    private BlockData state;

    public TargetBlockState(RuleTest target, BlockData state) {
        this.target = target;
        this.state = state;
    }

    static Codec<TargetBlockState> createCodec(Registries registries) {
        return RecordCodecBuilder.create((builder) -> builder.group(
                registries.getRuleTestTypeRegistry().dispatch("rule_test_key", RuleTest::getType, RuleTestType::getCodec)
                        .fieldOf("rule_test")
                        .forGetter(TargetBlockState::getTarget),
                Codec.STRING
                        .xmap(Bukkit::createBlockData, BlockData::getAsString)
                        .fieldOf("block_data")
                        .forGetter(TargetBlockState::getState)
        ).apply(builder, TargetBlockState::new));
    }

    public RuleTest getTarget() {
        return target;
    }

    public void setTarget(RuleTest target) {
        this.target = target;
        dirty = true;
    }

    public BlockData getState() {
        return state;
    }

    public void setState(BlockData state) {
        this.state = state;
        dirty = true;
    }

    @Override
    public boolean isDirty() {
        if (dirty) {
            return true;
        }

        return target != null && target.isDirty();
    }

    @Override
    public void saved() {
        dirty = false;

        if (target != null) {
            target.saved();
        }
    }

    @Override
    public TargetBlockState clone() {
        return new TargetBlockState(target == null ? null : target.clone(), state);
    }

    @Override
    public @NotNull List<@NotNull String> traverse(@NotNull StringFormatter formatter, int depth, @NotNull TraversKey key) {
        return MessageTraversUtil.multiple(formatter, depth, key, TraversKey.ofValueSetting("target-block-state"),
        new Pair<>("state", MessageTraversUtil.asTraversAble(getState())), new Pair<>("target", getTarget()));
    }
}
