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

    @Override
    public FeatureGenerator<?> getOwner() {
        return featureGenerator;
    }

    @Override
    public Set<Setting> getSettings() {
        return SETTINGS;
    }

    @Override
    public Value<?, ?, ?> getValue(Setting setting) {
        if (setting == SIZE) {
            return getSize();
        }

        if (setting == DISCARD_CHANCE_ON_AIR_EXPOSURE) {
            return getDiscardChanceOnAirExposure();
        }

        throw new IllegalArgumentException(String.format("Setting '%s' is not in the configuration '%s'", setting, "OreFeatureConfiguration"));
    }

    @Override
    public void setValue(Setting setting, Value<?, ?, ?> value) {
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
