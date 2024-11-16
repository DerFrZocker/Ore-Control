package de.derfrzocker.feature.common.feature.placement.configuration;

import de.derfrzocker.feature.api.FeaturePlacementModifier;
import de.derfrzocker.feature.api.PlacementModifierConfiguration;
import de.derfrzocker.feature.api.Setting;
import de.derfrzocker.feature.api.Value;
import de.derfrzocker.feature.common.value.number.IntegerType;
import de.derfrzocker.feature.common.value.number.IntegerValue;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class SurfaceWaterDepthModifierConfiguration implements PlacementModifierConfiguration {

    public final static Set<Setting> SETTINGS;
    private final static Setting MAX_WATER_DEPTH = new Setting("max-water-depth", IntegerType.class);

    static {
        Set<Setting> settings = new LinkedHashSet<>();
        settings.add(MAX_WATER_DEPTH);
        SETTINGS = Collections.unmodifiableSet(settings);
    }

    private final FeaturePlacementModifier<?> placementModifier;
    private IntegerValue maxWaterDepth;
    private boolean dirty = false;

    public SurfaceWaterDepthModifierConfiguration(FeaturePlacementModifier<?> placementModifier, IntegerValue maxWaterDepth) {
        this.placementModifier = placementModifier;
        this.maxWaterDepth = maxWaterDepth;
    }

    public IntegerValue getMaxWaterDepth() {
        return maxWaterDepth;
    }

    @NotNull
    @Override
    public FeaturePlacementModifier<?> getOwner() {
        return placementModifier;
    }

    @NotNull
    @Override
    public Set<Setting> getSettings() {
        return SETTINGS;
    }

    @Override
    public Value<?, ?, ?> getValue(@NotNull Setting setting) {
        if (setting == MAX_WATER_DEPTH) {
            return getMaxWaterDepth();
        }

        throw new IllegalArgumentException(String.format("Setting '%s' is not in the configuration '%s'", setting, "SurfaceWaterDepthModifierConfiguration"));
    }

    @Override
    public void setValue(@NotNull Setting setting, Value<?, ?, ?> value) {
        if (setting == MAX_WATER_DEPTH) {
            maxWaterDepth = (IntegerValue) value;
            dirty = true;
            return;
        }

        throw new IllegalArgumentException(String.format("Setting '%s' is not in the configuration '%s'", setting, "SurfaceWaterDepthModifierConfiguration"));
    }

    @Override
    public boolean isDirty() {
        if (dirty) {
            return true;
        }

        return maxWaterDepth != null && maxWaterDepth.isDirty();
    }

    @Override
    public void saved() {
        dirty = false;

        if (maxWaterDepth != null) {
            maxWaterDepth.saved();
        }
    }
}
