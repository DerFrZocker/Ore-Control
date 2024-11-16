package de.derfrzocker.feature.common.feature.generator.configuration;

import de.derfrzocker.feature.api.FeatureGenerator;
import de.derfrzocker.feature.api.FeatureGeneratorConfiguration;
import de.derfrzocker.feature.api.Setting;
import de.derfrzocker.feature.api.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Set;

public class EmptyFeatureConfiguration implements FeatureGeneratorConfiguration {

    private final FeatureGenerator<?> featureGenerator;

    public EmptyFeatureConfiguration(FeatureGenerator<?> featureGenerator) {
        this.featureGenerator = featureGenerator;
    }

    @Override
    public @NotNull Set<Setting> getSettings() {
        return Collections.emptySet();
    }

    @Override
    public @Nullable Value<?, ?, ?> getValue(@NotNull Setting setting) {
        return null;
    }

    @Override
    public void setValue(@NotNull Setting setting, @Nullable Value<?, ?, ?> value) {

    }

    @Override
    public boolean isDirty() {
        return false;
    }

    @Override
    public void saved() {

    }

    @Override
    public @NotNull FeatureGenerator<?> getOwner() {
        return featureGenerator;
    }
}
