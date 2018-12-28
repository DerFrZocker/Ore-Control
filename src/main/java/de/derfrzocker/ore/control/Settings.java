package de.derfrzocker.ore.control;

import de.derfrzocker.ore.control.api.EmeraldSettings;
import de.derfrzocker.ore.control.api.LapisSettings;
import de.derfrzocker.ore.control.api.OreSettings;
import de.derfrzocker.ore.control.impl.EmeraldSettingsYamlImpl;
import de.derfrzocker.ore.control.impl.LapisSettingsYamlImpl;
import de.derfrzocker.ore.control.impl.OreSettingsYamlImpl;
import lombok.NonNull;
import org.bukkit.configuration.file.YamlConfiguration;

public class Settings {

    @NonNull
    private final YamlConfiguration yaml;

    public Settings(YamlConfiguration yaml) {
        this.yaml = yaml;
    }


    public OreSettings getDefaultDiamondSettings() {
        return yaml.getSerializable("defaults.diamond", OreSettingsYamlImpl.class).clone();
    }

    public OreSettings getDefaultRedstoneSettings() {
        return yaml.getSerializable("defaults.redstone", OreSettingsYamlImpl.class).clone();
    }

    public OreSettings getDefaultCoalSettings() {
        return yaml.getSerializable("defaults.coal", OreSettingsYamlImpl.class).clone();
    }

    public OreSettings getDefaultGoldSettings() {
        return yaml.getSerializable("defaults.gold.normal", OreSettingsYamlImpl.class).clone();
    }

    public OreSettings getDefaultIronSettings() {
        return yaml.getSerializable("defaults.iron", OreSettingsYamlImpl.class).clone();
    }

    public OreSettings getDefaultBadlandsGoldSettings() {
        return yaml.getSerializable("defaults.gold.badlands", OreSettingsYamlImpl.class).clone();
    }

    public LapisSettings getDefaultLapisSettings() {
        return yaml.getSerializable("defaults.lapis", LapisSettingsYamlImpl.class).clone();
    }

    public EmeraldSettings getDefaultEmeraldSettings() {
        return yaml.getSerializable("defaults.emerald", EmeraldSettingsYamlImpl.class).clone();
    }

}
