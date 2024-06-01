package de.derfrzocker.feature.common.ruletest;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.derfrzocker.feature.api.RuleTest;
import de.derfrzocker.feature.api.RuleTestType;
import de.derfrzocker.feature.api.util.Parser;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.jetbrains.annotations.NotNull;

public class RandomBlockMatchRuleTestType implements RuleTestType {
    public static final RandomBlockMatchRuleTestType INSTANCE = new RandomBlockMatchRuleTestType();
    private static final NamespacedKey KEY = NamespacedKey.minecraft("random_block_match");
    private static final Parser<RuleTest> PARSER = new Parser<>() {
        @Override
        public JsonElement toJson(RuleTest v) {
            RandomBlockMatchRuleTest value = (RandomBlockMatchRuleTest) v;
            JsonObject jsonObject = new JsonObject();

            jsonObject.addProperty("block", value.getMaterial().getKey().toString());
            jsonObject.addProperty("probability", value.getProbability());

            return jsonObject;
        }

        @Override
        public RandomBlockMatchRuleTest fromJson(JsonElement jsonElement) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();

            Material material = Registry.MATERIAL.get(NamespacedKey.fromString(jsonObject.get("block").getAsString()));
            float probability = jsonObject.get("probability").getAsFloat();

            return new RandomBlockMatchRuleTest(material, probability);
        }
    };

    private RandomBlockMatchRuleTestType() {
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
