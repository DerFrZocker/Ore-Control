package de.derfrzocker.ore.control.api.config;

import de.derfrzocker.feature.api.util.SaveAble;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class ConfigInfo implements Comparable<ConfigInfo>, SaveAble {

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
