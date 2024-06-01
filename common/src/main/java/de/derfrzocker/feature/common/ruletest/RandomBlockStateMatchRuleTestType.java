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

public class RandomBlockStateMatchRuleTestType implements RuleTestType {
    public static final RandomBlockStateMatchRuleTestType INSTANCE = new RandomBlockStateMatchRuleTestType();
    private static final NamespacedKey KEY = NamespacedKey.minecraft("random_blockstate_match");
    private static final Parser<RuleTest> PARSER = new Parser<>() {
        @Override
        public JsonElement toJson(RuleTest v) {
            RandomBlockStateMatchRuleTest value = (RandomBlockStateMatchRuleTest) v;
            JsonObject jsonObject = new JsonObject();

            jsonObject.addProperty("block_state", value.getBlockData().getAsString());
            jsonObject.addProperty("probability", value.getProbability());

            return jsonObject;
        }

        @Override
        public RandomBlockStateMatchRuleTest fromJson(JsonElement jsonElement) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();

            BlockData blockData = Bukkit.createBlockData(jsonObject.get("block_state").getAsString());
            float probability = jsonObject.get("probability").getAsFloat();

            return new RandomBlockStateMatchRuleTest(blockData, probability);
        }
    };

    private RandomBlockStateMatchRuleTestType() {
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
