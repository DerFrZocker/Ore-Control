package de.derfrzocker.ore.control.cache.config;

import de.derfrzocker.feature.api.FeatureGeneratorConfiguration;
import de.derfrzocker.feature.api.PlacementModifierConfiguration;
import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.config.Config;
import de.derfrzocker.ore.control.api.config.ConfigInfo;
import de.derfrzocker.ore.control.api.config.dao.ConfigDao;
import org.bukkit.NamespacedKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class ConfigCache {
    private final DefaultConfigCache defaultConfigCache = new DefaultConfigCache();
    private final StandardConfigCache standardConfigCache;
    private final GuiConfigCache guiConfigCache;
    private final GenerationConfigCache generationConfigCache;

    public ConfigCache(ConfigDao configDao, Supplier<ConfigInfo> global) {
        this.standardConfigCache = new StandardConfigCache(configDao, global);
        this.guiConfigCache = new GuiConfigCache(defaultConfigCache, standardConfigCache);
        this.generationConfigCache = new GenerationConfigCache(standardConfigCache);
    }

    protected static Optional<Config> combineConfig(Optional<Config> primaryConfig, Optional<Config> secondaryConfig) {
        if (primaryConfig.isEmpty() && secondaryConfig.isEmpty()) {
            return primaryConfig;
        }

        if (secondaryConfig.isEmpty()) {
            return primaryConfig;
        }

        if (primaryConfig.isEmpty()) {
            return secondaryConfig;
        }

        Config primary = primaryConfig.get();
        Config secondary = secondaryConfig.get();
        List<PlacementModifierConfiguration> primaryPlacements = new ArrayList<>();
        List<PlacementModifierConfiguration> secondaryPlacements = new ArrayList<>();
        List<PlacementModifierConfiguration> placements = new ArrayList<>();

        if (primary.getPlacements() != null) {
            primaryPlacements.addAll(primary.getPlacements().values());
        }

        if (secondary.getPlacements() != null) {
            secondaryPlacements.addAll(secondary.getPlacements().values());
        }

        for (PlacementModifierConfiguration first : primaryPlacements) {
            PlacementModifierConfiguration second = null;
            for (PlacementModifierConfiguration temp : secondaryPlacements) {
                if (first.getOwner() == temp.getOwner()) {
                    second = temp;
                    break;
                }
            }

            if (second != null) {
                placements.add(first.getOwner().merge(first, second));
                secondaryPlacements.remove(second);
            } else {
                placements.add(first);
            }
        }

        placements.addAll(secondaryPlacements);

        FeatureGeneratorConfiguration configuration;
        if (primary.getFeature() != null && secondary.getFeature() != null) {
            configuration = primary.getFeature().getOwner().merge(primary.getFeature(), secondary.getFeature());
        } else if (primary.getFeature() != null) {
            configuration = primary.getFeature();
        } else {
            configuration = secondary.getFeature();
        }

        return Optional.of(new Config(placements, configuration));
    }

    public void save() {
        standardConfigCache.save();
    }

    public void clear() {
        standardConfigCache.clear();
        guiConfigCache.clear();
        generationConfigCache.clear();
    }

    public Optional<Config> getConfig(ConfigInfo configInfo, NamespacedKey featureKey) {
        return standardConfigCache.get(configInfo, featureKey);
    }

    public Optional<Config> getConfig(ConfigInfo configInfo, Biome biome, NamespacedKey featureKey) {
        return standardConfigCache.get(configInfo, biome, featureKey);
    }

    public Config getOrCreateConfig(ConfigInfo configInfo, NamespacedKey featureKey) {
        return standardConfigCache.getOrCreate(configInfo, featureKey);
    }

    public Config getOrCreateConfig(ConfigInfo configInfo, Biome biome, NamespacedKey featureKey) {
        return standardConfigCache.getOrCreate(configInfo, biome, featureKey);
    }

    public Optional<Config> getGuiConfig(ConfigInfo configInfo, NamespacedKey featureKey) {
        return guiConfigCache.getGuiConfig(configInfo, featureKey);
    }

    public Optional<Config> getGuiConfig(ConfigInfo configInfo, Biome biome, NamespacedKey featureKey) {
        return guiConfigCache.getGuiConfig(configInfo, biome, featureKey);
    }

    public void clearGuiConfigCache(ConfigInfo configInfo, NamespacedKey featureKey) {
        guiConfigCache.clearGuiConfigCache(configInfo, featureKey);
    }

    public void clearGuiConfigCache(ConfigInfo configInfo, Biome biome, NamespacedKey featureKey) {
        guiConfigCache.clearGuiConfigCache(configInfo, biome, featureKey);
    }

    public Optional<Config> getGenerationConfig(ConfigInfo configInfo, Biome biome, NamespacedKey featureKey) {
        return generationConfigCache.getGeneration(configInfo, biome, featureKey);
    }

    public Optional<Config> getDefaultConfig(NamespacedKey featureKey) {
        return defaultConfigCache.getDefault(featureKey);
    }

    public Optional<Config> getDefaultConfig(Biome biome, NamespacedKey featureKey) {
        return defaultConfigCache.getDefault(biome, featureKey);
    }

    public void setDefaultConfig(NamespacedKey featureKey, Config config) {
        defaultConfigCache.setDefault(featureKey, config);
    }

    public void setDefaultConfig(Biome biome, NamespacedKey featureKey, Config config) {
        defaultConfigCache.setDefault(biome, featureKey, config);
    }
}
