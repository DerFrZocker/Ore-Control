/*
 * MIT License
 *
 * Copyright (c) 2019 - 2021 Marvin (DerFrZocker)
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
import de.derfrzocker.feature.api.PlacementModifierConfiguration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Config {
    private List<PlacementModifierConfiguration> placements;
    private FeatureGeneratorConfiguration feature;
    private boolean dirty = false;

    public Config() {
        placements = new ArrayList<>();
        feature = null;
    }

    public Config(List<PlacementModifierConfiguration> placements, FeatureGeneratorConfiguration feature) {
        this.placements = placements;
        this.feature = feature;
    }

    public List<PlacementModifierConfiguration> getPlacements() {
        return Collections.unmodifiableList(placements);
    }

    public FeatureGeneratorConfiguration getFeature() {
        return feature;
    }

    public void setFeature(FeatureGeneratorConfiguration configuration) {
        this.feature = configuration;
        dirty = true;
    }

    public void setPlacement(PlacementModifierConfiguration configuration) {
        if (placements == null) {
            placements = new LinkedList<>();
            placements.add(configuration);
            dirty = true;
            return;
        }

        if (placements.isEmpty()) {
            placements.add(configuration);
            dirty = true;
            return;
        }

        PlacementModifierConfiguration toRemove = null;
        for (PlacementModifierConfiguration toCheck : placements) {
            if (toCheck.getOwner() == configuration.getOwner()) {
                toRemove = toCheck;
                break;
            }
        }

        if (toRemove != null) {
            placements.remove(toRemove);
        }

        placements.add(configuration);
        dirty = true;
    }

    public boolean isDirty() {
        if (dirty) {
            return true;
        }

        if (feature != null && feature.isDirty()) {
            return true;
        }

        if (placements == null) {
            return false;
        }

        for (PlacementModifierConfiguration configuration : placements) {
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

        if (placements == null) {
            return;
        }

        for (PlacementModifierConfiguration configuration : placements) {
            configuration.saved();
        }
    }
}
