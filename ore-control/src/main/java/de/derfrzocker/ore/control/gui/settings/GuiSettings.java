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

package de.derfrzocker.ore.control.gui.settings;

import de.derfrzocker.spigot.utils.Version;
import org.apache.commons.lang.Validate;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class GuiSettings {

    @NotNull
    private final BiomeGuiSettings biomeGuiSettings;
    @NotNull
    private final BooleanGuiSetting booleanGuiSetting;
    @NotNull
    private final ConfigGuiSettings configGuiSettings;
    @NotNull
    private final LanguageGuiSettings languageGuiSettings;
    @NotNull
    private final OreGuiSettings oreGuiSettings;
    @NotNull
    private final OreSettingsGuiSettings oreSettingsGuiSettings;
    @NotNull
    private final SettingsGuiSettings settingsGuiSettings;
    @NotNull
    private final WorldConfigGuiSettings worldConfigGuiSettings;
    @NotNull
    private final WorldGuiSettings worldGuiSettings;

    public GuiSettings(@NotNull final BiomeGuiSettings biomeGuiSettings, @NotNull final BooleanGuiSetting booleanGuiSetting, @NotNull final ConfigGuiSettings configGuiSettings, @NotNull final LanguageGuiSettings languageGuiSettings, @NotNull final OreGuiSettings oreGuiSettings, @NotNull final OreSettingsGuiSettings oreSettingsGuiSettings, @NotNull final SettingsGuiSettings settingsGuiSettings, @NotNull final WorldConfigGuiSettings worldConfigGuiSettings, @NotNull final WorldGuiSettings worldGuiSettings) {
        Validate.notNull(biomeGuiSettings, "BiomeGuiSettings cannot be null");
        Validate.notNull(booleanGuiSetting, "BooleanGuiSetting cannot be null");
        Validate.notNull(configGuiSettings, "ConfigGuiSettings cannot be null");
        Validate.notNull(languageGuiSettings, "LanguageGuiSettings cannot be null");
        Validate.notNull(oreGuiSettings, "OreGuiSettings cannot be null");
        Validate.notNull(oreSettingsGuiSettings, "OreSettingsGuiSettings cannot be null");
        Validate.notNull(settingsGuiSettings, "SettingsGuiSettings cannot be null");
        Validate.notNull(worldConfigGuiSettings, "WorldConfigGuiSettings cannot be null");
        Validate.notNull(worldGuiSettings, "WorldGuiSettings cannot be null");

        this.biomeGuiSettings = biomeGuiSettings;
        this.booleanGuiSetting = booleanGuiSetting;
        this.configGuiSettings = configGuiSettings;
        this.languageGuiSettings = languageGuiSettings;
        this.oreGuiSettings = oreGuiSettings;
        this.oreSettingsGuiSettings = oreSettingsGuiSettings;
        this.settingsGuiSettings = settingsGuiSettings;
        this.worldConfigGuiSettings = worldConfigGuiSettings;
        this.worldGuiSettings = worldGuiSettings;
    }

    public GuiSettings(@NotNull final JavaPlugin javaPlugin, @NotNull final File directory, @NotNull final Version version) {
        Validate.notNull(javaPlugin, "JavaPlugin cannot be null");
        Validate.notNull(directory, "Directory cannot be null");
        Validate.isTrue(directory.exists(), "Directory '" + directory + "' does not exist");
        Validate.isTrue(directory.isDirectory(), "Directory '" + directory + "' dis not a directory");
        Validate.notNull(version, "Version cannot be null");

        this.booleanGuiSetting = new BooleanGuiSetting(javaPlugin, new File(directory, "boolean-gui.yml").toString(), true);
        this.configGuiSettings = new ConfigGuiSettings(javaPlugin, new File(directory, "config-gui.yml").toString(), true);
        this.languageGuiSettings = new LanguageGuiSettings(javaPlugin, new File(directory, "language-gui.yml").toString(), true);
        this.settingsGuiSettings = new SettingsGuiSettings(javaPlugin, new File(directory, "settings-gui.yml").toString(), true);
        this.worldConfigGuiSettings = new WorldConfigGuiSettings(javaPlugin, new File(directory, "world-config-gui.yml").toString(), true);

        if (version == Version.v1_13_R1 || version == Version.v1_13_R2) {
            this.biomeGuiSettings = new BiomeGuiSettings(javaPlugin, new File(directory, "biome-gui_v1.13.yml").toString(), true);
            this.oreGuiSettings = new OreGuiSettings(javaPlugin, new File(directory, "ore-gui_v1.13.yml").toString(), true);
            this.oreSettingsGuiSettings = new OreSettingsGuiSettings(javaPlugin, new File(directory, "ore-settings-gui_v1.13.yml").toString(), true);
            this.worldGuiSettings = new WorldGuiSettings(javaPlugin, new File(directory, "world-gui_v1.13.yml").toString(), true);

        } else {
            this.biomeGuiSettings = new BiomeGuiSettings(javaPlugin, new File(directory, "biome-gui.yml").toString(), true);
            this.oreGuiSettings = new OreGuiSettings(javaPlugin, new File(directory, "ore-gui.yml").toString(), true);
            this.oreSettingsGuiSettings = new OreSettingsGuiSettings(javaPlugin, new File(directory, "ore-settings-gui.yml").toString(), true);
            this.worldGuiSettings = new WorldGuiSettings(javaPlugin, new File(directory, "world-gui.yml").toString(), true);

            if (Version.v1_14_R1.isNewerOrSameVersion(version)) {
                this.biomeGuiSettings.addValues(new File(directory, "biome-gui_v1.14.yml").toString(), true);
            }

            if (Version.v1_16_R1.isNewerOrSameVersion(version)) {
                this.biomeGuiSettings.addValues(new File(directory, "biome-gui_v1.16.yml").toString(), true);
            }
        }
    }

    @NotNull
    public BiomeGuiSettings getBiomeGuiSettings() {
        return this.biomeGuiSettings;
    }

    @NotNull
    public BooleanGuiSetting getBooleanGuiSetting() {
        return this.booleanGuiSetting;
    }

    @NotNull
    public ConfigGuiSettings getConfigGuiSettings() {
        return this.configGuiSettings;
    }

    @NotNull
    public LanguageGuiSettings getLanguageGuiSettings() {
        return this.languageGuiSettings;
    }

    @NotNull
    public OreGuiSettings getOreGuiSettings() {
        return this.oreGuiSettings;
    }

    @NotNull
    public OreSettingsGuiSettings getOreSettingsGuiSettings() {
        return this.oreSettingsGuiSettings;
    }

    @NotNull
    public SettingsGuiSettings getSettingsGuiSettings() {
        return this.settingsGuiSettings;
    }

    @NotNull
    public WorldConfigGuiSettings getWorldConfigGuiSettings() {
        return this.worldConfigGuiSettings;
    }

    @NotNull
    public WorldGuiSettings getWorldGuiSettings() {
        return this.worldGuiSettings;
    }

}
