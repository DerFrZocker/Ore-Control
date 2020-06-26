/*
 * MIT License
 *
 * Copyright (c) 2019 Marvin (DerFrZocker)
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
 */

package de.derfrzocker.ore.control.impl;

import com.google.common.collect.Sets;
import de.derfrzocker.ore.control.api.*;
import de.derfrzocker.ore.control.api.dao.WorldOreConfigDao;
import org.apache.commons.lang.Validate;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;

public abstract class OreControlServiceImpl implements OreControlService {

    @NotNull
    private final NMSService nmsService;
    @NotNull
    private final WorldOreConfigDao dao;

    public OreControlServiceImpl(@NotNull final NMSService nmsService, @NotNull final WorldOreConfigDao dao) {
        Validate.notNull(nmsService, "NMSService can not be null");
        Validate.notNull(dao, "WorldOreConfigDao can not be null");

        this.nmsService = nmsService;
        this.dao = dao;
    }

    @NotNull
    protected abstract OreSettings getDefaultOreSetting(@NotNull Ore ore);

    @NotNull
    protected abstract OreSettings getDefaultOreSetting(@NotNull Biome biome, @NotNull Ore ore);

    @NotNull
    protected abstract OreSettings getNewOreSetting(@NotNull Ore ore);

    @NotNull
    protected abstract WorldOreConfig getNewWorldOreConfig(@NotNull String name, boolean template);

    @NotNull
    protected abstract BiomeOreSettings getNewBiomeOreSettings(@NotNull Biome biome);

    @NotNull
    @Override
    public NMSService getNMSService() {
        return this.nmsService;
    }

    @NotNull
    @Override
    public Optional<WorldOreConfig> getWorldOreConfig(@NotNull final World world) {
        Validate.notNull(world, "World can not be null");

        return this.dao.get(world.getName());
    }

    @NotNull
    @Override
    public Optional<WorldOreConfig> getWorldOreConfig(@NotNull final String name) {
        Validate.notNull(name, "Name can not be null");

        return this.dao.get(name);
    }

    @NotNull
    @Override
    public WorldOreConfig createWorldOreConfig(@NotNull final World world) {
        Validate.notNull(world, "World can not be null");

        final WorldOreConfig worldOreConfig = getNewWorldOreConfig(world.getName(), false);

        saveWorldOreConfig(worldOreConfig);

        return worldOreConfig;
    }

    @NotNull
    @Override
    public WorldOreConfig createWorldOreConfigTemplate(@NotNull final String name) {
        Validate.notNull(name, "Name can not be null");
        Validate.notEmpty(name, "Name can not be empty");
        Validate.notEmpty(name.trim(), "Name cannot consist of only spaces");

        final WorldOreConfig worldOreConfig = getNewWorldOreConfig(name, true);

        saveWorldOreConfig(worldOreConfig);

        return worldOreConfig;
    }

    @Override
    public void saveWorldOreConfig(@NotNull final WorldOreConfig config) {
        Validate.notNull(config, "WorldOreConfig can not be null");

        this.dao.save(config);
    }

    @Override
    public void removeWorldOreConfig(@NotNull final WorldOreConfig config) {
        Validate.notNull(config, "WorldOreConfig can not be null");

        this.dao.remove(config);
    }

    @NotNull
    @Override
    public Set<WorldOreConfig> getAllWorldOreConfigs() {
        return this.dao.getAll();
    }

    @Override
    public double getDefaultValue(@NotNull final Ore ore, @NotNull final Setting setting) {
        Validate.notNull(ore, "Ore can not be null");
        Validate.notNull(setting, "Setting can not be null");
        Validate.isTrue(Sets.newHashSet(ore.getSettings()).contains(setting), "The Ore '" + ore + "' dont have the Setting '" + setting + "'");

        return getDefaultValue0(ore, setting);
    }

