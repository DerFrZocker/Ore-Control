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

package de.derfrzocker.ore.control.gui;

import de.derfrzocker.feature.api.Configuration;
import de.derfrzocker.feature.api.ConfigurationAble;
import de.derfrzocker.feature.api.Feature;
import de.derfrzocker.feature.api.FeatureGenerator;
import de.derfrzocker.feature.api.FeatureGeneratorConfiguration;
import de.derfrzocker.feature.api.FeaturePlacementModifier;
import de.derfrzocker.feature.api.PlacementModifierConfiguration;
import de.derfrzocker.feature.api.Value;
import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.OreControlManager;
import de.derfrzocker.ore.control.api.config.Config;
import de.derfrzocker.ore.control.api.config.ConfigInfo;
import de.derfrzocker.spigot.utils.gui.InventoryGui;
import org.bukkit.plugin.Plugin;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public class PlayerGuiData {

    private final Deque<InventoryGui> guiTree = new ArrayDeque<>();
    private final Deque<Value<?, ?, ?>> valueTree = new ArrayDeque<>();
    private final Map<String, Object> data = new HashMap<>();
    private ConfigInfo configInfo = null;
    private Biome biome = null;
    private Feature feature = null;
    private SettingWrapper settingWrapper = null;
    private FeaturePlacementModifier<?> placementModifier = null;
    private Value<?, ?, ?> originalValue = null;
    private Value<?, ?, ?> toEditValue = null;
    private boolean applied = false;

    private static PlacementModifierConfiguration getPlacementConfiguration(Collection<PlacementModifierConfiguration> placementModifierConfigurations, ConfigurationAble owner) {
        if (placementModifierConfigurations == null || placementModifierConfigurations.isEmpty()) {
            return null;
        }

        for (PlacementModifierConfiguration configuration : placementModifierConfigurations) {
            if (configuration.getOwner() == owner) {
                return configuration;
            }
        }

        return null;
    }

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

    public Feature getFeature() {
        return feature;
    }

    public void setFeature(Feature feature) {
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

    public void addGui(InventoryGui gui) {
        guiTree.addFirst(gui);
    }

    public InventoryGui pollFirstInventory() {
        return guiTree.pollFirst();
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
        if (this.toEditValue != null) {
            valueTree.addFirst(this.toEditValue);
        }

        this.toEditValue = toEditValue;
    }

    public void setPreviousToEditValue() {
        this.toEditValue = valueTree.pollFirst();
        if (toEditValue == null) {
            originalValue = null;
        }
    }

    public void addData(String key, Object value) {
        data.put(key, value);
    }

    public void removeData(String key) {
        data.remove(key);
    }

    public Object getData(String key) {
        return data.get(key);
    }

    // TODO move to better location
    public void apply(Plugin plugin, OreControlManager oreControlManager) {
        if (!isApplied()) {
            Config config;
            if (getBiome() == null) {
                config = oreControlManager.getConfigManager().getOrCreateConfig(getConfigInfo(), getFeature().getKey());
            } else {
                config = oreControlManager.getConfigManager().getOrCreateConfig(getConfigInfo(), getBiome(), getFeature().getKey());
            }

            SettingWrapper settingWrapper = getSettingWrapper();
            Configuration configuration;
            if (settingWrapper.getSettingOwner() instanceof FeatureGenerator) {
                if (config.getFeature() == null) {
                    config.setFeature((FeatureGeneratorConfiguration) settingWrapper.getSettingOwner().createEmptyConfiguration());
                } else if (config.getFeature().getOwner() != settingWrapper.getSettingOwner()) {
                    plugin.getLogger().warning(String.format("Expected a setting owner of type '%s' but got one of type '%s', this is a bug!", settingWrapper.getSettingOwner().getClass(), config.getFeature().getOwner()));
                    return;
                }

                configuration = config.getFeature();
            } else if (settingWrapper.getSettingOwner() instanceof FeaturePlacementModifier) {
                configuration = getPlacementConfiguration(config.getPlacements().values(), settingWrapper.getSettingOwner());

                if (configuration == null) {
                    configuration = settingWrapper.getSettingOwner().createEmptyConfiguration();
                    config.setPlacement((PlacementModifierConfiguration) configuration);
                }
            } else {
                plugin.getLogger().warning(String.format("Expected a setting owner of type '%s' or '%s' but got '%s', this is a bug!", FeatureGenerator.class, FeaturePlacementModifier.class, settingWrapper.getSettingOwner().getClass()));
                return;
            }

            configuration.setValue(settingWrapper.getSetting(), getOriginalValue());
            setApplied(true);

            // clear gui config cache
            if (getBiome() == null) {
                oreControlManager.getConfigManager().clearGuiConfigCache(getConfigInfo(), getFeature().getKey());
            } else {
                oreControlManager.getConfigManager().clearGuiConfigCache(getConfigInfo(), getBiome(), getFeature().getKey());
            }
        }
    }

    public FeaturePlacementModifier<?> getPlacementModifier() {
        return placementModifier;
    }

    public void setPlacementModifier(FeaturePlacementModifier<?> placementModifier) {
        this.placementModifier = placementModifier;
    }
}
