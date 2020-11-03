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

import de.derfrzocker.spigot.utils.Config;
import de.derfrzocker.spigot.utils.Language;
import de.derfrzocker.spigot.utils.ReloadAble;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public class ConfigValues implements ReloadAble {

    public final Set SET = new Set();
    public final Default DEFAULT = new Default();
    @NotNull
    private File file;
    private volatile YamlConfiguration yaml;

    public ConfigValues(@NotNull final File file) {
        Validate.notNull(file, "File cannot be null");

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

    public boolean showWelcomeMessage() {
        return yaml.getBoolean("show-welcome-message", true);
    }

    @Override
    public void reload() {
        yaml = Config.getConfig(OreControl.getInstance(), file.getName());
    }

    public class Set {

        private Set() {

        }

        public void setLanguage(@NotNull final Language language) {
            Validate.notNull(language, "Language cannot be null");

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

        public void setShowWelcomeMessage(final boolean bool) {
            yaml.set("show-welcome-message", bool);

            try {
                yaml.save(file);
            } catch (final IOException e) {
                throw new RuntimeException("Error while save config.yml, while set verify.reset_action to: " + bool, e);
            }
        }

    }

    public class Default {

        private Default() {

        }

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

        public boolean defaultShowWelcomeMessage() {
            return yaml.getDefaultSection().getBoolean("show-welcome-message", true);
        }

    }

}