    @Override
    public double getDefaultValue(@NotNull Biome biome, @NotNull Ore ore, @NotNull Setting setting) {
        Validate.notNull(ore, "Biome can not be null");
        Validate.notNull(ore, "Ore can not be null");
        Validate.notNull(setting, "Setting can not be null");
        Validate.isTrue(Sets.newHashSet(biome.getOres()).contains(ore), "The Biome '" + biome + "' dont have the Ore '" + ore + "'");
        Validate.isTrue(Sets.newHashSet(ore.getSettings()).contains(setting), "The Ore '" + ore + "' dont have the Setting '" + setting + "'");

        return getDefaultValue0(biome, ore, setting);
    }

    @Override
    public double getValue(@NotNull final WorldOreConfig worldOreConfig, @NotNull final Biome biome, @NotNull final Ore ore, @NotNull final Setting setting) {
        Validate.notNull(worldOreConfig, "WorldOreConfig can not be null");
        Validate.notNull(biome, "Biome can not be null");
        Validate.notNull(ore, "Ore can not be null");
        Validate.notNull(setting, "Setting can not be null");
        Validate.isTrue(Sets.newHashSet(biome.getOres()).contains(ore), "The Biome '" + biome + "' dont have the Ore '" + ore + "'");
        Validate.isTrue(Sets.newHashSet(ore.getSettings()).contains(setting), "The Ore '" + ore + "' dont have the Setting '" + setting + "'");

        final Optional<BiomeOreSettings> biomeOreSettings = worldOreConfig.getBiomeOreSettings(biome);

        // Check first in BiomeOreSetting
        if (biomeOreSettings.isPresent()) {
            final Optional<OreSettings> oreSettingsOptional = biomeOreSettings.get().getOreSettings(ore);

            //checking if OreSetting in BiomeOreSetting is present
            if (oreSettingsOptional.isPresent()) {
                final Optional<Double> valueOptional = oreSettingsOptional.get().getValue(setting);

                if (valueOptional.isPresent()) {
                    // value present, returning Biome specific value
                    return valueOptional.get();
                }
            }
        }

        // Now checking for WorldOreConfig specific value
        return getValue0(worldOreConfig, biome, ore, setting);
    }

    @Override
    public double getValue(@NotNull final WorldOreConfig worldOreConfig, @NotNull final Ore ore, @NotNull final Setting setting) {
        Validate.notNull(worldOreConfig, "WorldOreConfig can not be null");
        Validate.notNull(ore, "Ore can not be null");
        Validate.notNull(setting, "Setting can not be null");
        Validate.isTrue(Sets.newHashSet(ore.getSettings()).contains(setting), "The Ore '" + ore + "' dont have the Setting '" + setting + "'");

        return getValue0(worldOreConfig, null, ore, setting);
    }

    @Override
    public void setValue(@NotNull final WorldOreConfig worldOreConfig, @NotNull final Biome biome, @NotNull final Ore ore, @NotNull final Setting setting, final double value) {
        Validate.notNull(worldOreConfig, "WorldOreConfig can not be null");
        Validate.notNull(biome, "Biome can not be null");
        Validate.notNull(ore, "Ore can not be null");
        Validate.notNull(setting, "Setting can not be null");
        Validate.isTrue(Sets.newHashSet(biome.getOres()).contains(ore), "The Biome '" + biome + "' dont have the Ore '" + ore + "'");
        Validate.isTrue(Sets.newHashSet(ore.getSettings()).contains(setting), "The Ore '" + ore + "' dont have the Setting '" + setting + "'");

        final Optional<BiomeOreSettings> biomeOreSettingsOptional = worldOreConfig.getBiomeOreSettings(biome);
        final BiomeOreSettings biomeOreSettings;

        if (biomeOreSettingsOptional.isPresent()) {
            biomeOreSettings = biomeOreSettingsOptional.get();
        } else {
            biomeOreSettings = getNewBiomeOreSettings(biome);
            worldOreConfig.setBiomeOreSettings(biomeOreSettings);
        }

        final Optional<OreSettings> oreSettingsOptional = biomeOreSettings.getOreSettings(ore);
        final OreSettings oreSettings;

        if (oreSettingsOptional.isPresent()) {
            oreSettings = oreSettingsOptional.get();
        } else {
            oreSettings = getNewOreSetting(ore);
            biomeOreSettings.setOreSettings(oreSettings);
        }

        oreSettings.setValue(setting, value);
    }

