package de.derfrzocker.ore.control.api.config.dao;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import de.derfrzocker.feature.api.Configuration;
import de.derfrzocker.feature.api.FeatureGeneratorConfiguration;
import de.derfrzocker.feature.api.PlacementModifierConfiguration;
import de.derfrzocker.feature.api.Setting;
import de.derfrzocker.feature.api.Value;
import de.derfrzocker.feature.api.ValueLocation;
import de.derfrzocker.feature.api.util.Parser;
import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.OreControlRegistries;
import de.derfrzocker.ore.control.api.config.Config;
import de.derfrzocker.ore.control.api.config.ConfigInfo;
import de.derfrzocker.ore.control.api.config.ConfigType;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.NamespacedKey;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

public class ConfigDao {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Parser<Config> configParser;

    public ConfigDao(OreControlRegistries registries) {
        this.configParser = new Parser<>() {
            @Override
            public JsonElement toJson(Config value) {
                JsonObject jsonObject = new JsonObject();
                if (!value.getPlacements().isEmpty()) {
                    JsonArray placement = new JsonArray();
                    for (PlacementModifierConfiguration configuration : value.getPlacements().values()) {
                        JsonObject entry = configuration.getOwner().getParser().toJson(configuration).getAsJsonObject();
                        entry.add("placement_modifier_type", new JsonPrimitive(configuration.getOwner().getKey().toString()));
                        placement.add(entry);
                    }

                    jsonObject.add("placement_modifier_configurations", placement);
                }

                if (value.getFeature() != null) {
                    JsonObject entry = value.getFeature().getOwner().getParser().toJson(value.getFeature()).getAsJsonObject();
                    entry.add("feature_generator_type", new JsonPrimitive(value.getFeature().getOwner().getKey().toString()));
                    jsonObject.add("feature_generator_configuration", entry);
                }

                return jsonObject;
            }

            @Override
            public Config fromJson(JsonElement jsonElement) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                List<PlacementModifierConfiguration> configurations = null;
                if (jsonObject.has("placement_modifier_configurations")) {
                    configurations = new ArrayList<>();
                    JsonArray values = jsonObject.getAsJsonArray("placement_modifier_configurations");
                    for (JsonElement entryE : values) {
                        JsonObject entry = entryE.getAsJsonObject();
                        PlacementModifierConfiguration configuration = registries.getPlacementModifierRegistry().get(NamespacedKey.fromString(entry.getAsJsonPrimitive("placement_modifier_type").getAsString())).get().getParser().fromJson(entry);
                        configurations.add(configuration);
                    }
                }

                FeatureGeneratorConfiguration featureGeneratorConfiguration = null;
                if (jsonObject.has("feature_generator_configuration")) {
                    JsonObject feature = jsonObject.getAsJsonObject("feature_generator_configuration");
                    featureGeneratorConfiguration = registries.getFeatureGeneratorRegistry().get(NamespacedKey.fromString(feature.getAsJsonPrimitive("feature_generator_type").getAsString())).get().getParser().fromJson(feature);
                }

                return new Config(configurations, featureGeneratorConfiguration);
            }
        };
    }

    public Parser<Config> getConfigParser() {
        return this.configParser;
    }

    public Optional<Config> getConfig(ConfigInfo configInfo, NamespacedKey key) {
        return setValueLocation(load(getConfigFile(configInfo, key)), configInfo.getConfigType() == ConfigType.GLOBAL ? ValueLocation.GLOBAL_WORLD : ValueLocation.PER_WORLD);
    }

    public Optional<Config> getConfig(ConfigInfo configInfo, Biome biome, NamespacedKey key) {
        return setValueLocation(load(getConfigFile(configInfo, biome, key)), configInfo.getConfigType() == ConfigType.GLOBAL ? ValueLocation.GLOBAL_BIOME : ValueLocation.PER_BIOME);
    }

    public void saveConfig(ConfigInfo configInfo, NamespacedKey key, Config config) {
        if (config == null) {
            getConfigFile(configInfo, key).delete();
            return;
        }

        save(getConfigFile(configInfo, key), config);
    }

    public void saveConfig(ConfigInfo configInfo, Biome biome, NamespacedKey key, Config config) {
        if (config == null) {
            getConfigFile(configInfo, biome, key).delete();
            return;
        }

        save(getConfigFile(configInfo, biome, key), config);
    }

    private Optional<Config> setValueLocation(Optional<Config> configOptional, ValueLocation valueLocation) {
        configOptional.ifPresent(config -> {
            setValueLocation(config.getFeature(), valueLocation);

            if (config.getPlacements() != null) {
                for (PlacementModifierConfiguration placementConfig : config.getPlacements().values()) {
                    setValueLocation(placementConfig, valueLocation);
                }
            }
        });

        return configOptional;
    }

    private void setValueLocation(Configuration configuration, ValueLocation valueLocation) {
        if (configuration == null) {
            return;
        }

        for (Setting setting : configuration.getSettings()) {
            Value<?, ?, ?> value = configuration.getValue(setting);
            if (value != null) {
                value.setValueLocation(valueLocation);
            }
        }
    }

    private File getConfigFile(ConfigInfo configInfo, NamespacedKey key) {
        return new File(configInfo.getDataDirectory(), "world/" + key.getNamespace() + "/" + key.getKey() + ".json");
    }

    private File getConfigFile(ConfigInfo configInfo, Biome biome, NamespacedKey key) {
        return new File(configInfo.getDataDirectory(), "biome/" + biome.getKey().getNamespace() + "/" + biome.getKey().getKey() + "/" + key.getNamespace() + "/" + key.getKey() + ".json");
    }

    private Optional<Config> load(File file) {
        if (!file.exists()) {
            return Optional.empty();
        }

        try {
            JsonElement json = JsonParser.parseString(Files.readString(file.toPath()));
            return Optional.of(getConfigParser().fromJson(json));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void save(File file, Config config) {
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        JsonElement element = getConfigParser().toJson(config);
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(GSON.toJson(element));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
