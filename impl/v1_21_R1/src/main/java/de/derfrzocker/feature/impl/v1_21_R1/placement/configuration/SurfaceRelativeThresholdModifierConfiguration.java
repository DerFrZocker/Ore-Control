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

package de.derfrzocker.feature.impl.v1_21_R1.placement.configuration;

import de.derfrzocker.feature.api.FeaturePlacementModifier;
import de.derfrzocker.feature.api.PlacementModifierConfiguration;
import de.derfrzocker.feature.api.Setting;
import de.derfrzocker.feature.api.Value;
import de.derfrzocker.feature.common.value.number.IntegerType;
import de.derfrzocker.feature.common.value.number.IntegerValue;
import de.derfrzocker.feature.impl.v1_21_R1.value.heightmap.HeightmapValue;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

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
