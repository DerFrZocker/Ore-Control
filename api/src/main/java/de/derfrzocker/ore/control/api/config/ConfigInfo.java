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

import org.jetbrains.annotations.NotNull;

import java.io.File;

public class ConfigInfo implements Comparable<ConfigInfo> {

    private final String worldName;
    private final File dataDirectory;
    private ConfigType configType;
    private boolean dirty = false;

    public ConfigInfo(String worldName, ConfigType configType, File dataDirectory) {
        this.worldName = worldName;
        this.configType = configType;
        this.dataDirectory = dataDirectory;
    }

    public String getWorldName() {
        return worldName;
    }

    public ConfigType getConfigType() {
        return configType;
    }

    public void setConfigType(ConfigType configType) {
        if (configType == ConfigType.GLOBAL) {
            throw new IllegalArgumentException(String.format("Cannot set config type to '%s'", configType));
        }

        this.configType = configType;
        dirty = true;
    }

    public File getDataDirectory() {
        return dataDirectory;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void saved() {
        dirty = false;
    }

    @Override
    public int compareTo(@NotNull ConfigInfo configInfo) {
        if (this == configInfo) {
            return 0;
        }

        if (getConfigType() != configInfo.getConfigType()) {
            return getConfigType().compareTo(configInfo.getConfigType());
        }

        return getWorldName().compareTo(configInfo.getWorldName());
    }
}
