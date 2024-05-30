package de.derfrzocker.feature.common.value.target;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.derfrzocker.feature.api.LocatedAble;
import de.derfrzocker.feature.api.Registries;
import de.derfrzocker.feature.api.RuleTest;
import de.derfrzocker.feature.api.ValueLocation;
import de.derfrzocker.feature.api.util.Parser;
import de.derfrzocker.feature.api.util.traverser.message.MessageTraversAble;
import de.derfrzocker.feature.api.util.SaveAble;
import de.derfrzocker.feature.api.util.traverser.message.StringFormatter;
import de.derfrzocker.feature.api.util.traverser.message.TraversKey;
import de.derfrzocker.feature.common.util.MessageTraversUtil;
import de.derfrzocker.spigot.utils.Pair;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TargetBlockState implements MessageTraversAble, SaveAble, Cloneable, LocatedAble {

    private boolean dirty = false;
    private RuleTest target;
    private BlockData state;
    private ValueLocation valueLocation = ValueLocation.UNKNOWN;

    public TargetBlockState(RuleTest target, BlockData state) {
        this.target = target;
        this.state = state;
    }

    static Parser<TargetBlockState> createParser(Registries registries) {
        return new Parser<>() {
            @Override
            public JsonElement toJson(TargetBlockState value) {
                JsonObject jsonObject = new JsonObject();

                if (value.getTarget() != null) {
                    JsonObject entry = value.getTarget().getType().getParser().toJson(value.getTarget()).getAsJsonObject();
                    entry.addProperty("rule_test_key", value.getTarget().getType().getKey().toString());
                    jsonObject.add("rule_test", entry);
                }

                if (value.getState() != null) {
                    jsonObject.addProperty("block_data", value.getState().getAsString());
                }

                return jsonObject;
            }

            @Override
            public TargetBlockState fromJson(JsonElement jsonElement) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();

                RuleTest ruleTest = null;
                if (jsonObject.has("rule_test")) {
                    JsonObject entry = jsonObject.getAsJsonObject("rule_test");
                    ruleTest  = registries.getRuleTestTypeRegistry().get(NamespacedKey.fromString(entry.getAsJsonPrimitive("rule_test_key").getAsString())).get().getParser().fromJson(entry);
                }

                BlockData blockData = null;
                if (jsonObject.has("block_data")) {
                    blockData = Bukkit.createBlockData(jsonObject.getAsJsonPrimitive("block_data").getAsString());
                }

                return new TargetBlockState(ruleTest, blockData);
            }
        };
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

    @Override
    public @NotNull ValueLocation getValueLocation() {
        return valueLocation;
    }

    @Override
    public void setValueLocation(@NotNull ValueLocation valueLocation) {
        this.valueLocation = valueLocation;

        if (target != null) {
            target.setValueLocation(valueLocation);
        }
    }
}
