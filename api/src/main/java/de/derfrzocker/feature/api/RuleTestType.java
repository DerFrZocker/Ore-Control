package de.derfrzocker.feature.api;

import de.derfrzocker.feature.api.util.Parser;
import org.bukkit.Keyed;
import org.jetbrains.annotations.NotNull;

public interface RuleTestType extends Keyed {

    @NotNull
    Parser<RuleTest> getParser();
}
