/*
 * MIT License
 *
 * Copyright (c) 2019 Marvin (DerFrZocker)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.derfrzocker.ore.control.utils;

import de.derfrzocker.ore.control.OreControl;
import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.Ore;
import de.derfrzocker.ore.control.api.Setting;
import de.derfrzocker.spigot.utils.Version;
import de.derfrzocker.spigot.utils.message.MessageKey;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@SuppressWarnings("Duplicates")
public class OreControlUtil {

    /**
     * Returns if the given value is unsafe or safe.
     * This only checks if the value is to low, this does not check if the value is to high.
     *
     * @param setting which must be non-null
     * @param value   that get checked
     * @return true for unsafe false for safe
     */
    public static boolean isUnSafe(@NotNull final Setting setting, final double value) {
        Validate.notNull(setting, "Setting cannot be null");

        return setting.getMinimumValue() > value;
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
    public static HashMap<Ore, String> getTranslatedOres(@NotNull final Ore... ores) { //TODO add test cases
        Validate.notNull(ores, "Ores cannot be null");

        final HashMap<Ore, String> map = new HashMap<>();

        Stream.of(ores).forEach(value -> map.put(value, new MessageKey(OreControl.getInstance(), "ore." + value.toString()).getMessage().replace(" ", "_")));

        return map;
    }

    /**
     * @return a map with all translated Biome names
     */
    public static HashMap<Biome, String> getTranslatedBiomes() { //TODO add test cases
        final HashMap<Biome, String> map = new HashMap<>();

        Stream.of(Biome.values()).forEach(value -> map.put(value, new MessageKey(OreControl.getInstance(), "biome." + value.toString()).getMessage().replace(" ", "_")));

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
    public static HashMap<Setting, String> getTranslatedSettings(@NotNull final Setting... settings) { //TODO add test cases
        Validate.notNull(settings, "Settings cannot be null");

        final HashMap<Setting, String> map = new HashMap<>();

        Stream.of(settings).forEach(value -> map.put(value, new MessageKey(OreControl.getInstance(), "setting." + value.toString()).getMessage().replace(" ", "_")));

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
    public static Optional<Ore> getOre(@NotNull final String oreName, final boolean translated) { //TODO add test cases
        Validate.notNull(oreName, "Ore name cannot be null");

        Optional<Ore> optional;

        if (translated) {
            optional = getTranslatedOres().entrySet().stream().filter(entry -> entry.getValue().equalsIgnoreCase(oreName)).findAny().map(Map.Entry::getKey);
        } else {
            try {
                optional = Optional.of(Ore.valueOf(oreName.toUpperCase()));
            } catch (final IllegalArgumentException e) {
                optional = Optional.empty();
            }
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
    public static Optional<Ore> getOre(@NotNull final String oreName, final boolean translated, @NotNull final Ore... ores) { //TODO add test cases
        Validate.notNull(oreName, "Ore name cannot be null");
        Validate.notNull(ores, "Ores cannot be null");

        Optional<Ore> optional;

        if (translated) {
            optional = getTranslatedOres(ores).entrySet().stream().filter(entry -> entry.getValue().equalsIgnoreCase(oreName)).findAny().map(Map.Entry::getKey);
        } else {
            try {
                optional = Optional.of(Ore.valueOf(oreName.toUpperCase()));
            } catch (final IllegalArgumentException e) {
                optional = Optional.empty();
            }
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
    public static Optional<Biome> getBiome(@NotNull final String biomeName, final boolean translated) { //TODO add test cases
        Validate.notNull(biomeName, "Biome Name cannot be null");

        Optional<Biome> optional;

        if (translated) {
            optional = getTranslatedBiomes().entrySet().stream().filter(entry -> Version.getCurrent().isOlderOrSameVersion(entry.getKey().getSince())).filter(entry -> entry.getValue().equalsIgnoreCase(biomeName)).findAny().map(Map.Entry::getKey);
        } else {
            try {
                optional = Optional.of(Biome.valueOf(biomeName.toUpperCase()));
                if (Version.getCurrent().isNewerVersion(optional.get().getSince())) {
                    optional = Optional.empty();
                }
            } catch (final IllegalArgumentException e) {
                optional = Optional.empty();
            }
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
    public static Optional<Setting> getSetting(@NotNull final String settingName, final boolean translated) { //TODO add test cases
        Validate.notNull(settingName, "Setting Name cannot be null");

        Optional<Setting> optional;

        if (translated) {
            optional = getTranslatedSettings().entrySet().stream().filter(entry -> entry.getValue().equalsIgnoreCase(settingName)).findAny().map(Map.Entry::getKey);
        } else {
            try {
                optional = Optional.of(Setting.valueOf(settingName.toUpperCase()));
            } catch (final IllegalArgumentException e) {
                optional = Optional.empty();
            }
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
    public static Optional<Setting> getSetting(@NotNull final String settingName, final boolean translated, @NotNull final Setting... settings) { //TODO add test cases
        Validate.notNull(settingName, "Setting name cannot be null");
        Validate.notNull(settings, "Settings cannot be null");

        Optional<Setting> optional;

        if (translated) {
            optional = getTranslatedSettings(settings).entrySet().stream().filter(entry -> entry.getValue().equalsIgnoreCase(settingName)).findAny().map(Map.Entry::getKey);
        } else {
            try {
                optional = Stream.of(settings).filter(value -> value.toString().equalsIgnoreCase(settingName.toUpperCase())).findAny();
            } catch (final IllegalArgumentException e) {
                optional = Optional.empty();
            }
        }

        return optional;
    }

}