    @Override
    public void setValue(@NotNull final WorldOreConfig worldOreConfig, @NotNull final Ore ore, @NotNull final Setting setting, final double value) {
        Validate.notNull(worldOreConfig, "WorldOreConfig can not be null");
        Validate.notNull(ore, "Ore can not be null");
        Validate.notNull(setting, "Setting can not be null");
        Validate.isTrue(Sets.newHashSet(ore.getSettings()).contains(setting), "The Ore '" + ore + "' dont have the Setting '" + setting + "'");

        final Optional<OreSettings> oreSettingsOptional = worldOreConfig.getOreSettings(ore);
        final OreSettings oreSettings;

        if (oreSettingsOptional.isPresent()) {
            oreSettings = oreSettingsOptional.get();
        } else {
            oreSettings = getNewOreSetting(ore);
            worldOreConfig.setOreSettings(oreSettings);
        }

        oreSettings.setValue(setting, value);
    }

    @Override
    public boolean isActivated(@NotNull final WorldOreConfig worldOreConfig, @NotNull final Biome biome, @NotNull final Ore ore) {
        final Optional<BiomeOreSettings> biomeOreSettings = worldOreConfig.getBiomeOreSettings(biome);

        if (biomeOreSettings.isPresent()) {
            final Optional<OreSettings> oreSettings = biomeOreSettings.get().getOreSettings(ore);

            return oreSettings.map(OreSettings::isActivated).orElse(true);
        }

        final Optional<OreSettings> oreSettings = worldOreConfig.getOreSettings(ore);

        return oreSettings.map(OreSettings::isActivated).orElse(true);
    }

    @Override
    public boolean isActivated(@NotNull final WorldOreConfig worldOreConfig, @NotNull final Ore ore) {
        final Optional<OreSettings> oreSettings = worldOreConfig.getOreSettings(ore);

        return oreSettings.map(OreSettings::isActivated).orElse(true);
    }

    @Override
    public void setActivated(@NotNull final WorldOreConfig worldOreConfig, @NotNull final Biome biome, @NotNull final Ore ore, final boolean value) {
        Validate.notNull(worldOreConfig, "WorldOreConfig can not be null");
        Validate.notNull(biome, "Biome can not be null");
        Validate.notNull(ore, "Ore can not be null");
        Validate.isTrue(Sets.newHashSet(biome.getOres()).contains(ore), "The Biome '" + biome + "' dont have the Ore '" + ore + "'");

        final Optional<BiomeOreSettings> biomeOreSettingsOptional = worldOreConfig.getBiomeOreSettings(biome);
        final BiomeOreSettings biomeOreSettings;

        if (biomeOreSettingsOptional.isPresent()) {
            biomeOreSettings = biomeOreSettingsOptional.get();
        } else {
            biomeOreSettings = getNewBiomeOreSettings(biome);
            worldOreConfig.setBiomeOreSettings(biomeOreSettings);
        }

        final Optional<OreSettings> oreSettingsOptional = biomeOreSettings.getOreSettings(ore);
        final OreSettings oreSettings;

        if (oreSettingsOptional.isPresent()) {
            oreSettings = oreSettingsOptional.get();
        } else {
            oreSettings = getNewOreSetting(ore);
            biomeOreSettings.setOreSettings(oreSettings);
        }

        oreSettings.setActivated(value);
    }

