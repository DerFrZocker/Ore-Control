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

package de.derfrzocker.ore.control.api.config.dao;

import de.derfrzocker.ore.control.api.config.ConfigInfo;
import de.derfrzocker.ore.control.api.config.ConfigType;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

public class ConfigInfoDao {
    private static final String CONFIG_INFO_FILE = "config-info.yml";

    private final Plugin plugin;
    private final File normalConfigs;
    private final File globalConfigs;

    public ConfigInfoDao(Plugin plugin, File normalConfigs, File globalConfigs) {
        this.plugin = plugin;
        this.normalConfigs = normalConfigs;
        this.globalConfigs = globalConfigs;
    }

    public ConfigInfo createConfigInfo(String worldName) {
        File file = new File(normalConfigs, worldName);

        if (file.exists()) {
            int i = 0;

            do {
                file = new File(normalConfigs, worldName + "_" + i);
                i++;
            } while (file.exists());
        }

        return new ConfigInfo(worldName, ConfigType.TEMPLATE, file);
    }

    public ConfigInfo getGlobalConfig() {
        if (!globalConfigs.exists()) {
            globalConfigs.mkdirs();
        }

        File file = new File(globalConfigs, CONFIG_INFO_FILE);

        if (!file.exists()) {
            ConfigInfo configInfo = new ConfigInfo("global", ConfigType.GLOBAL, globalConfigs);
            save(configInfo);
            return configInfo;
        }

        return readConfigInfo(file, ConfigType.GLOBAL);
    }

    public Set<ConfigInfo> getConfigInfos() {
        if (!normalConfigs.exists()) {
            normalConfigs.mkdirs();
        }

        Set<ConfigInfo> configs = new LinkedHashSet<>();

        for (File directory : normalConfigs.listFiles()) {
            if (!directory.isDirectory()) {
                continue;
            }

            File file = new File(directory, CONFIG_INFO_FILE);
            ConfigInfo configInfo;
            if (!file.exists()) {
                configInfo = new ConfigInfo(directory.getName(), ConfigType.TEMPLATE, directory);
                save(configInfo);
            } else {
                configInfo = readConfigInfo(file, ConfigType.TEMPLATE);
            }

            if (configInfo.getConfigType() == ConfigType.TEMPLATE || configInfo.getConfigType() == ConfigType.WORLD) {
                configs.add(configInfo);
            } else {
                plugin.getLogger().warning(String.format("Found config type '%s' for none global config info '%s'. Ignoring it", configInfo.getConfigType(), file));
            }
        }

        return configs;
    }

    public void save(ConfigInfo info) {
        FileConfiguration config = new YamlConfiguration();
        File file = new File(info.getDataDirectory(), CONFIG_INFO_FILE);

        if (file.exists()) {
            try {
                config.load(file);
            } catch (IOException | InvalidConfigurationException e) {
                throw new RuntimeException("Cannot load configuration from file " + file, e);
            }
        }

        config.set("world-name", info.getWorldName());
        config.set("config-type", info.getConfigType().toString());

        try {
            config.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Cannot save configuration " + file, e);
        }
    }

    private ConfigInfo readConfigInfo(File file, ConfigType defaultConfigType) {
        FileConfiguration config = new YamlConfiguration();
        try {
            config.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException("Cannot load configuration from file " + file, e);
        }

        String worldName = config.getString("world-name");
        String configTypeString = config.getString("config-type");

        if (worldName == null || worldName.isEmpty()) {
            plugin.getLogger().warning(String.format("World name for config info '%s' is null or empty. Using directory name", file));
            worldName = file.getParentFile().getName();
        }

        ConfigType configType;
        try {
            configType = ConfigType.valueOf(configTypeString);
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning(String.format("Could not parse config type '%s' for config info '%s'. Using default config type '%s'", configTypeString, file, defaultConfigType));
            configType = defaultConfigType;
        }

        if (configType == ConfigType.GLOBAL && defaultConfigType != ConfigType.GLOBAL) {
            plugin.getLogger().warning(String.format("Found config type '%s' for none global config info '%s'. Using default config type '%s'", configTypeString, file, defaultConfigType));
            configType = defaultConfigType;
        }

        return new ConfigInfo(worldName, configType, file.getParentFile());
    }
}
