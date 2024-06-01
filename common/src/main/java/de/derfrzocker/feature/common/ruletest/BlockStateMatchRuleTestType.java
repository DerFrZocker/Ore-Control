package de.derfrzocker.feature.common.ruletest;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.derfrzocker.feature.api.RuleTest;
import de.derfrzocker.feature.api.RuleTestType;
import de.derfrzocker.feature.api.util.Parser;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;

public class BlockStateMatchRuleTestType implements RuleTestType {
    public static final BlockStateMatchRuleTestType INSTANCE = new BlockStateMatchRuleTestType();
    private static final NamespacedKey KEY = NamespacedKey.minecraft("blockstate_match");
    private static final Parser<RuleTest> PARSER = new Parser<>() {
        @Override
        public JsonElement toJson(RuleTest v) {
            BlockStateMatchRuleTest value = (BlockStateMatchRuleTest) v;
            JsonObject jsonObject = new JsonObject();

            jsonObject.addProperty("value", value.getBlockData().getAsString());

            return jsonObject;
        }

        @Override
        public BlockStateMatchRuleTest fromJson(JsonElement jsonElement) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();

            BlockData blockData = Bukkit.createBlockData(jsonObject.getAsJsonPrimitive("value").getAsString());

            return new BlockStateMatchRuleTest(blockData);
        }
    };

    private BlockStateMatchRuleTestType() {
    }

    @Override
    public @NotNull Parser<RuleTest> getParser() {
        return PARSER;
    }

    @NotNull
    @Override
    public NamespacedKey getKey() {
        return KEY;
    }
}
