package de.derfrzocker.ore.control.gui;

import de.derfrzocker.ore.control.utils.Config;
import de.derfrzocker.ore.control.utils.ReloadAble;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class BasicSettings implements ReloadAble {

    @Getter
    private YamlConfiguration yaml;

    private final String file;

    private final JavaPlugin plugin;

    public BasicSettings(final @NonNull JavaPlugin plugin, final @NonNull String file) {
        this.file = file;
        this.plugin = plugin;
        yaml = Config.getConfig(plugin, file);
        RELOAD_ABLES.add(this);
    }

    public String getInventoryName() {
        return yaml.getString("inventory.name");
    }

    public int getRows() {
        return yaml.getInt("inventory.rows");
    }

    @Override
    public void reload() {
        yaml = Config.getConfig(plugin, file);
    }

}
