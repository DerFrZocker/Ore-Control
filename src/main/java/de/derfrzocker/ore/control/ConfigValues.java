package de.derfrzocker.ore.control;

import de.derfrzocker.ore.control.utils.Config;
import de.derfrzocker.ore.control.utils.YamlReloadAble;
import lombok.NonNull;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class ConfigValues implements YamlReloadAble {

    @NonNull
    private File file;

    @NonNull
    private volatile YamlConfiguration yaml;

    public ConfigValues(File file) {
        reload(file);
    }

    @Override
    public void reload(File file) {
        this.file = file;
        reload();
    }

    @Override
    public void reload() {
        yaml = Config.getConfig(OreControl.getInstance(), file.getName());
    }
}
