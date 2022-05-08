/*
 * MIT License
 *
 * Copyright (c) 2019 - 2022 Marvin (DerFrZocker)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package de.derfrzocker.ore.control.api.config;

import de.derfrzocker.feature.api.FeatureGeneratorConfiguration;
import de.derfrzocker.feature.api.FeaturePlacementModifier;
import de.derfrzocker.feature.api.PlacementModifierConfiguration;

import java.util.*;

public class Config {

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
