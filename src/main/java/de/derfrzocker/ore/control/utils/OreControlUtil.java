package de.derfrzocker.ore.control.utils;

import com.google.common.collect.Sets;
import de.derfrzocker.ore.control.OreControl;
import de.derfrzocker.ore.control.OreControlMessages;
import de.derfrzocker.ore.control.api.*;
import de.derfrzocker.ore.control.impl.BiomeOreSettingsYamlImpl;
import de.derfrzocker.ore.control.impl.OreSettingsYamlImpl;
import lombok.NonNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

@SuppressWarnings("Duplicates")
public class OreControlUtil {

    private final static Function<Ore, OreSettings> ORE_SETTINGS_FUNCTION = OreSettingsYamlImpl::new;
    private final static Function<Biome, BiomeOreSettings> BIOME_ORE_SETTINGS_FUNCTION = BiomeOreSettingsYamlImpl::new;

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
        valid(ore, setting);

        return config.getOreSettings(ore).map(oreSettings -> oreSettings.getValue(setting).orElseGet(() -> getDefault(ore, setting))).orElseGet(() -> getDefault(ore, setting));
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
        valid(ore, setting);
        valid(biome, ore);

        return config.getBiomeOreSettings(biome).
                map(biomeOreSettings1 -> biomeOreSettings1.getOreSettings(ore).
                        map(oreSettings1 -> oreSettings1.getValue(setting).
                                orElseGet(() -> getAmount(ore, setting, config))).
                        orElseGet(() -> getAmount(ore, setting, config))).
                orElseGet(() -> getAmount(ore, setting, config));
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
        valid(ore, setting);

