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

public class BlockMatchRuleTestType implements RuleTestType {

    public static final BlockMatchRuleTestType INSTANCE = new BlockMatchRuleTestType();
    private static final NamespacedKey KEY = NamespacedKey.minecraft("block_match");
    private static final Parser<RuleTest> PARSER = new Parser<>() {
        @Override
        public JsonElement toJson(RuleTest v) {
            BlockMatchRuleTest value = (BlockMatchRuleTest) v;
            JsonObject jsonObject = new JsonObject();

            jsonObject.addProperty("value", value.getBlock().getKey().toString());

            return jsonObject;
        }

        @Override
        public BlockMatchRuleTest fromJson(JsonElement jsonElement) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();

            Material material = Registry.MATERIAL.get(NamespacedKey.fromString(jsonObject.getAsJsonPrimitive("value").getAsString()));

            return new BlockMatchRuleTest(material);
        }
    };

    private BlockMatchRuleTestType() {
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
