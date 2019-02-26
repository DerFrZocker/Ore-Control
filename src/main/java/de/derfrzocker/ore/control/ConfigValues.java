package de.derfrzocker.ore.control;

import de.derfrzocker.ore.control.utils.Config;
import de.derfrzocker.ore.control.utils.Language;
import de.derfrzocker.ore.control.utils.ReloadAble;
import lombok.NonNull;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class ConfigValues implements ReloadAble {

    @NonNull
    private File file;

    @NonNull
    private volatile YamlConfiguration yaml;

    public ConfigValues(File file) {
        this.file = file;
        reload();
        RELOAD_ABLES.add(this);
    }

    public boolean isSaveMode() {
        return yaml.getBoolean("save_mode", true);
    }

    public Language getLanguage() {
        return Language.getLanguage(yaml.getString("language"));
    }

    public boolean isTranslateTabCompilation() {
        return yaml.getBoolean("translate_tab_compilation", true);
    }

    @Override
    public void reload() {
        yaml = Config.getConfig(OreControl.getInstance(), file.getName());
    }

}