        config.getOreSettings(ore).orElseGet(() -> {
            final OreSettings oreSettings = ORE_SETTINGS_FUNCTION.apply(ore);
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
        valid(ore, setting);
        valid(biome, ore);

        BiomeOreSettings biomeSettings = config.getBiomeOreSettings(biome).orElseGet(() -> {
            final BiomeOreSettings biomeOreSettings = BIOME_ORE_SETTINGS_FUNCTION.apply(biome);
            config.setBiomeOreSettings(biomeOreSettings);
            return biomeOreSettings;
        });

        biomeSettings.getOreSettings(ore).orElseGet(() -> {
            final OreSettings oreSettings = ORE_SETTINGS_FUNCTION.apply(ore);
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
        valid(ore, setting);

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
        valid(biome, ore);

        return config.getBiomeOreSettings(biome).
                map(value -> value.getOreSettings(ore).
                        orElseGet(() -> config.getOreSettings(ore).
                                orElseGet(() -> OreControl.getInstance().getSettings().getDefaultSettings(ore)))).
                orElseGet(() -> config.getOreSettings(ore).
                        orElseGet(() -> OreControl.getInstance().getSettings().getDefaultSettings(ore))).isActivated();
    }

    /**
     * Set the given status to the OreSettings. If the WorldOreConfig dont have the OreSettings it created a new one and
     * set the value to the new OreSettings.
     *
     * @param ore    which must be non-null
     * @param config which must be non-null
     * @param status true for activated false for not activated
     * @throws NullPointerException if Ore or WorldOreConfig is null
     */
    public static void setActivated(final @NonNull Ore ore, final @NonNull WorldOreConfig config, final boolean status) {
        config.getOreSettings(ore).orElseGet(() -> {
            final OreSettings oreSettings = ORE_SETTINGS_FUNCTION.apply(ore);
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
     * @throws NullPointerException     if Ore,Biome or WorldOreConfig is null
     * @throws IllegalArgumentException if the Biome dont have the given Ore
     */
    public static void setActivated(final @NonNull Ore ore, final @NonNull WorldOreConfig config, final boolean status, final @NonNull Biome biome) {
        valid(biome, ore);

        final BiomeOreSettings biomeSettings = config.getBiomeOreSettings(biome).orElseGet(() -> {
            final BiomeOreSettings biomeOreSettings = BIOME_ORE_SETTINGS_FUNCTION.apply(biome);
            config.setBiomeOreSettings(biomeOreSettings);
            return biomeOreSettings;
        });

        biomeSettings.getOreSettings(ore).orElseGet(() -> {
            final OreSettings oreSettings = ORE_SETTINGS_FUNCTION.apply(ore);
            biomeSettings.setOreSettings(oreSettings);
            return oreSettings;
        }).setActivated(status);
    }

    /**
     * @return a map with translated Ore names
     */
    public static HashMap<Ore, String> getTranslatedOres() { //TODO add test cases
        return getTranslatedOres(Ore.values());
    }

    /**
     * @param ores which must be non-null
     * @return a map with the translated Ore names of the given Ores
     * @throws NullPointerException if Ore is null
     */
    public static HashMap<Ore, String> getTranslatedOres(final @NonNull Ore... ores) { //TODO add test cases
        final HashMap<Ore, String> map = new HashMap<>();

        Stream.of(ores).forEach(value -> map.put(value, new MessageKey(OreControlMessages.getInstance(), "ore." + value.toString()).getMessage().replace(" ", "_")));

        return map;
    }

    /**
     * @return a map with all translated Biome names
     */
    public static HashMap<Biome, String> getTranslatedBiomes() { //TODO add test cases
        final HashMap<Biome, String> map = new HashMap<>();

        Stream.of(Biome.values()).forEach(value -> map.put(value, new MessageKey(OreControlMessages.getInstance(), "biome." + value.toString()).getMessage().replace(" ", "_")));

        return map;
    }

    /**
     * @return a map with all translated Setting names
     */
    public static HashMap<Setting, String> getTranslatedSettings() { //TODO add test cases
        return getTranslatedSettings(Setting.values());
    }

    /**
     * @param settings which must be non-null
     * @return a map with the translated Setting names of the given Settings
     * @throws NullPointerException if Setting is null
     */
    public static HashMap<Setting, String> getTranslatedSettings(final @NonNull Setting... settings) { //TODO add test cases
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
    public static Optional<Ore> getOre(final @NonNull String oreName, final boolean translated) { //TODO add test cases
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
    public static Optional<Ore> getOre(final @NonNull String oreName, final boolean translated, final @NonNull Ore... ores) { //TODO add test cases
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
    public static Optional<Biome> getBiome(final @NonNull String biomeName, final boolean translated) { //TODO add test cases
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
    public static Optional<Setting> getSetting(final @NonNull String settingName, final boolean translated) { //TODO add test cases
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
    public static Optional<Setting> getSetting(final @NonNull String settingName, final boolean translated, final @NonNull Setting... settings) { //TODO add test cases
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

    /**
     * Copy all values from the given WorldOreConfig to an other WorldOreConfig
     *
     * @param from the source of the values that get copy
     * @param to   the destinations of the values
     * @throws NullPointerException     if one of the WorldOreConfig is null
     * @throws IllegalArgumentException if the WorldOreConfigs are the same or have the same name
     */
    public static void copy(final @NonNull WorldOreConfig from, final @NonNull WorldOreConfig to) { //TODO add test cases
        valid(from, to);
        reset(to);

        from.getBiomeOreSettings().forEach((biome, biomeOreSettings) -> biomeOreSettings.getOreSettings().forEach((ore, oreSettings) -> {
            oreSettings.getSettings().forEach(((setting, integer) -> setAmount(ore, setting, to, integer, biome)));
            setActivated(ore, to, oreSettings.isActivated(), biome);
        }));

        from.getOreSettings().forEach((ore, oreSettings) -> {
            oreSettings.getSettings().forEach(((setting, integer) -> setAmount(ore, setting, to, integer)));
            setActivated(ore, to, oreSettings.isActivated());
        });
    }

    /**
     * Copy the OreSettings from the given Ore and the given WorldOreConfig to a new WorldOreConfig and a new.
     *
     * @param from    the source of the values that get copy
     * @param to      the destinations of the values
     * @param fromOre the source Ore
     * @param toOre   the destinations Ore
     * @throws NullPointerException     if one of the arguments is null
     * @throws IllegalArgumentException if the WorldOreConfigs and the Ores are the same
     * @throws IllegalArgumentException if the given Ores dont have the same Settings
     */
    public static void copy(final @NonNull WorldOreConfig from, final @NonNull WorldOreConfig to, final @NonNull Ore fromOre, final @NonNull Ore toOre) {//TODO add test cases
        valid(fromOre, toOre);
        valid(from, to, fromOre, toOre);
        reset(to, toOre);

        from.getOreSettings(fromOre).ifPresent(oreSettings -> {
            oreSettings.getSettings().forEach(((setting, integer) -> setAmount(toOre, setting, to, integer)));
            setActivated(toOre, to, oreSettings.isActivated());
        });
    }

    /**
     * Copy the OreSettings from the given Ore and given source WorldOreConfig,
     * to the destinations OreSettings in the specified Biome.
     *
     * @param from    the source of the values that get copy
     * @param to      the destinations of the values
     * @param fromOre the source Ore
     * @param toOre   the destinations Ore
     * @param toBiome the destinations Biome
     * @throws NullPointerException     if one of the arguments is null
     * @throws IllegalArgumentException if the given Ores dont have the same Settings
     * @throws IllegalArgumentException if the Biome destinations dont have the given Ore destinations
     */
    public static void copy(final @NonNull WorldOreConfig from, final @NonNull WorldOreConfig to, final @NonNull Ore fromOre, final @NonNull Ore toOre, final @NonNull Biome toBiome) { //TODO add test cases
        valid(from, to);
        valid(toBiome, toOre);
        reset(to, toOre, toBiome);

        from.getOreSettings(fromOre).ifPresent(oreSettings -> {
            oreSettings.getSettings().forEach(((setting, integer) -> setAmount(toOre, setting, to, integer, toBiome)));
            setActivated(toOre, to, oreSettings.isActivated(), toBiome);
        });
    }

    public static void copy(final @NonNull WorldOreConfig from, final @NonNull WorldOreConfig to, final @NonNull Ore fromOre, final @NonNull Biome fromBiome, final @NonNull Ore toOre){
        valid(from, to);
        valid(toBiome, toOre);
        reset(to, toOre, toBiome);
    }

    public static void copy(final @NonNull WorldOreConfig from, final @NonNull WorldOreConfig to, final @NonNull Ore ore, final @NonNull Setting setting) {//TODO add test cases
        valid(from, to);
        valid(ore, setting);
        reset(to, ore, setting);

        from.getOreSettings(ore).ifPresent(oreSettings -> oreSettings.getValue(setting).ifPresent(integer -> setAmount(ore, setting, to, integer)));
    }

    /**
     * This clear all set values from the given WorldOreConfig, it not remove the OreSettings or the BiomeOreSettings Object itself.
     *
     * @param worldOreConfig that should reset
     * @throws NullPointerException if WorldOreConfig is null
     */
    public static void reset(final @NonNull WorldOreConfig worldOreConfig) { //TODO add test cases
        worldOreConfig.getBiomeOreSettings().forEach(((biome, biomeOreSettings) -> biomeOreSettings.getOreSettings().forEach(((ore, oreSettings) -> {
            oreSettings.getSettings().clear();
            oreSettings.setActivated(true);
        }))));

        worldOreConfig.getOreSettings().forEach(((ore, oreSettings) -> {
            oreSettings.getSettings().clear();
            oreSettings.setActivated(true);
        }));
    }

    /**
     * Reset the values from the given Ore in the given WorldOreConfig.
     *
     * @param worldOreConfig which must be non-null
     * @param ore            which must be non-null
     * @throws NullPointerException if WorldOreConfig or Ore is null
     */
    public static void reset(final @NonNull WorldOreConfig worldOreConfig, final @NonNull Ore ore) { //TODO add test cases
        worldOreConfig.getOreSettings(ore).ifPresent(oreSettings -> {
            oreSettings.getSettings().clear();
            oreSettings.setActivated(true);
        });
    }

    /**
     * Reset the given Setting from the given Ore in the given WorldOreConfig.
     *
     * @param worldOreConfig which must be non-null
     * @param ore            which must be non-null
     * @param setting        which must be non-null
     * @throws NullPointerException     if WorldOreConfig, Ore or Setting is null
     * @throws IllegalArgumentException if the Ore dont have the given Setting
     */
    public static void reset(final @NonNull WorldOreConfig worldOreConfig, final @NonNull Ore ore, final @NonNull Setting setting) { //TODO add test cases
        valid(ore, setting);
        worldOreConfig.getOreSettings(ore).ifPresent(oreSettings -> oreSettings.getSettings().remove(setting));
    }

    /**
     * Reset all OreSettings from the given Biome in the given WorldOreConfig.
     *
     * @param worldOreConfig which must be non-null
     * @param biome          which must be non-null
     * @throws NullPointerException if WorldOreConfig or Biome is null
     */
    public static void reset(final @NonNull WorldOreConfig worldOreConfig, final @NonNull Biome biome) { //TODO add test cases
        worldOreConfig.getBiomeOreSettings(biome).ifPresent(biomeOreSettings -> biomeOreSettings.getOreSettings().forEach((ore, oreSettings) -> {
            oreSettings.getSettings().clear();
            oreSettings.setActivated(true);
        }));
    }

    /**
     * Reset the OreSetting from the given Ore, in the given Biome, in the given WorldOreConfig.
     *
     * @param worldOreConfig which must be non-null
     * @param ore            which must be non-null
     * @param biome          which must be non-null
     * @throws NullPointerException     if WorldOreConfig, Ore or Biome is null
     * @throws IllegalArgumentException if the Biome dont have the given Ore
     */
    public static void reset(final @NonNull WorldOreConfig worldOreConfig, final @NonNull Ore ore, final @NonNull Biome biome) {//TODO add test cases
        valid(biome, ore);
        worldOreConfig.getBiomeOreSettings(biome).ifPresent(biomeOreSettings -> biomeOreSettings.getOreSettings(ore).ifPresent(oreSettings -> {
            oreSettings.getSettings().clear();
            oreSettings.setActivated(true);
        }));
    }

    /**
     * Reset the Setting in the given Ore, in the given Biome, in the given WorldOreConfig.
     *
     * @param worldOreConfig which must be non-null
     * @param ore            which must be non-null
     * @param biome          which must be non-null
     * @param setting        which must be non-null
     * @throws NullPointerException     if WorldOreConfig, Ore, Setting or Biome is null
     * @throws IllegalArgumentException if the Biome dont have the given Ore
     * @throws IllegalArgumentException if the Ore dont have the given Setting
     */
    public static void reset(final @NonNull WorldOreConfig worldOreConfig, final @NonNull Ore ore, final @NonNull Biome biome, final @NonNull Setting setting) {//TODO add test cases
        valid(biome, ore);
        valid(ore, setting);
        worldOreConfig.getBiomeOreSettings(biome).ifPresent(biomeOreSettings -> biomeOreSettings.getOreSettings(ore).ifPresent(oreSettings -> oreSettings.getSettings().remove(setting)));
    }

    private static void valid(final Ore ore, final Setting setting) {
        if (!Sets.newHashSet(ore.getSettings()).contains(setting))
            throw new IllegalArgumentException("The Ore '" + ore + "' don't have the Setting '" + setting + "'!");
    }

    private static void valid(final Biome biome, final Ore ore) {
        if (!Sets.newHashSet(biome.getOres()).contains(ore))
            throw new IllegalArgumentException("The Biome '" + biome + "' don't have the Ore '" + ore + "'!");
    }

    private static void valid(final WorldOreConfig worldOreConfig, final WorldOreConfig worldOreConfig1) {
        if (worldOreConfig == worldOreConfig1 || worldOreConfig.getName().equals(worldOreConfig1.getName()))
            throw new IllegalArgumentException("The given WorldOreConfig (" + worldOreConfig.getName() + ") are the same!");
    }

    private static void valid(final Ore ore, final Ore ore1) {
        if (!Arrays.equals(ore.getSettings(), ore1.getSettings()))
            throw new IllegalArgumentException("The given Ore's ('" + ore + "' '" + ore1 + "') have not the same Settings!");
    }

    private static void valid(final WorldOreConfig worldOreConfig, final WorldOreConfig worldOreConfig1, final Ore ore, final Ore ore1) {
        if ((worldOreConfig == worldOreConfig1 || worldOreConfig.getName().equals(worldOreConfig1.getName())) && ore == ore1)
            throw new IllegalArgumentException("The given WorldOreConfig (" + worldOreConfig.getName() + ") and the given Ores (" + ore + ") are the same!");
    }

}
