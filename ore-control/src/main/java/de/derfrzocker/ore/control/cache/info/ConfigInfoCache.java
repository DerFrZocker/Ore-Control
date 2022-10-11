package de.derfrzocker.ore.control.cache.info;

import de.derfrzocker.ore.control.api.config.ConfigInfo;
import de.derfrzocker.ore.control.api.config.ConfigType;
import de.derfrzocker.ore.control.api.config.dao.ConfigInfoDao;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigInfoCache {

    private final Set<ConfigInfo> configInfos = new HashSet<>();
    private final Map<String, ConfigInfo> worldNames = new ConcurrentHashMap<>();
    private final ConfigInfoDao configInfoDao;
    private ConfigInfo globalConfigInfo;

    public ConfigInfoCache(ConfigInfoDao configInfoDao) {
        this.configInfoDao = configInfoDao;
    }

    public ConfigInfo getGlobalConfigInfo() {
        return globalConfigInfo;
    }

    public void save() {
        for (ConfigInfo configInfo : configInfos) {
            if (configInfo.isDirty()) {
                configInfoDao.save(configInfo);
                configInfo.saved();
            }
        }
    }

    public void reload() {
        globalConfigInfo = configInfoDao.getGlobalConfig();

        worldNames.clear();
        configInfos.clear();
        for (ConfigInfo configInfo : configInfoDao.getConfigInfos()) {
            worldNames.put(configInfo.getWorldName(), configInfo);
            configInfos.add(configInfo);
        }
    }

    public Set<ConfigInfo> getConfigInfos() {
        Set<ConfigInfo> configInfos = new LinkedHashSet<>();
        Set<ConfigInfo> tmpConfigInfos = new LinkedHashSet<>();
        configInfos.add(globalConfigInfo);

        for (ConfigInfo configInfo : this.configInfos) {
            if (configInfo.getConfigType() == ConfigType.WORLD) {
                configInfos.add(configInfo);
            } else {
                tmpConfigInfos.add(configInfo);
            }
        }

        configInfos.addAll(tmpConfigInfos);

        return configInfos;
    }

    public ConfigInfo getOrCreateConfigInfo(String worldName) {
        ConfigInfo configInfo = worldNames.get(worldName);
        if (configInfo == null) {
            configInfo = configInfoDao.createConfigInfo(worldName);
            configInfos.add(configInfo);
            worldNames.put(worldName, configInfo);
        }

        return configInfo;
    }
}
