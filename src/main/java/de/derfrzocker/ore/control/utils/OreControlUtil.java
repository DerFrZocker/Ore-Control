package de.derfrzocker.ore.control.utils;

import com.google.common.collect.Sets;
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

    /**
     * Returns the value for the given Setting from the given WorldOreConfig and Ore.
     * If the WorldOreConfig dont have the OreSettings of the given Ore it returns the default value.
     * If the OreSettings dont have the Setting, but the Ore have it, it returns the default value.
     * <p>
     * For the default Ore Settings see: {@link #getDefault(Ore, Setting)}
     * To see which Ore have which Setting see: {@link Ore#getSettings()}
     *
     * @param ore     which must be non-null
     * @param setting which must be non-null
     * @param config  which must be non-null
     * @return the value if present or the default value.
     * @throws NullPointerException     if Ore, Setting or WorldOreConfig is null
     * @throws IllegalArgumentException if the Ore dont have the given Setting
     */
    public static int getAmount(final @NonNull Ore ore, final @NonNull Setting setting, final @NonNull WorldOreConfig config) {
        return config.getOreSettings(ore).orElseGet(() -> OreControl.getInstance().getSettings().getDefaultSettings(ore)).getValue(setting).orElseThrow(() -> new IllegalArgumentException("The ore '" + ore + "' don't have the setting '" + setting + "'!"));
    }

    /**
     * Returns the value for the given Setting from the given WorldOreConfig, Biome and Ore.
     * If the WorldOreConfig dont have the BiomeOreSettings of the given Biomes or the BiomeOreSettings dont have the
     * OreSettings for the Ore, it checks if the WorldOreConfig have the OreSettings
     * of the given Ore. If the WorldOreConfig have the OreSettings it checks if it also have the given Setting. If true it returns the value.
     * otherwise it returns the default value.
     * <p>
     * This means the Priority is: "Biome specific settings" -> "Ore specific settings" -> "default settings"
     * <p>
     * For the default Ore Settings see: {@link #getDefault(Ore, Setting)}
     * To see which Biome have which Ore see: {@link Biome#getOres()}
     * To see which Ore have which Setting see: {@link Ore#getSettings()}
     *
     * @param ore     which must be non-null
     * @param setting which must be non-null
     * @param config  which must be non-null
     * @param biome   which must be non-null
     * @return the value if present or the default value.
     * @throws NullPointerException     if Ore, Setting or WorldOreConfig is null
     * @throws IllegalArgumentException if the Biome dont have the given Ore
     * @throws IllegalArgumentException if the Ore dont have the given Setting
     */
    public static int getAmount(final @NonNull Ore ore, final @NonNull Setting setting, final @NonNull WorldOreConfig config, final @NonNull Biome biome) {
        return getOreSettings(ore, config, biome).getValue(setting).orElseThrow(() -> new IllegalArgumentException("The ore '" + ore + "' don't have the setting '" + setting + "'!"));
    }

    /**
     * This set the value to the given Ore and Setting in the given WorldOreConfig
     * If the WorldOreConfig dont have the OreSetting for the given value it creates a new one.
     * <p>
     * To see which Ore have which Setting see: {@link Ore#getSettings()}
     *
     * @param ore     which must be non-null
     * @param setting which must be non-null
     * @param config  which must be non-null
     * @param value   the new value
     * @throws NullPointerException     if Ore, Setting or WorldOreConfig is null
     * @throws IllegalArgumentException if the Ore dont have the given Setting
     */
    public static void setAmount(final @NonNull Ore ore, final @NonNull Setting setting, final @NonNull WorldOreConfig config, final int value) {
        if (!Sets.newHashSet(ore.getSettings()).contains(setting))
            throw new IllegalArgumentException("The Ore '" + ore + "' don't have the Setting '" + setting + "'!");

        config.getOreSettings(ore).orElseGet(() -> {
            OreSettings oreSettings = OreControl.getInstance().getSettings().getDefaultSettings(ore);
            config.setOreSettings(oreSettings);
            return oreSettings;
        }).setValue(setting, value);
    }

    /**
     * This set the value to the given Ore, Setting and Biome in the given WorldOreConfig
     * If the WorldOreConfig dont have the BiomeOreSettings for the given value it creates a new one.
     * If the BiomeOreSettings dont have the OreSettings for the given Ore it create a new one
     * <p>
     * To see which Biome have which Ore see: {@link Biome#getOres()}
     * To see which Ore have which Setting see: {@link Ore#getSettings()}
     *
     * @param ore     which must be non-null
     * @param setting which must be non-null
     * @param config  which must be non-null
     * @param value   the new value
     * @param biome   which must be non-null
     * @throws NullPointerException     if Ore, Setting or WorldOreConfig is null
     * @throws IllegalArgumentException if the Biome dont have the given Ore
     * @throws IllegalArgumentException if the Ore dont have the given Setting
     */
    public static void setAmount(final @NonNull Ore ore, final @NonNull Setting setting, final @NonNull WorldOreConfig config, final int value, final @NonNull Biome biome) {
        BiomeOreSettings biomeSettings = config.getBiomeOreSettings(biome).orElseGet(() -> {
            BiomeOreSettings biomeOreSettings = new BiomeOreSettingsYamlImpl(biome);
            config.setBiomeOreSettings(biomeOreSettings);
            return biomeOreSettings;
        });

        biomeSettings.getOreSettings(ore).orElseGet(() -> {
            OreSettings oreSettings = OreControl.getInstance().getSettings().getDefaultSettings(ore);
            biomeSettings.setOreSettings(oreSettings);
            return oreSettings;
        }).setValue(setting, value);
    }

    /**
     * Returns the default value for the given Ore and Setting.
     *
     * @param ore     which must be non-null
     * @param setting which must be non-null
     * @return the default value
     * @throws NullPointerException     if Ore, Setting or WorldOreConfig is null
     * @throws IllegalArgumentException if the Ore dont have the given Setting
     */
    public static int getDefault(final @NonNull Ore ore, final @NonNull Setting setting) {
        return OreControl.getInstance().getSettings().getDefaultSettings(ore).getValue(setting).orElseThrow(() -> new IllegalArgumentException("The ore '" + ore + "' don't have the setting '" + setting + "'!"));
    }

    /**
     * Returns if the given value is unsafe or safe.
     * This only checks if the value is to low, this does not check if the value is to high.
     *
     * @param setting which must be non-null
     * @param value   that get checked
     * @return true for unsafe false for safe
     */
    public static boolean isUnSafe(final @NonNull Setting setting, final int value) {
        return setting.getMinimumValue() > value;
    }

    /**
     * Returns the OreSettings from the BiomeOreSettings, if the BiomeOreSettings dont exists or the BiomeOreSettings dont
     * have the OreSettings. It returns the normal OreSettings. If the WorldOreConfig also dont have the OreSettings, it return
     * the default one.
     * <p>
     * To see which Biome have which Ore see: {@link Biome#getOres()}
     * For the default Ore Settings see: {@link #getDefault(Ore, Setting)}
     *
     * @param ore    which must be non-null
     * @param config which must be non-null
     * @param biome  which must be non-null
     * @return an OreSettings
     * @throws NullPointerException     if Ore, Biome or WorldOreConfig is null
     * @throws IllegalArgumentException if the Biome dont have the given Ore
     */
    public static OreSettings getOreSettings(final @NonNull Ore ore, final @NonNull WorldOreConfig config, final @NonNull Biome biome) { //TODO find better name
        if (!Sets.newHashSet(biome.getOres()).contains(ore))
            throw new IllegalArgumentException("The biome '" + biome + "' don't have the ore '" + ore + "'!");

        return config.getBiomeOreSettings(biome).
                map(value -> value.getOreSettings(ore).
                        orElseGet(() -> config.getOreSettings(ore).
                                orElseGet(() -> OreControl.getInstance().getSettings().getDefaultSettings(ore)))).
                orElseGet(() -> config.getOreSettings(ore).
                        orElseGet(() -> OreControl.getInstance().getSettings().getDefaultSettings(ore)));
    }

    /**
     * Return true if the given String is the name of a Ore or not.
     * The String can be lower, upper or mixed case.
     *
     * @param string which must be non-null
     * @return true if Ore and false if not
     * @throws NullPointerException if String is null
     */
    public static boolean isOre(final @NonNull String string) {
        try {
            Ore.valueOf(string.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Return true if the given String is the name of a Biome or not.
     * The String can be lower, upper or mixed case.
     *
     * @param string which must be non-null
     * @return true if Biome and false if not
     * @throws NullPointerException if String is null
     */
    public static boolean isBiome(final @NonNull String string) {
        try {
            Biome.valueOf(string.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Return true if the given String is the name of a Setting or not.
     * The String can be lower, upper or mixed case.
     *
     * @param string which must be non-null
     * @return true if Setting and false if not
     * @throws NullPointerException if String is null
     */
    public static boolean isSetting(final @NonNull String string) {
        try {
            Setting.valueOf(string.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Checks if the given Ore is activated or not.
     * If the WorldOreConfig dont have the OreSettings it returns true.
     *
     * @param ore    which must be non-null
     * @param config which must be non-null
     * @return true if activated false if not
     * @throws NullPointerException if Ore or WorldOreConfig is null
     */
    public static boolean isActivated(final @NonNull Ore ore, final @NonNull WorldOreConfig config) {
        return config.getOreSettings(ore).map(OreSettings::isActivated).orElse(true);
    }

    /**
     * Checks if the given Ore is in the given Biome activated or not.
     * If the WorldOreConfig dont have the BiomeOreSettings or the BiomeOreSettings dont have the OreSettings,
     * than it checks the normal OreSettings, if this Settings also not exists it returns true.
     *
     * @param ore    which must be non-null
     * @param config which must be non-null
     * @return true if activated false if not
     * @throws NullPointerException     if Ore,Biome or WorldOreConfig is null
     * @throws IllegalArgumentException if the Biome dont have the given Ore
     */
    public static boolean isActivated(final @NonNull Ore ore, final @NonNull WorldOreConfig config, final @NonNull Biome biome) {
        return getOreSettings(ore, config, biome).isActivated();
    }

    /**
     * Set the given status to the OreSettings. If the WorldOreConfig dont have the OreSettings it created a new one and
     * set the value to the new OreSettings.
     *
     * @param ore    which must be non-null
     * @param config which must be non-null
     * @param status true for activated false for not activated
     */
    public static void setActivated(final @NonNull Ore ore, final @NonNull WorldOreConfig config, final boolean status) {
        config.getOreSettings(ore).orElseGet(() -> {
            OreSettings oreSettings = OreControl.getInstance().getSettings().getDefaultSettings(ore);
            config.setOreSettings(oreSettings);
            return oreSettings;
        }).setActivated(status);
    }

    /**
     * Set the given status to the OreSettings in the BiomeOreSettings in.
     * If the WorldOreConfig dont have the BiomeOreSettings or the BiomeOreSettings dont have OreSettings, than it create new ones.
     *
     * @param ore    which must be non-null
     * @param config which must be non-null
     * @param status true for activated false for not activated
     * @param biome  which must be non-null
     * @throws IllegalArgumentException if the Biome dont have the given Ore
     */
    public static void setActivated(final @NonNull Ore ore, final @NonNull WorldOreConfig config, final boolean status, final @NonNull Biome biome) {
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

    /**
     * @return a map with translated Ore names
     */
    public static HashMap<Ore, String> getTranslatedOres() {
        return getTranslatedOres(Ore.values());
    }

    /**
     * @param ores which must be non-null
     * @return a map with the translated Ore names of the given Ores
     * @throws NullPointerException if Ore is null
     */
    public static HashMap<Ore, String> getTranslatedOres(final @NonNull Ore... ores) {
        final HashMap<Ore, String> map = new HashMap<>();

        Stream.of(ores).forEach(value -> map.put(value, new MessageKey(OreControlMessages.getInstance(), "ore." + value.toString()).getMessage().replace(" ", "_")));

        return map;
    }

    /**
     * @return a map with all translated Biome names
     */
    public static HashMap<Biome, String> getTranslatedBiomes() {
        final HashMap<Biome, String> map = new HashMap<>();

        Stream.of(Biome.values()).forEach(value -> map.put(value, new MessageKey(OreControlMessages.getInstance(), "biome." + value.toString()).getMessage().replace(" ", "_")));

        return map;
    }

    /**
     * @return a map with all translated Setting names
     */
    public static HashMap<Setting, String> getTranslatedSettings() {
        return getTranslatedSettings(Setting.values());
    }

    /**
     * @param settings which must be non-null
     * @return a map with the translated Setting names of the given Settings
     * @throws NullPointerException if Setting is null
     */
    public static HashMap<Setting, String> getTranslatedSettings(final @NonNull Setting... settings) {
        final HashMap<Setting, String> map = new HashMap<>();

        Stream.of(settings).forEach(value -> map.put(value, new MessageKey(OreControlMessages.getInstance(), "setting." + value.toString()).getMessage().replace(" ", "_")));

        return map;
    }

    /**
     * Return an Optional that contains the Ore of the given String if translated is true it only checks the translated names.
     * If false it only checks the Name of the Ores. If no Ore match the String it returns an empty Optional.
     *
     * @param oreName    which must be non-null
     * @param translated true if the oreName is translated or false if not
     * @return an Optional describing the Ore of the given String,
     * or an empty Optional if the String not match an Ore.
     */
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

    /**
     * Return an Optional that contains the Ore of the given String if translated is true it only checks the translated names.
     * If false it only checks the Name of the Ores. If no Ore match the String it returns an empty Optional.
     * <p>
     * Other as {@link #getOre(String, boolean)} this method only search through the given Oes.
     *
     * @param oreName    which must be non-null
     * @param translated true if the oreName is translated or false if not
     * @param ores       which must be non-null
     * @return an Optional describing the Ore of the given String,
     * or an empty Optional if the String not match an Ore.
     */
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

    /**
     * Return an Optional that contains the Biome of the given String if translated is true it only checks the translated names.
     * If false it only checks the Name of the Biome. If no Biome match the String it returns an empty Optional.
     *
     * @param biomeName  which must be non-null
     * @param translated true if the biomeName is translated or false if not
     * @return an Optional describing the Biome of the given String,
     * or an empty Optional if the String not match an Biome.
     */
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

    /**
     * Return an Optional that contains the Setting of the given String if translated is true it only checks the translated names.
     * If false it only checks the Name of the Setting. If no Setting match the String it returns an empty Optional.
     *
     * @param settingName which must be non-null
     * @param translated  true if the settingName is translated or false if not
     * @return an Optional describing the Setting of the given String,
     * or an empty Optional if the String not match an Setting.
     */
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

    /**
     * Return an Optional that contains the Setting of the given String if translated is true it only checks the translated names.
     * If false it only checks the Name of the Setting. If no Setting match the String it returns an empty Optional.
     * <p>
     * Other as {@link #getSetting(String, boolean)} this method only search through the given Settings.
     *
     * @param settingName which must be non-null
     * @param translated  true if the settingName is translated or false if not
     * @param settings    which must be non-null
     * @return an Optional describing the Setting of the given String,
     * or an empty Optional if the String not match an Setting.
     */
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
