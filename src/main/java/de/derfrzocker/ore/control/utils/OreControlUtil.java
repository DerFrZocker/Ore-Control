package de.derfrzocker.ore.control.utils;

import de.derfrzocker.ore.control.OreControl;
import de.derfrzocker.ore.control.api.*;
import de.derfrzocker.ore.control.impl.BiomeOreSettingsYamlImpl;
import lombok.NonNull;

@SuppressWarnings("Duplicates")
public class OreControlUtil {

    public static int getAmount(@NonNull Ore ore, @NonNull Setting setting, @NonNull WorldOreConfig config) {
        return config.getOreSettings(ore).orElseGet(() -> OreControl.getInstance().getSettings().getDefaultSettings(ore)).getValue(setting).orElseThrow(() -> new IllegalArgumentException("The ore '" + ore.toString() + "' don't have the setting '" + setting + "'!"));
    }

    public static void setAmount(@NonNull Ore ore, @NonNull Setting setting, @NonNull WorldOreConfig config, int amount) {
        config.getOreSettings(ore).orElseGet(() -> {
            OreSettings oreSettings = OreControl.getInstance().getSettings().getDefaultSettings(ore);
            config.setOreSettings(oreSettings);
            return oreSettings;
        }).setValue(setting, amount);
    }

    public static void setAmount(@NonNull Ore ore, @NonNull Setting setting, @NonNull WorldOreConfig config, int amount, @NonNull Biome biome) {
        BiomeOreSettings biomeSettings = config.getBiomeOreSettings(biome).orElseGet(() -> {
            BiomeOreSettings biomeOreSettings = new BiomeOreSettingsYamlImpl(biome);
            config.setBiomeOreSettings(biomeOreSettings);
            return biomeOreSettings;
        });

        biomeSettings.getOreSettings(ore).orElseGet(() -> {
            OreSettings oreSettings = OreControl.getInstance().getSettings().getDefaultSettings(ore);
            biomeSettings.setOreSettings(oreSettings);
            return oreSettings;
        }).setValue(setting, amount);
    }

    public static int getAmount(@NonNull Ore ore, @NonNull Setting setting, @NonNull WorldOreConfig config, @NonNull Biome biome) {
        return getOreSettings(ore, config, biome).getValue(setting).orElseThrow(() -> new IllegalArgumentException("The ore '" + ore.toString() + "' don't have the setting '" + setting + "'!"));
    }

    public static int getDefault(@NonNull Ore ore, @NonNull Setting setting) {
        return OreControl.getInstance().getSettings().getDefaultSettings(ore).getValue(setting).orElseThrow(() -> new IllegalArgumentException("The ore '" + ore.toString() + "' don't have the setting '" + setting + "'!"));
    }

    public static boolean isUnSave(@NonNull Setting setting, int amount) {
        return setting.getMinimumValue() > amount;
    }

    public static OreSettings getOreSettings(Ore ore, WorldOreConfig config, Biome biome) { //TODO find better name
        return config.getBiomeOreSettings(biome).
                map(value -> value.getOreSettings(ore).
                        orElseGet(() -> config.getOreSettings(ore).
                                orElseGet(() -> OreControl.getInstance().getSettings().getDefaultSettings(ore)))).
                orElseGet(() -> config.getOreSettings(ore).
                        orElseGet(() -> OreControl.getInstance().getSettings().getDefaultSettings(ore)));
    }

    public static boolean isOre(String string) {
        try {
            Ore.valueOf(string.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static boolean isBiome(String string) {
        try {
            Biome.valueOf(string.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static boolean isSetting(String string) {
        try {
            Setting.valueOf(string.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static boolean isActivated(@NonNull Ore ore, @NonNull WorldOreConfig config) {
        return config.getOreSettings(ore).map(OreSettings::isActivated).orElse(true);
    }

    public static boolean isActivated(@NonNull Ore ore, @NonNull WorldOreConfig config, @NonNull Biome biome) {
        return getOreSettings(ore, config, biome).isActivated();
    }

    public static void setActivated(@NonNull Ore ore, @NonNull WorldOreConfig config, boolean status) {
        config.getOreSettings(ore).orElseGet(() -> {
            OreSettings oreSettings = OreControl.getInstance().getSettings().getDefaultSettings(ore);
            config.setOreSettings(oreSettings);
            return oreSettings;
        }).setActivated(status);
    }

    public static void setActivated(@NonNull Ore ore, @NonNull WorldOreConfig config, boolean status, @NonNull Biome biome) {
        BiomeOreSettings biomeSettings = config.getBiomeOreSettings(biome).orElseGet(() -> {
            BiomeOreSettings biomeOreSettings = new BiomeOreSettingsYamlImpl(biome);
            config.setBiomeOreSettings(biomeOreSettings);
            return biomeOreSettings;
        });

        biomeSettings.getOreSettings(ore).orElseGet(() -> {
            OreSettings oreSettings = OreControl.getInstance().getSettings().getDefaultSettings(ore);
            biomeSettings.setOreSettings(oreSettings);
            return oreSettings;
        }).setActivated(status);
    }

}
