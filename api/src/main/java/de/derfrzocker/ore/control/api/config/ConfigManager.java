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

import de.derfrzocker.feature.api.ExtraValues;
import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.util.Reloadable;
import org.bukkit.NamespacedKey;

import java.util.Optional;
import java.util.Set;

public interface ConfigManager extends Reloadable {

    void save();

    void saveAndReload();

    // Config Info
    Set<ConfigInfo> getConfigInfos();

    ConfigInfo getOrCreateConfigInfo(String worldName);

    // Extra Values
    Optional<ExtraValues> getExtraValues(ConfigInfo configInfo);

    ExtraValues getOrCreateExtraValues(ConfigInfo configInfo);

    Optional<ExtraValues> getGuiExtraValues(ConfigInfo configInfo);

    void clearGuiExtraValueCache(ConfigInfo configInfo);

    Optional<ExtraValues> getGenerationExtraValues(ConfigInfo configInfo);

    // Normal Config
    Optional<Config> getConfig(ConfigInfo configInfo, NamespacedKey featureKey);

    Optional<Config> getConfig(ConfigInfo configInfo, Biome biome, NamespacedKey featureKey);

    Config getOrCreateConfig(ConfigInfo configInfo, NamespacedKey featureKey);

    Config getOrCreateConfig(ConfigInfo configInfo, Biome biome, NamespacedKey featureKey);

    // GUI Config
    Optional<Config> getGuiConfig(ConfigInfo configInfo, NamespacedKey featureKey);

    Optional<Config> getGuiConfig(ConfigInfo configInfo, Biome biome, NamespacedKey featureKey);

    void clearGuiConfigCache(ConfigInfo configInfo, NamespacedKey featureKey);

    void clearGuiConfigCache(ConfigInfo configInfo, Biome biome, NamespacedKey featureKey);

    // Generation Config
    Optional<Config> getGenerationConfig(ConfigInfo configInfo, Biome biome, NamespacedKey featureKey);

    // Default Config
    Optional<Config> getDefaultConfig(NamespacedKey featureKey);

    Optional<Config> getDefaultConfig(Biome biome, NamespacedKey featureKey);

    void setDefaultConfig(NamespacedKey featureKey, Config config);

    void setDefaultConfig(Biome biome, NamespacedKey featureKey, Config config);
}
