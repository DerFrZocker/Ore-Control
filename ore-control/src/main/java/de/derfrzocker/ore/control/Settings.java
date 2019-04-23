package de.derfrzocker.ore.control;

import de.derfrzocker.ore.control.api.Ore;
import de.derfrzocker.ore.control.api.OreSettings;
import de.derfrzocker.ore.control.impl.OreSettingsYamlImpl;
import lombok.NonNull;
import org.bukkit.configuration.file.YamlConfiguration;

public class Settings {

    @NonNull
    private final YamlConfiguration yaml;

    public Settings(final YamlConfiguration yaml) {
        this.yaml = yaml;
    }

    public OreSettings getDefaultSettings(final @NonNull Ore ore) {
        return get(String.format("defaults.%s", ore.toString().toLowerCase()));
    }

    private OreSettings get(final @NonNull String key) {
        final OreSettingsYamlImpl settings = yaml.getSerializable(key, OreSettingsYamlImpl.class);

        if (settings == null)
            throw new NullPointerException("settings: " + key + " can't be null!");

        return settings.clone();
    }

}