    @Override
    public void setActivated(@NotNull final WorldOreConfig worldOreConfig, @NotNull final Ore ore, final boolean value) {
        Validate.notNull(worldOreConfig, "WorldOreConfig can not be null");
        Validate.notNull(ore, "Ore can not be null");

        final Optional<OreSettings> oreSettingsOptional = worldOreConfig.getOreSettings(ore);
        final OreSettings oreSettings;

        if (oreSettingsOptional.isPresent()) {
            oreSettings = oreSettingsOptional.get();
        } else {
            oreSettings = getNewOreSetting(ore);
            worldOreConfig.setOreSettings(oreSettings);
        }

        oreSettings.setActivated(value);
    }

    @Override
    public boolean isOre(@Nullable final String string) {
        if (string == null)
            return false;

        try {
            Ore.valueOf(string.toUpperCase());
            return true;
        } catch (final IllegalArgumentException e) {
            return false;
        }

    }

    @Override
    public boolean isBiome(@Nullable final String string) {
        if (string == null)
            return false;

        try {
            Biome.valueOf(string.toUpperCase());
            return true;
        } catch (final IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public boolean isSetting(@Nullable final String string) {
        if (string == null)
            return false;

        try {
            Setting.valueOf(string.toUpperCase());
            return true;
        } catch (final IllegalArgumentException e) {
            return false;
        }
    }

    @NotNull
    private OreSettings getDefault(@NotNull final Ore ore) {
        final OreSettings oreSettings = getDefaultOreSetting(ore);

        Validate.notNull(oreSettings, "Default OreSettings for the ore '" + ore + "' is null, this should never happen");

        return oreSettings;
    }

    @NotNull
    private OreSettings getDefault(@NotNull Biome biome, @NotNull final Ore ore) {
        final OreSettings oreSettings = getDefaultOreSetting(biome, ore);

        Validate.notNull(oreSettings, "Default OreSettings for the biome '" + biome + "' and the ore '" + ore + "' is null, this should never happen");

        return oreSettings;
    }

    private double getDefaultValue0(@NotNull final Ore ore, @NotNull final Setting setting) {
        final OreSettings oreSettings = getDefault(ore);

        final Optional<Double> value = oreSettings.getValue(setting);

        if (value.isPresent()) {
            return value.get();
        }

        //Something went wrong, this means there is now default value for the given Ore and Setting, this is probably a configuration problem for the default settings
        throw new RuntimeException("The default OreSettings for the Ore '" + ore + "' dont contains a default value for the Setting '" + setting + "'");
    }

    private double getDefaultValue0(@NotNull Biome biome, @NotNull final Ore ore, @NotNull final Setting setting) {
        final OreSettings oreSettings = getDefault(biome, ore);

        final Optional<Double> value = oreSettings.getValue(setting);

        if (value.isPresent()) {
            return value.get();
        }

        //Something went wrong, this means there is now default value for the given Ore and Setting, this is probably a configuration problem for the default settings
        throw new RuntimeException("The default OreSettings for the Biome '" + biome + "' and the Ore '" + ore + "' dont contains a default value for the Setting '" + setting + "'");
    }

    private double getValue0(@NotNull final WorldOreConfig worldOreConfig, @Nullable Biome biome, @NotNull final Ore ore, @NotNull final Setting setting) {
        // Checking first for WorldOreConfig specific value
        final Optional<OreSettings> oreSettingsOptional = worldOreConfig.getOreSettings(ore);

        //checking if OreSetting in WorldOreConfig is present
        if (oreSettingsOptional.isPresent()) {
            final Optional<Double> valueOptional = oreSettingsOptional.get().getValue(setting);

            if (valueOptional.isPresent()) {
                // value present, returning WorldOreConfig specific value
                return valueOptional.get();
            }
        }

        // If a Biome is present, check for biome specific default values
        if (biome != null) {
            return getDefaultValue0(biome, ore, setting);
        }

        //Now checking for a default value
        return getDefaultValue0(ore, setting);
    }

}
