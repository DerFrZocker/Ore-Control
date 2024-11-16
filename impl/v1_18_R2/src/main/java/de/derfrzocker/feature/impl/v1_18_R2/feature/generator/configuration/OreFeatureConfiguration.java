package de.derfrzocker.feature.impl.v1_18_R2.feature.generator.configuration;

import de.derfrzocker.feature.api.FeatureGenerator;
import de.derfrzocker.feature.api.FeatureGeneratorConfiguration;
import de.derfrzocker.feature.api.Setting;
import de.derfrzocker.feature.api.Value;
import de.derfrzocker.feature.common.value.number.FloatType;
import de.derfrzocker.feature.common.value.number.FloatValue;
import de.derfrzocker.feature.common.value.number.IntegerType;
import de.derfrzocker.feature.common.value.number.IntegerValue;
import de.derfrzocker.feature.impl.v1_18_R2.value.target.TargetValue;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class OreFeatureConfiguration implements FeatureGeneratorConfiguration {

    public final static Set<Setting> SETTINGS;
    private final static Setting SIZE = new Setting("size", IntegerType.class);
    private final static Setting DISCARD_CHANCE_ON_AIR_EXPOSURE = new Setting("discard-chance-on-air-exposure", FloatType.class);

    static {
        Set<Setting> settings = new LinkedHashSet<>();
        settings.add(SIZE);
        settings.add(DISCARD_CHANCE_ON_AIR_EXPOSURE);
        SETTINGS = Collections.unmodifiableSet(settings);
    }

    private final FeatureGenerator<?> featureGenerator;
    private final List<TargetValue> targets;
    private IntegerValue size;
    private FloatValue discardChanceOnAirExposure;
    private boolean dirty = false;

    public OreFeatureConfiguration(FeatureGenerator<?> featureGenerator, List<TargetValue> targets, IntegerValue size, FloatValue discardChanceOnAirExposure) {
        this.featureGenerator = featureGenerator;
        this.targets = targets;
        this.size = size;
        this.discardChanceOnAirExposure = discardChanceOnAirExposure;
    }

    public List<TargetValue> getTargets() {
        return targets;
    }

    public IntegerValue getSize() {
        return size;
    }

    public FloatValue getDiscardChanceOnAirExposure() {
        return discardChanceOnAirExposure;
    }

    @NotNull
    @Override
    public FeatureGenerator<?> getOwner() {
        return featureGenerator;
    }

    @NotNull
    @Override
    public Set<Setting> getSettings() {
        return SETTINGS;
    }

    @Override
    public Value<?, ?, ?> getValue(@NotNull Setting setting) {
        if (setting == SIZE) {
            return getSize();
        }

        if (setting == DISCARD_CHANCE_ON_AIR_EXPOSURE) {
            return getDiscardChanceOnAirExposure();
        }

        throw new IllegalArgumentException(String.format("Setting '%s' is not in the configuration '%s'", setting, "OreFeatureConfiguration"));
    }

    @Override
    public void setValue(@NotNull Setting setting, Value<?, ?, ?> value) {
        if (setting == SIZE) {
            size = (IntegerValue) value;
            dirty = true;
            return;
        }

        if (setting == DISCARD_CHANCE_ON_AIR_EXPOSURE) {
            discardChanceOnAirExposure = (FloatValue) value;
            dirty = true;
            return;
        }

        throw new IllegalArgumentException(String.format("Setting '%s' is not in the configuration '%s'", setting, "OreFeatureConfiguration"));
    }

    @Override
    public boolean isDirty() {
        if (dirty) {
            return true;
        }

        if (size != null && size.isDirty()) {
            return true;
        }

        return discardChanceOnAirExposure != null && discardChanceOnAirExposure.isDirty();
    }

    @Override
    public void saved() {
        dirty = false;

        if (size != null) {
            size.saved();
        }

        if (discardChanceOnAirExposure != null) {
            discardChanceOnAirExposure.saved();
        }
    }
}
