/*
 * MIT License
 *
 * Copyright (c) 2019 - 2020 Marvin (DerFrZocker)
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

package de.derfrzocker.ore.control.impl;

import de.derfrzocker.ore.control.api.*;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@SerializableAs("OreControl#WorldOreConfig")
public class WorldOreConfigYamlImpl implements ConfigurationSerializable, WorldOreConfig {

    @Deprecated
    private static final String WORLD_KEY = "world";
    @Deprecated
    private static final String TEMPLATE_KEY = "template";
    private static final String NAME_KEY = "name";
    private static final String CONFIG_TYPE_KEY = "config-type";
    private static final String ORE_SETTINGS_KEY = "ore-settings";
    private static final String BIOME_ORE_SETTINGS_KEY = "biome-ore-settings";

    @NotNull
    private final Map<Ore, OreSettings> oreSettings = new LinkedHashMap<>();
    @NotNull
    private final Map<Biome, BiomeOreSettings> biomeOreSettings = new LinkedHashMap<>();
    @NotNull
    private final String name;
    private ConfigType configType;

    public WorldOreConfigYamlImpl(@NotNull final String name, final ConfigType configType) {
        Validate.notNull(name, "Name cannot be null");

        this.name = name;
        this.configType = configType;
    }

    public WorldOreConfigYamlImpl(@NotNull final String name, final ConfigType configType, @NotNull final Map<Ore, OreSettings> oreSettings) {
        this(name, configType);
        Validate.notNull(oreSettings, "OreSettings map cannot be null");

        oreSettings.forEach((key, value) -> this.oreSettings.put(key, value.clone()));
    }

    public WorldOreConfigYamlImpl(@NotNull final String name, final ConfigType configType, @NotNull final Map<Ore, OreSettings> oreSettings, @NotNull final Map<Biome, BiomeOreSettings> biomeOreSettings) {
        this(name, configType, oreSettings);
        Validate.notNull(biomeOreSettings, "BiomeOreSettings map cannot be null");

        biomeOreSettings.forEach((key, value) -> this.biomeOreSettings.put(key, value.clone()));
    }

    @NotNull
    public static WorldOreConfigYamlImpl deserialize(@NotNull final Map<String, Object> map) {
        Validate.notNull(map, "Map cannot be null");

        final Map<Ore, OreSettings> oreSettings = new LinkedHashMap<>();
        final Map<Biome, BiomeOreSettings> biomeOreSettings = new LinkedHashMap<>();

        // if no ore settings key is present we have
        // a) no ore settings in this world ore config
        // b) the old storage type
        if (map.containsKey(ORE_SETTINGS_KEY)) {
            // new storage type
            final List<OreSettings> list = (List<OreSettings>) map.get(ORE_SETTINGS_KEY);

            list.forEach(oreSettings1 -> oreSettings.put(oreSettings1.getOre(), oreSettings1));
        } else {
            // old storage type
            map.entrySet().stream().filter(entry -> isOre(entry.getKey())).
                    forEach(entry -> oreSettings.put(Ore.valueOf(entry.getKey().toUpperCase()), (OreSettings) entry.getValue()));
        }

        // if no biome ore settings key is present we have
        // a) no biome ore settings in this world ore config
        // b) the old storage type
        if (map.containsKey(BIOME_ORE_SETTINGS_KEY)) {
            // new storage type
            final List<BiomeOreSettings> list = (List<BiomeOreSettings>) map.get(BIOME_ORE_SETTINGS_KEY);

            list.forEach(biomeOreSettings1 -> biomeOreSettings.put(biomeOreSettings1.getBiome(), biomeOreSettings1));
        } else {
            // old storage type
            map.entrySet().stream().filter(entry -> isBiome(entry.getKey())).
                    forEach(entry -> biomeOreSettings.put(Biome.valueOf(entry.getKey().toUpperCase()), (BiomeOreSettings) entry.getValue()));
        }

        final String name;

        if (map.containsKey(WORLD_KEY)) {
            name = (String) map.get(WORLD_KEY);
        } else {
            name = (String) map.get(NAME_KEY);
        }

        String configTypeString = (String) map.get(CONFIG_TYPE_KEY);
        ConfigType configType;
        if (configTypeString == null) {
            if ((boolean) map.getOrDefault(TEMPLATE_KEY, false)) {
                configType = ConfigType.TEMPLATE;
            } else {
                configType = ConfigType.UNKNOWN;
            }
        } else {
            configType = ConfigType.valueOf(configTypeString.toUpperCase());
        }

        return new WorldOreConfigYamlImpl(name, configType, oreSettings, biomeOreSettings);
    }

    private static boolean isOre(@Nullable final String string) {
        if (string == null) {
            return false;
        }

        try {
            Ore.valueOf(string.toUpperCase());
            return true;
        } catch (final IllegalArgumentException e) {
            return false;
        }

    }

    private static boolean isBiome(@Nullable final String string) {
        if (string == null) {
            return false;
        }

        try {
            Biome.valueOf(string.toUpperCase());
            return true;
        } catch (final IllegalArgumentException e) {
            return false;
        }
    }

    @NotNull
    @Override
    public String getName() {
        return this.name;
    }

    @NotNull
    @Override
    public Optional<OreSettings> getOreSettings(@NotNull final Ore ore) {
        Validate.notNull(ore, "Ore cannot be null");

        return Optional.ofNullable(this.oreSettings.get(ore));
    }

    @NotNull
    @Override
    public Map<Ore, OreSettings> getOreSettings() {
        return this.oreSettings;
    }

    @Override
    public void setOreSettings(@NotNull final OreSettings oreSettings) {
        Validate.notNull(oreSettings, "OreSettings cannot be null");

        this.oreSettings.put(oreSettings.getOre(), oreSettings);
    }

    @NotNull
    @Override
    public Optional<BiomeOreSettings> getBiomeOreSettings(@NotNull final Biome biome) {
        Validate.notNull(biome, "Biome cannot be null");

        return Optional.ofNullable(getBiomeOreSettings().get(biome));
    }

    @NotNull
    @Override
    public Map<Biome, BiomeOreSettings> getBiomeOreSettings() {
        return this.biomeOreSettings;
    }

    @Override
    public void setBiomeOreSettings(@NotNull final BiomeOreSettings biomeOreSettings) {
        Validate.notNull(biomeOreSettings, "BiomeOreSettings cannot be null");

        getBiomeOreSettings().put(biomeOreSettings.getBiome(), biomeOreSettings);
    }

    @Override
    public boolean isTemplate() {
        return configType == ConfigType.TEMPLATE || configType == ConfigType.GLOBAL;
    }

    @Override
    public void setTemplate(boolean status) {
        if (status) {
            this.configType = ConfigType.TEMPLATE;
        } else {
            this.configType = ConfigType.UNKNOWN;
        }
    }

    @NotNull
    @Override
    public ConfigType getConfigType() {
        return this.configType;
    }

    @Override
    public void setConfigType(@NotNull ConfigType configType) {
        Validate.notNull(configType, "ConfigType cannot be null");

        this.configType = configType;
    }

    @NotNull
    @Override
    public WorldOreConfig clone(@NotNull final String name) {
        Validate.notNull(name, "Name cannot be null");

        return new WorldOreConfigYamlImpl(name, getConfigType(), getOreSettings(), getBiomeOreSettings());
    }

    @Override
    public boolean equals(@Nullable final Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        final WorldOreConfigYamlImpl that = (WorldOreConfigYamlImpl) object;

        return isTemplate() == that.isTemplate() &&
                getOreSettings().equals(that.getOreSettings()) &&
                getBiomeOreSettings().equals(that.getBiomeOreSettings()) &&
                getName().equals(that.getName());
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        final Map<String, Object> serialize = new LinkedHashMap<>();

        serialize.put(NAME_KEY, getName());
        serialize.put(CONFIG_TYPE_KEY, configType.name());

        final Map<Ore, OreSettings> oreSettingsMap = getOreSettings();
        if (!oreSettingsMap.isEmpty()) {
            final List<OreSettings> oreSettingsList = new LinkedList<>();

            oreSettingsMap.values().forEach(oreSettings -> {
                if (!oreSettings.getSettings().isEmpty() || !oreSettings.isActivated()) {
                    if (oreSettings instanceof ConfigurationSerializable) {
                        oreSettingsList.add(oreSettings);
                    } else {
                        final OreSettingsYamlImpl oreSettingsYaml = new OreSettingsYamlImpl(oreSettings.getOre(), oreSettings.getSettings());
                        oreSettingsYaml.setActivated(oreSettings.isActivated());
                        oreSettingsList.add(oreSettingsYaml);
                    }
                }
            });

            serialize.put(ORE_SETTINGS_KEY, oreSettingsList);
        }

        final Map<Biome, BiomeOreSettings> biomeOreSettingsMap = getBiomeOreSettings();
        if (!biomeOreSettingsMap.isEmpty()) {
            final List<BiomeOreSettings> biomeOreSettingsList = new LinkedList<>();

            biomeOreSettingsMap.values().forEach(biomeOreSettings -> {
                if (biomeOreSettings.getOreSettings().isEmpty()) {
                    return;
                }

                if (biomeOreSettings.getOreSettings().values().stream().
                        anyMatch(oreSettings -> !oreSettings.getSettings().isEmpty() || !oreSettings.isActivated())) {
                    if (biomeOreSettings instanceof ConfigurationSerializable) {
                        biomeOreSettingsList.add(biomeOreSettings);
                    } else {
                        biomeOreSettingsList.add(new BiomeOreSettingsYamlImpl(biomeOreSettings.getBiome(), biomeOreSettings.getOreSettings()));
                    }
                }
            });

            serialize.put(BIOME_ORE_SETTINGS_KEY, biomeOreSettingsList);
        }

        return serialize;
    }

}
