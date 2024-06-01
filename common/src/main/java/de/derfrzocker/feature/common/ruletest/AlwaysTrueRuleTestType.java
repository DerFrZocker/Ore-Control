package de.derfrzocker.feature.common.ruletest;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.derfrzocker.feature.api.RuleTest;
import de.derfrzocker.feature.api.RuleTestType;
import de.derfrzocker.feature.api.util.Parser;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

public class AlwaysTrueRuleTestType implements RuleTestType {

    public static final AlwaysTrueRuleTestType INSTANCE = new AlwaysTrueRuleTestType();
    private static final NamespacedKey KEY = NamespacedKey.minecraft("always_true");
    private static final Parser<RuleTest> PARSER = new Parser<>() {
        @Override
        public JsonElement toJson(RuleTest value) {
            return new JsonObject();
        }

        @Override
        public AlwaysTrueRuleTest fromJson(JsonElement jsonElement) {
            return new AlwaysTrueRuleTest();
        }
    };

    private AlwaysTrueRuleTestType() {
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
