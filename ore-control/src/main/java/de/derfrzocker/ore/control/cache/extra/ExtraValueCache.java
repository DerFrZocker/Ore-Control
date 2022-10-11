package de.derfrzocker.ore.control.cache.extra;

import de.derfrzocker.feature.api.ExtraValues;
import de.derfrzocker.ore.control.api.config.ConfigInfo;
import de.derfrzocker.ore.control.api.config.dao.ExtraValueDao;

import java.util.Optional;
import java.util.function.Supplier;

public class ExtraValueCache {

    private final ExtraValueCacheSlice configCache = new ExtraValueCacheSlice();
    private final ExtraValueCacheSlice guiConfigCache = new ExtraValueCacheSlice();
    private final ExtraValueCacheSlice generationConfigCache = new ExtraValueCacheSlice();
    private final ExtraValueDao extraValueDao;
    private final Supplier<ConfigInfo> global;

    public ExtraValueCache(ExtraValueDao extraValueDao, Supplier<ConfigInfo> global) {
        this.extraValueDao = extraValueDao;
        this.global = global;
    }

    public Optional<ExtraValues> getExtraValues(ConfigInfo configInfo) {
        return configCache.getOrCompute(configInfo, () -> extraValueDao.getExtraValues(configInfo));
    }

    public Optional<ExtraValues> getGuiExtraValues(ConfigInfo configInfo) {
        return guiConfigCache.getOrCompute(configInfo, () -> loadConfig(configInfo));
    }

    public Optional<ExtraValues> getGenerationExtraValues(ConfigInfo configInfo) {
        return generationConfigCache.getOrCompute(configInfo, () -> loadConfig(configInfo));
    }

    public Optional<ExtraValues> loadConfig(ConfigInfo configInfo) {
        Optional<ExtraValues> globalConfig = getExtraValues(global.get());
        Optional<ExtraValues> worldConfig = getExtraValues(configInfo);

        return combine(worldConfig, globalConfig);
    }

    private Optional<ExtraValues> combine(Optional<ExtraValues> primaryConfig, Optional<ExtraValues> secondaryConfig) {
        if (primaryConfig.isEmpty()) {
            return secondaryConfig;
        }

        if (secondaryConfig.isEmpty()) {
            return primaryConfig;
        }

        ExtraValues primary = primaryConfig.get();
        ExtraValues secondary = secondaryConfig.get();

        Optional<Boolean> generatedBigOreVeins = primary.shouldGeneratedBigOreVeins();
        if (generatedBigOreVeins.isEmpty()) {
            generatedBigOreVeins = secondary.shouldGeneratedBigOreVeins();
        }

        return Optional.of(new ExtraValues(generatedBigOreVeins));
    }
}
