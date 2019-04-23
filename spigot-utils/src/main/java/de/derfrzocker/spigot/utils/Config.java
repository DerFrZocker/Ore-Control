package de.derfrzocker.spigot.utils;

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

    @SuppressWarnings({"UnstableApiUsage", "ResultOfMethodCallIgnored"})
    public Config(@NonNull File file) {

        if (!file.exists()) {
            try {
                Files.createParentDirs(file);
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException("Error while create a new File: " + file, e);
            }
        }

        // try to load the Config file
        try {
            this.load(file);
        } catch (Exception e) {
            throw new RuntimeException("Error while load data from file: " + file, e);
        }

    }

    public Config(@NonNull InputStream input) {

        // try to load the Config file
        try {
            this.load(input);
        } catch (Exception e) {
            throw new RuntimeException("Error while load data from InputStream: " + input, e);
        }

    }

    public Config(@NonNull String input) {
        try {
            this.loadFromString(input);
        } catch (InvalidConfigurationException e) {
            throw new RuntimeException("Error while load data from String: " + input, e);
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void save(@NonNull File file) throws IOException {
        Files.createParentDirs(file);

        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file),
                Charsets.UTF_8)) {

            writer.write(this.saveToString());
        }
    }

    @Override
    public void load(@NonNull File file) throws IOException, InvalidConfigurationException {

        // load the config file
        this.load(new InputStreamReader(new FileInputStream(file), Charsets.UTF_8));
    }

    @SuppressWarnings("WeakerAccess")
    public void load(@NonNull InputStream input) throws IOException, InvalidConfigurationException {

        // load the config file
        this.load(new InputStreamReader(input, Charsets.UTF_8));
    }

    public static Config getConfig(@NonNull JavaPlugin plugin, @NonNull String name) {
        if (!name.endsWith(".yml"))
            name = format("%s.yml", name);

        File file = new File(plugin.getDataFolder().getPath(), name);

        Config defaults = null;

        if (file.exists())
            defaults = new Config(plugin.getResource(name));
        else
            plugin.saveResource(name, true);

        Config config = new Config(file);

        if (defaults != null) {
            config.setDefaults(defaults);
            config.options().copyDefaults(true);
            try {
                config.save(file);
            } catch (IOException e) {
                throw new RuntimeException("Error while save data to file: " + file, e);
            }
        }

        return config;
    }

}
