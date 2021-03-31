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

package de.derfrzocker.ore.control.gui.settings;

import de.derfrzocker.spigot.utils.Config;
import de.derfrzocker.spigot.utils.Version;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.function.Supplier;

public class GuiSettings {

    @NotNull
    private final Supplier<BiomeGuiSettings> biomeGuiSettings;
    @NotNull
    private final Supplier<BooleanGuiSetting> booleanGuiSetting;
    @NotNull
    private final Supplier<ConfigGuiSettings> configGuiSettings;
    @NotNull
    private final Supplier<LanguageGuiSettings> languageGuiSettings;
    @NotNull
    private final Supplier<OreGuiSettings> oreGuiSettings;
    @NotNull
    private final Supplier<OreSettingsGuiSettings> oreSettingsGuiSettings;
    @NotNull
    private final Supplier<SettingsGuiSettings> settingsGuiSettings;
    @NotNull
    private final Supplier<WorldConfigGuiSettings> worldConfigGuiSettings;
    @NotNull
    private final Supplier<WorldGuiSettings> worldGuiSettings;

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

        this.biomeGuiSettings = () -> biomeGuiSettings;
        this.booleanGuiSetting = () -> booleanGuiSetting;
        this.configGuiSettings = () -> configGuiSettings;
        this.languageGuiSettings = () -> languageGuiSettings;
        this.oreGuiSettings = () -> oreGuiSettings;
        this.oreSettingsGuiSettings = () -> oreSettingsGuiSettings;
        this.settingsGuiSettings = () -> settingsGuiSettings;
        this.worldConfigGuiSettings = () -> worldConfigGuiSettings;
        this.worldGuiSettings = () -> worldGuiSettings;
    }

    public GuiSettings(@NotNull final Plugin plugin, @NotNull final String directory, @NotNull final Version version) {
        Validate.notNull(plugin, "Plugin cannot be null");
        Validate.notNull(directory, "Directory cannot be null");
        Validate.notNull(version, "Version cannot be null");

        this.booleanGuiSetting = new SettingsSupplier<BooleanGuiSetting>() {
            @NotNull
            @Override
            protected BooleanGuiSetting get0() {
                checkFile(plugin, directory + "/boolean-gui.yml");
                return new BooleanGuiSetting(plugin, directory + "/boolean-gui.yml", true);
            }
        };

        this.configGuiSettings = new SettingsSupplier<ConfigGuiSettings>() {
            @NotNull
            @Override
            protected ConfigGuiSettings get0() {
                checkFile(plugin, directory + "/config-gui.yml");
                return new ConfigGuiSettings(plugin, directory + "/config-gui.yml", true);
            }
        };

        this.languageGuiSettings = new SettingsSupplier<LanguageGuiSettings>() {
            @NotNull
            @Override
            protected LanguageGuiSettings get0() {
                checkFile(plugin, directory + "/language-gui.yml");
                return new LanguageGuiSettings(plugin, directory + "/language-gui.yml", true);
            }
        };

        this.settingsGuiSettings = new SettingsSupplier<SettingsGuiSettings>() {
            @NotNull
            @Override
            protected SettingsGuiSettings get0() {
                checkFile(plugin, directory + "/settings-gui.yml");
                return new SettingsGuiSettings(plugin, directory + "/settings-gui.yml", true);
            }
        };

        this.worldConfigGuiSettings = new SettingsSupplier<WorldConfigGuiSettings>() {
            @NotNull
            @Override
            protected WorldConfigGuiSettings get0() {
                checkFile(plugin, directory + "/world-config-gui.yml");
                return new WorldConfigGuiSettings(plugin, directory + "/world-config-gui.yml", true);
            }
        };

        if (version == Version.v1_13_R1 || version == Version.v1_13_R2) {
            this.biomeGuiSettings = new SettingsSupplier<BiomeGuiSettings>() {
                @NotNull
                @Override
                protected BiomeGuiSettings get0() {
                    checkFile(plugin, directory + "/biome-gui_v1.13.yml");
                    return new BiomeGuiSettings(plugin, directory + "/biome-gui_v1.13.yml", true);
                }
            };

            this.oreGuiSettings = new SettingsSupplier<OreGuiSettings>() {
                @NotNull
                @Override
                protected OreGuiSettings get0() {
                    checkFile(plugin, directory + "/ore-gui_v1.13.yml");
                    return new OreGuiSettings(plugin, directory + "/ore-gui_v1.13.yml", true);
                }
            };

            this.oreSettingsGuiSettings = new SettingsSupplier<OreSettingsGuiSettings>() {
                @NotNull
                @Override
                protected OreSettingsGuiSettings get0() {
                    checkFile(plugin, directory + "/ore-settings-gui_v1.13.yml");
                    return new OreSettingsGuiSettings(plugin, directory + "/ore-settings-gui_v1.13.yml", true);
                }
            };

            this.worldGuiSettings = new SettingsSupplier<WorldGuiSettings>() {
                @NotNull
                @Override
                protected WorldGuiSettings get0() {
                    checkFile(plugin, directory + "/world-gui_v1.13.yml");
                    return new WorldGuiSettings(plugin, directory + "/world-gui_v1.13.yml", true);
                }
            };
        } else {
            this.biomeGuiSettings = new SettingsSupplier<BiomeGuiSettings>() {
                @NotNull
                @Override
                protected BiomeGuiSettings get0() {
                    checkFile(plugin, directory + "/biome-gui.yml");
                    final BiomeGuiSettings settings = new BiomeGuiSettings(plugin, directory + "/biome-gui.yml", true);

                    if (version.isNewerOrSameThan(Version.v1_14_R1)) {
                        checkFile(plugin, directory + "/biome-gui_v1.14.yml");
                        settings.addValues(directory + "/biome-gui_v1.14.yml", true);
                    }

                    if (version.isNewerOrSameThan(Version.v1_16_R1)) {
                        checkFile(plugin, directory + "/biome-gui_v1.16.yml");
                        settings.addValues(directory + "/biome-gui_v1.16.yml", true);
                    }

                    return settings;
                }
            };

            this.oreGuiSettings = new SettingsSupplier<OreGuiSettings>() {
                @NotNull
                @Override
                protected OreGuiSettings get0() {
                    checkFile(plugin, directory + "/ore-gui.yml");
                    return new OreGuiSettings(plugin, directory + "/ore-gui.yml", true);
                }
            };

            this.oreSettingsGuiSettings = new SettingsSupplier<OreSettingsGuiSettings>() {
                @NotNull
                @Override
                protected OreSettingsGuiSettings get0() {
                    checkFile(plugin, directory + "/ore-settings-gui.yml");
                    return new OreSettingsGuiSettings(plugin, directory + "/ore-settings-gui.yml", true);
                }
            };

            this.worldGuiSettings = new SettingsSupplier<WorldGuiSettings>() {
                @NotNull
                @Override
                protected WorldGuiSettings get0() {
                    checkFile(plugin, directory + "/world-gui.yml");
                    return new WorldGuiSettings(plugin, directory + "/world-gui.yml", true);
                }
            };

        }
    }

    @NotNull
    public BiomeGuiSettings getBiomeGuiSettings() {
        return this.biomeGuiSettings.get();
    }

    @NotNull
    public BooleanGuiSetting getBooleanGuiSetting() {
        return this.booleanGuiSetting.get();
    }

    @NotNull
    public ConfigGuiSettings getConfigGuiSettings() {
        return this.configGuiSettings.get();
    }

    @NotNull
    public LanguageGuiSettings getLanguageGuiSettings() {
        return this.languageGuiSettings.get();
    }

    @NotNull
    public OreGuiSettings getOreGuiSettings() {
        return this.oreGuiSettings.get();
    }

    @NotNull
    public OreSettingsGuiSettings getOreSettingsGuiSettings() {
        return this.oreSettingsGuiSettings.get();
    }

    @NotNull
    public SettingsGuiSettings getSettingsGuiSettings() {
        return this.settingsGuiSettings.get();
    }

    @NotNull
    public WorldConfigGuiSettings getWorldConfigGuiSettings() {
        return this.worldConfigGuiSettings.get();
    }

    @NotNull
    public WorldGuiSettings getWorldGuiSettings() {
        return this.worldGuiSettings.get();
    }


    private abstract static class SettingsSupplier<T> implements Supplier<T> {

        @Nullable
        private T setting;

        @Override
        public T get() {
            if (setting == null) {
                setting = get0();
            }

            return setting;
        }

        @NotNull
        protected abstract T get0();

    }

    private void checkFile(@NotNull Plugin plugin, @NotNull final String name) {
        final File file = new File(plugin.getDataFolder(), name);

        if (!file.exists()) {
            return;
        }

        final YamlConfiguration configuration = new Config(file);

        final YamlConfiguration configuration2 = new Config(plugin.getResource(name));

        if (configuration.getInt("version") == configuration2.getInt("version"))
            return;

        plugin.getLogger().warning("File " + name + " has an outdated / new version, replacing it!");

        if (!file.delete()) {
            throw new RuntimeException("can't delete file " + name + " stop plugin start!");
        }

        plugin.saveResource(name, true);
    }

}
