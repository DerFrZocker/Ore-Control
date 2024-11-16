package de.derfrzocker.feature.impl.v1_19_R3.placement.configuration;

import de.derfrzocker.feature.api.FeaturePlacementModifier;
import de.derfrzocker.feature.api.PlacementModifierConfiguration;
import de.derfrzocker.feature.api.Setting;
import de.derfrzocker.feature.api.Value;
import de.derfrzocker.feature.common.value.number.IntegerType;
import de.derfrzocker.feature.common.value.number.IntegerValue;
import de.derfrzocker.feature.impl.v1_19_R3.value.heightmap.HeightmapValue;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class SurfaceRelativeThresholdModifierConfiguration implements PlacementModifierConfiguration {

    public final static Set<Setting> SETTINGS;
    private final static Setting MIN_INCLUSIVE = new Setting("min-inclusive", IntegerType.class);
    private final static Setting MAX_INCLUSIVE = new Setting("max-inclusive", IntegerType.class);

    static {
        Set<Setting> settings = new LinkedHashSet<>();
        settings.add(MIN_INCLUSIVE);
        settings.add(MAX_INCLUSIVE);
        SETTINGS = Collections.unmodifiableSet(settings);
    }

    private final FeaturePlacementModifier<?> placementModifier;
    private final HeightmapValue heightmap;
    private IntegerValue minInclusive;
    private IntegerValue maxInclusive;
    private boolean dirty = false;

    public SurfaceRelativeThresholdModifierConfiguration(FeaturePlacementModifier<?> placementModifier, HeightmapValue heightmap, IntegerValue minInclusive, IntegerValue maxInclusive) {
        this.placementModifier = placementModifier;
        this.heightmap = heightmap;
        this.minInclusive = minInclusive;
        this.maxInclusive = maxInclusive;
    }

    @NotNull
    @Override
    public FeaturePlacementModifier<?> getOwner() {
        return placementModifier;
    }

    public HeightmapValue getHeightmap() {
        return heightmap;
    }

    public IntegerValue getMinInclusive() {
        return minInclusive;
    }

    public IntegerValue getMaxInclusive() {
        return maxInclusive;
    }

    @NotNull
    @Override
    public Set<Setting> getSettings() {
        return SETTINGS;
    }

    @Override
    public Value<?, ?, ?> getValue(@NotNull Setting setting) {
        if (setting == MIN_INCLUSIVE) {
            return getMinInclusive();
        }

        if (setting == MAX_INCLUSIVE) {
            return getMaxInclusive();
        }

        throw new IllegalArgumentException(String.format("Setting '%s' is not in the configuration '%s'", setting, "SurfaceRelativeThresholdModifierConfiguration"));
    }

    @Override
    public void setValue(@NotNull Setting setting, Value<?, ?, ?> value) {
        if (setting == MIN_INCLUSIVE) {
            minInclusive = (IntegerValue) value;
            dirty = true;
            return;
        }

        if (setting == MAX_INCLUSIVE) {
            maxInclusive = (IntegerValue) value;
            dirty = true;
            return;
        }

        throw new IllegalArgumentException(String.format("Setting '%s' is not in the configuration '%s'", setting, "SurfaceRelativeThresholdModifierConfiguration"));
    }

    @Override
    public boolean isDirty() {
        if (dirty) {
            return true;
        }

        if (minInclusive != null && minInclusive.isDirty()) {
            return true;
        }

        return maxInclusive != null && maxInclusive.isDirty();
    }

    @Override
    public void saved() {
        dirty = false;

        if (minInclusive != null) {
            minInclusive.saved();
        }

        if (maxInclusive != null) {
            maxInclusive.saved();
        }
    }
}
