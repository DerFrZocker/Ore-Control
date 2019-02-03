package de.derfrzocker.ore.control;

import de.derfrzocker.ore.control.api.Ore;
import de.derfrzocker.ore.control.api.OreSettings;
import de.derfrzocker.ore.control.impl.OreSettingsYamlImpl;
import lombok.NonNull;
import org.bukkit.configuration.file.YamlConfiguration;

public class Settings {

    private final String DEFAULT_FORMAT = "defaults.%s";

    @NonNull
    private final YamlConfiguration yaml;

    public Settings(YamlConfiguration yaml) {
        this.yaml = yaml;
    }

    public OreSettings getDefaultSettings(@NonNull Ore ore) {
        return get(String.format(DEFAULT_FORMAT, ore.toString().toLowerCase()));
    }

    private OreSettings get(String key) {
        OreSettingsYamlImpl settings = yaml.getSerializable(key, OreSettingsYamlImpl.class);

        if (settings == null)
            throw new NullPointerException("settings: " + key + " can't be null!");

        return settings.clone();
    }

}
