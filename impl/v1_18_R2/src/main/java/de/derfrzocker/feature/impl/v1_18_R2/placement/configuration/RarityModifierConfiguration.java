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

package de.derfrzocker.feature.impl.v1_18_R2.placement.configuration;

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

public class RarityModifierConfiguration implements PlacementModifierConfiguration {

    public final static Set<Setting> SETTINGS;
    private final static Setting CHANCE = new Setting("chance", IntegerType.class);

    static {
        Set<Setting> settings = new LinkedHashSet<>();
        settings.add(CHANCE);
        SETTINGS = Collections.unmodifiableSet(settings);
    }

    private final FeaturePlacementModifier<?> placementModifier;
    private IntegerValue chance;
    private boolean dirty = false;

    public RarityModifierConfiguration(FeaturePlacementModifier<?> placementModifier, IntegerValue chance) {
        this.placementModifier = placementModifier;
        this.chance = chance;
    }

    public IntegerValue getChance() {
        return chance;
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
        if (setting == CHANCE) {
            return getChance();
        }

        throw new IllegalArgumentException(String.format("Setting '%s' is not in the configuration '%s'", setting, "PlacementModifierConfiguration"));
    }

    @Override
    public void setValue(@NotNull Setting setting, Value<?, ?, ?> value) {
        if (setting == CHANCE) {
            chance = (IntegerValue) value;
            dirty = true;
            return;
        }

        throw new IllegalArgumentException(String.format("Setting '%s' is not in the configuration '%s'", setting, "PlacementModifierConfiguration"));
    }

    @Override
    public boolean isDirty() {
        if (dirty) {
            return true;
        }

        return chance != null && chance.isDirty();
    }

    @Override
    public void saved() {
        dirty = false;

        if (chance != null) {
            chance.saved();
        }
    }
}
