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
            config.setOreSettings(OreControl.getInstance().getSettings().getDefaultSettings(ore));
            return config.getOreSettings(ore).orElseThrow(IllegalStateException::new);
        }).setValue(setting, amount);
    }

    public static void setAmount(@NonNull Ore ore, @NonNull Setting setting, @NonNull WorldOreConfig config, int amount, @NonNull Biome biome) {
        config.getBiomeOreSettings(biome).orElseGet(() -> {
            config.setBiomeOreSettings(new BiomeOreSettingsYamlImpl(biome));
            return config.getBiomeOreSettings(biome).orElseThrow(IllegalStateException::new);
        }).getOreSettings(ore).orElseGet(() -> {
            config.getBiomeOreSettings(biome).get().setOreSettings(OreControl.getInstance().getSettings().getDefaultSettings(ore));
            return config.getBiomeOreSettings(biome).get().getOreSettings(ore).orElseThrow(IllegalStateException::new);
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

    public static OreSettings getOreSettings(@NonNull Ore ore, @NonNull WorldOreConfig config, @NonNull Biome biome) {
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

}
