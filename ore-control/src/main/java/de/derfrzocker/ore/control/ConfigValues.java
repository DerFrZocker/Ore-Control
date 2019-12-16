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

package de.derfrzocker.ore.control;

import de.derfrzocker.spigot.utils.Config;
import de.derfrzocker.spigot.utils.Language;
import de.derfrzocker.spigot.utils.ReloadAble;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigValues implements ReloadAble {

    @NonNull
    private File file;

    public final Set SET = new Set();

    public final Default DEFAULT = new Default();

    @NonNull
    private volatile YamlConfiguration yaml;

    public ConfigValues(File file) {
        this.file = file;
        reload();
        RELOAD_ABLES.add(this);
    }

    public boolean isSafeMode() {
        return yaml.getBoolean("safe-mode", true);
    }

    public Language getLanguage() {
        return Language.getLanguage(yaml.getString("language", "english"));
    }

    public boolean isTranslateTabCompilation() {
        return yaml.getBoolean("translate-tab-compilation", true);
    }

    public boolean verifyCopyAction() {
        return yaml.getBoolean("verify.copy-action", true);
    }

    public boolean verifyResetAction() {
        return yaml.getBoolean("verify.reset-action", true);
    }

    @Override
    public void reload() {
        yaml = Config.getConfig(OreControl.getInstance(), file.getName());
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public class Set {

        public void setLanguage(final @NonNull Language language) {
            yaml.set("language", language.toString());

            try {
                yaml.save(file);
            } catch (final IOException e) {
                throw new RuntimeException("Error while save config.yml, while set language to: " + language, e);
            }
        }

        public void setSafeMode(final boolean bool) {
            yaml.set("safe-mode", bool);

            try {
                yaml.save(file);
            } catch (final IOException e) {
                throw new RuntimeException("Error while save config.yml, while set safe_mode to: " + bool, e);
            }
        }

        public void setTranslateTabCompilation(final boolean bool) {
            yaml.set("translate-tab-compilation", bool);

            try {
                yaml.save(file);
            } catch (final IOException e) {
                throw new RuntimeException("Error while save config.yml, while set translate_tab_compilation to: " + bool, e);
            }
        }

        public void setVerifyCopyAction(final boolean bool) {
            yaml.set("verify.copy-action", bool);

            try {
                yaml.save(file);
            } catch (final IOException e) {
                throw new RuntimeException("Error while save config.yml, while set verify.copy_action to: " + bool, e);
            }
        }

        public void setVerifyResetAction(final boolean bool) {
            yaml.set("verify.reset-action", bool);

            try {
                yaml.save(file);
            } catch (final IOException e) {
                throw new RuntimeException("Error while save config.yml, while set verify.reset_action to: " + bool, e);
            }
        }

    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public class Default {

        public Language defaultLanguage() {
            return Language.getLanguage(yaml.getDefaultSection().getString("language"));
        }

        public boolean defaultSafeMode() {
            return yaml.getDefaultSection().getBoolean("safe-mode", true);
        }

        public boolean defaultTranslateTabCompilation() {
            return yaml.getDefaultSection().getBoolean("translate-tab-compilation", true);
        }

        public boolean defaultVerifyCopyAction() {
            return yaml.getDefaultSection().getBoolean("verify.copy-action", true);
        }

        public boolean defaultVerifyResetAction() {
            return yaml.getDefaultSection().getBoolean("verify.reset-action", true);
        }

    }

}
