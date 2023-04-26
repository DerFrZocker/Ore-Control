package de.derfrzocker.feature.api;

import com.mojang.serialization.Codec;
import org.bukkit.Keyed;
import org.jetbrains.annotations.NotNull;

public interface RuleTestType extends Keyed {

    @NotNull
    Codec<RuleTest> getCodec();
}
