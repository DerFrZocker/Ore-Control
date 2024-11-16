package de.derfrzocker.feature.impl.v1_18_R2.feature.generator;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.derfrzocker.feature.api.Configuration;
import de.derfrzocker.feature.api.Registries;
import de.derfrzocker.feature.api.Setting;
import de.derfrzocker.feature.api.util.Parser;
import de.derfrzocker.feature.common.feature.generator.configuration.EmptyFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Random;
import java.util.Set;

public class GlowstoneBlobFeatureGenerator extends MinecraftFeatureGenerator<NoneFeatureConfiguration, EmptyFeatureConfiguration> {

    public GlowstoneBlobFeatureGenerator(Registries registries) {
        super(registries, Feature.GLOWSTONE_BLOB, "glowstone_blob");
    }

    @Override
    public @NotNull Set<Setting> getSettings() {
        return Collections.emptySet();
    }

    @Override
    public @NotNull Configuration createEmptyConfiguration() {
        return new EmptyFeatureConfiguration(this);
    }

    @Override
    public EmptyFeatureConfiguration mergeConfig(EmptyFeatureConfiguration first, EmptyFeatureConfiguration second) {
        return first;
    }

    @Override
    public Parser<EmptyFeatureConfiguration> createParser(Registries registries) {
        return new Parser<>() {
            @Override
            public JsonElement toJson(EmptyFeatureConfiguration value) {
                return new JsonObject();
            }

            @Override
            public EmptyFeatureConfiguration fromJson(JsonElement jsonElement) {
                return new EmptyFeatureConfiguration(GlowstoneBlobFeatureGenerator.this);
            }
        };
    }

    @Override
    public NoneFeatureConfiguration createConfiguration(@NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull BlockVector position, @NotNull LimitedRegion limitedRegion, @NotNull EmptyFeatureConfiguration configuration) {
        return NoneFeatureConfiguration.INSTANCE;
    }
}
