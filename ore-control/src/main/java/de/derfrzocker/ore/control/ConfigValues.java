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
        return yaml.getBoolean("safe_mode", true);
    }

    public Language getLanguage() {
        return Language.getLanguage(yaml.getString("language", "english"));
    }

    public boolean isTranslateTabCompilation() {
        return yaml.getBoolean("translate_tab_compilation", true);
    }

    public boolean verifyCopyAction() {
        return yaml.getBoolean("verify.copy_action", true);
    }

    public boolean verifyResetAction() {
        return yaml.getBoolean("verify.reset_action", true);
    }

    @Override
    public void reload() {
        yaml = Config.getConfig(OreControl.getInstance(), file.getName());
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public class Set {

        public void setLanguage(final Language language) {
            yaml.set("language", language.toString());

            try {
                yaml.save(file);
            } catch (IOException e) {
                throw new RuntimeException("Error while save config.yml, while set language to: " + language, e);
            }
        }

        public void setSafeMode(final boolean bool) {
            yaml.set("safe_mode", bool);

            try {
                yaml.save(file);
            } catch (IOException e) {
                throw new RuntimeException("Error while save config.yml, while set safe_mode to: " + bool, e);
            }
        }

        public void setTranslateTabCompilation(final boolean bool) {
            yaml.set("translate_tab_compilation", bool);

            try {
                yaml.save(file);
            } catch (IOException e) {
                throw new RuntimeException("Error while save config.yml, while set translate_tab_compilation to: " + bool, e);
            }
        }

        public void setVerifyCopyAction(final boolean bool) {
            yaml.set("verify.copy_action", bool);

            try {
                yaml.save(file);
            } catch (IOException e) {
                throw new RuntimeException("Error while save config.yml, while set verify.copy_action to: " + bool, e);
            }
        }

        public void setVerifyResetAction(final boolean bool) {
            yaml.set("verify.reset_action", bool);

            try {
                yaml.save(file);
            } catch (IOException e) {
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
            return yaml.getDefaultSection().getBoolean("safe_mode", true);
        }

        public boolean defaultTranslateTabCompilation() {
            return yaml.getDefaultSection().getBoolean("translate_tab_compilation", true);
        }

        public boolean defaultVerifyCopyAction() {
            return yaml.getDefaultSection().getBoolean("verify.copy_action", true);
        }

        public boolean defaultVerifyResetAction() {
            return yaml.getDefaultSection().getBoolean("verify.reset_action", true);
        }

    }

}
