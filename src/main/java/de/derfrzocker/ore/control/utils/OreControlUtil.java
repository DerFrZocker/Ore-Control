package de.derfrzocker.ore.control.utils;

import de.derfrzocker.ore.control.OreControl;
import de.derfrzocker.ore.control.OreControlMessages;
import de.derfrzocker.ore.control.api.*;
import de.derfrzocker.ore.control.impl.BiomeOreSettingsYamlImpl;
import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

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

    public static HashMap<Ore, String> getTranslatedOres() {
        return getTranslatedOres(Ore.values());
    }

    public static HashMap<Ore, String> getTranslatedOres(final @NonNull Ore... ores) {
        HashMap<Ore, String> map = new HashMap<>();

        Stream.of(ores).forEach(value -> map.put(value, new MessageKey(OreControlMessages.getInstance(), "ore." + value.toString()).getMessage().replace(" ", "_")));

        return map;
    }


    public static HashMap<Biome, String> getTranslatedBiomes() {
        HashMap<Biome, String> map = new HashMap<>();

        Stream.of(Biome.values()).forEach(value -> map.put(value, new MessageKey(OreControlMessages.getInstance(), "biome." + value.toString()).getMessage().replace(" ", "_")));

        return map;
    }

    public static HashMap<Setting, String> getTranslatedSettings() {
        return getTranslatedSettings(Setting.values());
    }

    public static HashMap<Setting, String> getTranslatedSettings(final @NonNull Setting... settings) {
        HashMap<Setting, String> map = new HashMap<>();

        Stream.of(settings).forEach(value -> map.put(value, new MessageKey(OreControlMessages.getInstance(), "setting." + value.toString()).getMessage().replace(" ", "_")));

        return map;
    }

    public static Optional<Ore> getOre(final @NonNull String oreName, final boolean translated) {
        Optional<Ore> optional;

        if (translated)
            optional = getTranslatedOres().entrySet().stream().filter(entry -> entry.getValue().equalsIgnoreCase(oreName)).findAny().map(Map.Entry::getKey);
        else
            try {
                optional = Optional.of(Ore.valueOf(oreName.toUpperCase()));
            } catch (IllegalArgumentException e) {
                optional = Optional.empty();
            }

        return optional;
    }

    public static Optional<Ore> getOre(final @NonNull String oreName, final boolean translated, final @NonNull Ore... ores) {
        Optional<Ore> optional;

        if (translated)
            optional = getTranslatedOres(ores).entrySet().stream().filter(entry -> entry.getValue().equalsIgnoreCase(oreName)).findAny().map(Map.Entry::getKey);
        else
            try {
                optional = Optional.of(Ore.valueOf(oreName.toUpperCase()));
            } catch (IllegalArgumentException e) {
                optional = Optional.empty();
            }

        return optional;
    }

    public static Optional<Biome> getBiome(final @NonNull String biomeName, final boolean translated) {
        Optional<Biome> optional;

        if (translated)
            optional = getTranslatedBiomes().entrySet().stream().filter(entry -> entry.getValue().equalsIgnoreCase(biomeName)).findAny().map(Map.Entry::getKey);
        else
            try {
                optional = Optional.of(Biome.valueOf(biomeName.toUpperCase()));
            } catch (IllegalArgumentException e) {
                optional = Optional.empty();
            }

        return optional;
    }

    public static Optional<Setting> getSetting(final @NonNull String settingName, final boolean translated) {
        Optional<Setting> optional;

        if (translated)
            optional = getTranslatedSettings().entrySet().stream().filter(entry -> entry.getValue().equalsIgnoreCase(settingName)).findAny().map(Map.Entry::getKey);
        else
            try {
                optional = Optional.of(Setting.valueOf(settingName.toUpperCase()));
            } catch (IllegalArgumentException e) {
                optional = Optional.empty();
            }

        return optional;
    }

    public static Optional<Setting> getSetting(final @NonNull String settingName, final boolean translated, final @NonNull Setting... settings) {
        Optional<Setting> optional;

        if (translated)
            optional = getTranslatedSettings(settings).entrySet().stream().filter(entry -> entry.getValue().equalsIgnoreCase(settingName)).findAny().map(Map.Entry::getKey);
        else
            try {
                optional = Stream.of(settings).filter(value -> value.toString().equalsIgnoreCase(settingName.toUpperCase())).findAny();
            } catch (IllegalArgumentException e) {
                optional = Optional.empty();
            }

        return optional;
    }

}
