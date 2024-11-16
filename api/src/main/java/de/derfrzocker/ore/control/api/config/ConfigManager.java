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
