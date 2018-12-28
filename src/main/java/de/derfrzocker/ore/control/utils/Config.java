package de.derfrzocker.ore.control.utils;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import lombok.NonNull;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;

import static java.lang.String.format;

/**
 * This is the Config class<br>
 * <br>
 * <p>
 * [notice me if you find Bugs or spelling mistake] <br>
 * [or if you have idea for more functions]
 *
 * @author ? <br>
 * optimized by DerFrZocker
 */
public class Config extends YamlConfiguration {

    public Config(@NonNull File file) {

        if (!file.exists()) {
            try {
                Files.createParentDirs(file);
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // try to load the Config file
        try {
            this.load(file);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Config(@NonNull InputStream input) {

        // try to load the Config file
        try {
            this.load(input);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Config(@NonNull String input) {
        try {
            this.loadFromString(input);
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save(@NonNull File file) throws IOException {

        Files.createParentDirs(file);

        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file),
                Charsets.UTF_8)) {

            writer.write(this.saveToString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void load(@NonNull File file) throws IOException, InvalidConfigurationException {

        // load the config file
        this.load(new InputStreamReader(new FileInputStream(file), Charsets.UTF_8));

    }

    public void load(@NonNull InputStream input) throws IOException, InvalidConfigurationException {

        // load the config file
        this.load(new InputStreamReader(input));

    }

    public static Config getConfig(@NonNull JavaPlugin plugin, @NonNull String name) {
        if (!name.endsWith(".yml"))
            name = format("%s.yml", name);

        File file = new File(plugin.getDataFolder().getPath(), name);

        if (!file.exists())
            plugin.saveResource(name, true);

        return new Config(file);
    }

}
