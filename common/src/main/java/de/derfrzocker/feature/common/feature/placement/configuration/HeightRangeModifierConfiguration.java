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

public class HeightRangeModifierConfiguration implements PlacementModifierConfiguration {

    public final static Set<Setting> SETTINGS;
    private final static Setting HEIGHT = new Setting("height", IntegerType.class);

    static {
        Set<Setting> settings = new LinkedHashSet<>();
        settings.add(HEIGHT);
        SETTINGS = Collections.unmodifiableSet(settings);
    }

    private final FeaturePlacementModifier<?> placementModifier;
    private IntegerValue height;
    private boolean dirty = false;

    public HeightRangeModifierConfiguration(FeaturePlacementModifier<?> placementModifier, IntegerValue height) {
        this.placementModifier = placementModifier;
        this.height = height;
    }

    public IntegerValue getHeight() {
        return height;
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
        if (setting == HEIGHT) {
            return getHeight();
        }

        throw new IllegalArgumentException(String.format("Setting '%s' is not in the configuration '%s'", setting, "HeightRangeModifierConfiguration"));
    }

    @Override
    public void setValue(@NotNull Setting setting, Value<?, ?, ?> value) {
        if (setting == HEIGHT) {
            height = (IntegerValue) value;
            dirty = true;
            return;
        }

        throw new IllegalArgumentException(String.format("Setting '%s' is not in the configuration '%s'", setting, "HeightRangeModifierConfiguration"));
    }

    @Override
    public boolean isDirty() {
        if (dirty) {
            return true;
        }

        return height != null && height.isDirty();
    }

    @Override
    public void saved() {
        dirty = false;

        if (height != null) {
            height.saved();
        }
    }
}
