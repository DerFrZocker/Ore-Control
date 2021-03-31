/*
 * MIT License
 *
 * Copyright (c) 2019 - 2021 Marvin (DerFrZocker)
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

import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.BiomeOreSettings;
import de.derfrzocker.ore.control.api.Ore;
import de.derfrzocker.ore.control.api.OreSettings;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@SerializableAs("OreControl#BiomeOreSettings")
public class BiomeOreSettingsYamlImpl implements ConfigurationSerializable, BiomeOreSettings {

    private static final String BIOME_KEY = "biome";
    private static final String ORE_SETTINGS_KEY = "ore-settings";

    @NotNull
    private final Biome biome;
    @NotNull
    private final Map<Ore, OreSettings> oreSettings = new LinkedHashMap<>();

    public BiomeOreSettingsYamlImpl(@NotNull final Biome biome) {
        Validate.notNull(biome, "Biome cannot be null");

        this.biome = biome;
    }

    public BiomeOreSettingsYamlImpl(@NotNull final Biome biome, @NotNull final Map<Ore, OreSettings> oreSettings) {
        this(biome);
        Validate.notNull(oreSettings, "OreSettings map cannot be null");

        oreSettings.forEach((key, value) -> this.oreSettings.put(key, value.clone()));
    }

    @NotNull
    public static BiomeOreSettingsYamlImpl deserialize(@NotNull final Map<String, Object> map) {
        Validate.notNull(map, "Map cannot be null");

        final Map<Ore, OreSettings> oreSettings = new HashMap<>();

        // if no ore settings key is present we have
        // a) no ore settings in this biome ore config
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

        return new BiomeOreSettingsYamlImpl(Biome.valueOf(((String) map.get(BIOME_KEY)).toUpperCase()), oreSettings);
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

    @NotNull
    @Override
    public Biome getBiome() {
        return this.biome;
    }

    @NotNull
    @Override
    public Optional<OreSettings> getOreSettings(@NotNull final Ore ore) {
        Validate.notNull(ore, "Ore cannot be null");

        return Optional.ofNullable(getOreSettings().get(ore));
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
    public BiomeOreSettings clone() {
        return new BiomeOreSettingsYamlImpl(getBiome(), getOreSettings());
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        final Map<String, Object> serialize = new LinkedHashMap<>();

        serialize.put(BIOME_KEY, getBiome().toString());

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

        return serialize;
    }

}
