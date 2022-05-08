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

package de.derfrzocker.feature.common.feature.placement.configuration;

import de.derfrzocker.feature.api.FeaturePlacementModifier;
import de.derfrzocker.feature.api.PlacementModifierConfiguration;
import de.derfrzocker.feature.api.Setting;
import de.derfrzocker.feature.api.Value;
import de.derfrzocker.feature.common.value.bool.BooleanType;
import de.derfrzocker.feature.common.value.bool.BooleanValue;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class ActivationConfiguration implements PlacementModifierConfiguration {

    public final static Set<Setting> SETTINGS;
    public final static Setting ACTIVATE = new Setting("activate", BooleanType.class);

    static {
        Set<Setting> settings = new LinkedHashSet<>();
        settings.add(ACTIVATE);
        SETTINGS = Collections.unmodifiableSet(settings);
    }

    private final FeaturePlacementModifier<?> placementModifier;
    private BooleanValue activate;
    private boolean dirty = false;

    public ActivationConfiguration(FeaturePlacementModifier<?> placementModifier, BooleanValue activate) {
        this.placementModifier = placementModifier;
        this.activate = activate;
    }

    public BooleanValue getActivate() {
        return activate;
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
        if (setting == ACTIVATE) {
            return getActivate();
        }

        throw new IllegalArgumentException(String.format("Setting '%s' is not in the configuration '%s'", setting, getClass().getSimpleName()));
    }

    @Override
    public void setValue(@NotNull Setting setting, Value<?, ?, ?> value) {
        if (setting == ACTIVATE) {
            activate = (BooleanValue) value;
            dirty = true;
            return;
        }

        throw new IllegalArgumentException(String.format("Setting '%s' is not in the configuration '%s'", setting, getClass().getSimpleName()));
    }

    @Override
    public boolean isDirty() {
        if (dirty) {
            return true;
        }

        return activate != null && activate.isDirty();
    }

    @Override
    public void saved() {
        dirty = false;

        if (activate != null) {
            activate.saved();
        }
    }
}
