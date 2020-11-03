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

package de.derfrzocker.ore.control;

import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.BiomeOreSettings;
import de.derfrzocker.ore.control.api.Ore;
import de.derfrzocker.ore.control.api.OreSettings;
import de.derfrzocker.spigot.utils.ReloadAble;
import de.derfrzocker.spigot.utils.Version;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.logging.Logger;

public class Settings implements ReloadAble {

    @NotNull
    private final Supplier<YamlConfiguration> yamlSupplier;
    @NotNull
    private final Version version;
    @NotNull
    private final Logger logger;
    @NotNull
    private Map<Ore, OreSettings> oreSettings = new HashMap<>();
    @NotNull
    private Map<Biome, BiomeOreSettingsStorage> biomeOreSettingsStorage = new HashMap<>();

    public Settings(@NotNull final Supplier<YamlConfiguration> yamlSupplier, @NotNull final Version version, @NotNull final Logger logger) {
        Validate.notNull(yamlSupplier, "YamlSupplier cannot be null");
        Validate.notNull(version, "Version cannot be null");
        Validate.notNull(logger, "Logger cannot be null");

        this.yamlSupplier = yamlSupplier;
        this.version = version;
        this.logger = logger;
        RELOAD_ABLES.add(this);

        reload();
    }

    @NotNull
    public OreSettings getDefaultSettings(@NotNull final Ore ore) {
        Validate.notNull(ore, "Ore cannot be null");

        final OreSettings oreSettings = this.oreSettings.get(ore);

        if (oreSettings == null) {
            throw new NoSuchDefaultOreSettingsException(ore, "There is no default OreSetting for the Ore '" + ore + "'");
        }

        return oreSettings;
    }

    @NotNull
    public OreSettings getDefaultSettings(@NotNull Biome biome, @NotNull final Ore ore) {
        Validate.notNull(biome, "Biome cannot be null");
        Validate.notNull(ore, "Ore cannot be null");

        final BiomeOreSettingsStorage biomeOreSettingsStorage = this.biomeOreSettingsStorage.get(biome);

        if (biomeOreSettingsStorage == null) {
            return getDefaultSettings(ore);
        }

        final OreSettings oreSettings = biomeOreSettingsStorage.getDefaultSettings(ore);

        if (oreSettings == null) {
            return getDefaultSettings(ore);
        }

        return oreSettings;
    }

    @Override
    public void reload() {
        final YamlConfiguration yaml = yamlSupplier.get();
        final List<?> oreSettings = yaml.getList("defaults.ore-settings");

        final Map<Ore, OreSettings> oreSettingsMap = new HashMap<>();
        final Map<Biome, BiomeOreSettingsStorage> biomeOreSettingsStorageMap = new HashMap<>();

        if (oreSettings == null) {
            throw new IllegalArgumentException("The YamlConfiguration don't have a list under 'defaults.ore-settings'");
        }

        for (final Object oreSettingObject : oreSettings) {
            if (!(oreSettingObject instanceof OreSettings)) {
                throw new IllegalArgumentException("The object '" + oreSettingObject + "' is not an instance of OreSettings");
            }

            final OreSettings oreSetting = (OreSettings) oreSettingObject;
            final Ore ore = oreSetting.getOre();

            // ignoring ore-settings from ores which are not present in the version the server is running
            if (version.isOlderThan(ore.getSince())) {
                continue;
            }

            final OreSettings otherOreSetting = oreSettingsMap.put(ore, oreSetting);

            if (otherOreSetting != null) {
                logger.info("There are multiple OreSettings for the ore '" + ore + "' in the default Ore-Settings YamlConfiguration");
                logger.info("Old OreSetting: " + otherOreSetting);
                logger.info("New OreSetting: " + oreSetting);
            }
        }

        final List<?> biomeOreSettings = yaml.getList("defaults.biome-ore-settings");

        if (biomeOreSettings == null) {
            throw new IllegalArgumentException("The YamlConfiguration don't have a list under 'defaults.biome-ore-settings'");
        }

        for (final Object biomeOreSettingsObject : biomeOreSettings) {
            if (!(biomeOreSettingsObject instanceof BiomeOreSettings)) {
                throw new IllegalArgumentException("The object '" + biomeOreSettings + "' is not an instance of BiomeOreSettings");
            }

            final BiomeOreSettings biomeOreSetting = (BiomeOreSettings) biomeOreSettingsObject;
            final Biome biome = biomeOreSetting.getBiome();

            // ignoring biome-ore-settings from biomes which are not present in the version the server is running
            if (version.isOlderThan(biome.getSince())) {
                continue;
            }

            if (biome.getUntil() != null && version.isNewerThan(biome.getUntil())) {
                continue;
            }

            final BiomeOreSettingsStorage biomeOreSettingsStorage = new BiomeOreSettingsStorage(biomeOreSetting.getOreSettings());
            final BiomeOreSettingsStorage otherBiomeOreSetting = biomeOreSettingsStorageMap.put(biome, biomeOreSettingsStorage);

            if (otherBiomeOreSetting != null) {
                logger.info("There are multiple BiomeOreSettings for the biome '" + biome + "' in the default Biome-Ore-Settings YamlConfiguration");
                logger.info("Old BiomeOreSettings: " + otherBiomeOreSetting);
                logger.info("New BiomeOreSettings: " + biomeOreSettingsStorage);
            }
        }

        this.oreSettings = oreSettingsMap;
        this.biomeOreSettingsStorage = biomeOreSettingsStorageMap;
    }

    private static final class BiomeOreSettingsStorage {

        private final Map<Ore, OreSettings> oreSettings = new HashMap<>();

        private BiomeOreSettingsStorage(@NotNull final Map<Ore, OreSettings> oreSettingsMap) {
            this.oreSettings.putAll(oreSettingsMap);
        }

        @Nullable
        private OreSettings getDefaultSettings(@NotNull final Ore ore) {
            return this.oreSettings.get(ore);
        }

        @Override
        public String toString() {
            return "BiomeOreSettingsStorage{" +
                    "oreSettings=" + oreSettings +
                    '}';
        }

    }

    public static final class NoSuchDefaultOreSettingsException extends RuntimeException {

        @NotNull
        private final Ore ore;

        private NoSuchDefaultOreSettingsException(@NotNull Ore ore, final String message) {
            super(message);

            this.ore = ore;
        }

        @NotNull
        public Ore getOre() {
            return ore;
        }

    }

}
