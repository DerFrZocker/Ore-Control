package de.derfrzocker.ore.control.api.config;

import de.derfrzocker.feature.api.FeatureGeneratorConfiguration;
import de.derfrzocker.feature.api.FeaturePlacementModifier;
import de.derfrzocker.feature.api.PlacementModifierConfiguration;
import de.derfrzocker.feature.api.util.SaveAble;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Config implements SaveAble {

    private final Map<FeaturePlacementModifier<?>, PlacementModifierConfiguration> placements = new LinkedHashMap<>();
    private FeatureGeneratorConfiguration feature;
    private boolean dirty = false;

    public Config() {
        feature = null;
    }

    public Config(List<PlacementModifierConfiguration> placements, FeatureGeneratorConfiguration feature) {
        this.feature = feature;

        if (placements != null) {
            for (PlacementModifierConfiguration configuration : placements) {
                this.placements.put(configuration.getOwner(), configuration);
            }
        }
    }

    public Map<FeaturePlacementModifier<?>, PlacementModifierConfiguration> getPlacements() {
        return Collections.unmodifiableMap(placements);
    }

    public FeatureGeneratorConfiguration getFeature() {
        return feature;
    }

    public void setFeature(FeatureGeneratorConfiguration configuration) {
        this.feature = configuration;
        dirty = true;
    }

    public void setPlacement(PlacementModifierConfiguration configuration) {
        placements.put(configuration.getOwner(), configuration);
        dirty = true;
    }

    public boolean isDirty() {
        if (dirty) {
            return true;
        }

        if (feature != null && feature.isDirty()) {
            return true;
        }

        for (PlacementModifierConfiguration configuration : placements.values()) {
            if (configuration.isDirty()) {
                return true;
            }
        }

        return false;
    }

    public void saved() {
        dirty = false;

        if (feature != null) {
            feature.saved();
        }

        for (PlacementModifierConfiguration configuration : placements.values()) {
            configuration.saved();
        }
    }
}
