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

package de.derfrzocker.ore.control.gui;

import de.derfrzocker.feature.api.Feature;
import de.derfrzocker.feature.api.Value;
import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.config.ConfigInfo;

public class PlayerGuiData {

    private ConfigInfo configInfo = null;
    private Biome biome = null;
    private Feature<?> feature = null;
    private SettingWrapper settingWrapper = null;
    private Value<?, ?, ?> originalValue = null;
    private Value<?, ?, ?> toEditValue = null;
    private boolean applied = false;

    public ConfigInfo getConfigInfo() {
        return configInfo;
    }

    public void setConfigInfo(ConfigInfo configInfo) {
        this.configInfo = configInfo;
    }

    public Biome getBiome() {
        return biome;
    }

    public void setBiome(Biome biome) {
        this.biome = biome;
    }

    public Feature<?> getFeature() {
        return feature;
    }

    public void setFeature(Feature<?> feature) {
        this.feature = feature;
    }

    public SettingWrapper getSettingWrapper() {
        return settingWrapper;
    }

    public void setSettingWrapper(SettingWrapper settingWrapper) {
        this.settingWrapper = settingWrapper;
    }

    public Value<?, ?, ?> getOriginalValue() {
        return originalValue;
    }

    public void setOriginalValue(Value<?, ?, ?> originalValue) {
        this.originalValue = originalValue;
    }

    public boolean isApplied() {
        return applied;
    }

    public void setApplied(boolean applied) {
        this.applied = applied;
    }

    public Value<?, ?, ?> getToEditValue() {
        return toEditValue;
    }

    public void setToEditValue(Value<?, ?, ?> toEditValue) {
        this.toEditValue = toEditValue;
    }
}
