package de.derfrzocker.feature.common.ruletest;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.derfrzocker.feature.api.RuleTest;
import de.derfrzocker.feature.api.RuleTestType;
import de.derfrzocker.feature.api.util.Parser;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

public class TagMatchRuleTestType implements RuleTestType {
    public static final TagMatchRuleTestType INSTANCE = new TagMatchRuleTestType();
    private static final NamespacedKey KEY = NamespacedKey.minecraft("tag_match");
    private static final Parser<RuleTest> PARSER = new Parser<>() {
        @Override
        public JsonElement toJson(RuleTest v) {
            TagMatchRuleTest value = (TagMatchRuleTest) v;
            JsonObject jsonObject = new JsonObject();

            jsonObject.addProperty("value", value.getTag().toString());

            return jsonObject;
        }

        @Override
        public RuleTest fromJson(JsonElement jsonElement) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();

            NamespacedKey tag = NamespacedKey.fromString(jsonObject.get("value").getAsString());

            return new TagMatchRuleTest(tag);
        }
    };

    private TagMatchRuleTestType() {
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
