/*
 * MIT License
 *
 * Copyright (c) 2019 - 2020 Marvin (DerFrZocker)
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
 *
 */

package de.derfrzocker.ore.control.utils;

import com.google.common.collect.Sets;
import de.derfrzocker.ore.control.api.*;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class CopyUtil {

    /**
     * Copy all values from the given WorldOreConfig to an other WorldOreConfig
     *
     * @param from the source of the values that get copy
     * @param to   the destinations of the values
     * @throws IllegalArgumentException if one of the arguments is null
     * @throws IllegalArgumentException if the WorldOreConfigs are the same or have the same name
     */
    public static void copy(@NotNull final OreControlService service, @NotNull final WorldOreConfig from, @NotNull final WorldOreConfig to) { //TODO add test cases
        Validate.notNull(service, "OreControlService cannot be null");
        Validate.notNull(from, "Source WorldOreConfig cannot be null");
        Validate.notNull(to, "Target WorldOreConfig cannot be null");
        valid(from, to);
        ResetUtil.reset(to);

        from.getBiomeOreSettings().forEach((biome, biomeOreSettings) -> biomeOreSettings.getOreSettings().forEach((ore, oreSettings) -> {
            oreSettings.getSettings().forEach(((setting, integer) -> service.setValue(to, biome, ore, setting, integer)));
            service.setActivated(to, biome, ore, oreSettings.isActivated());
        }));

        from.getOreSettings().forEach((ore, oreSettings) -> {
            oreSettings.getSettings().forEach(((setting, integer) -> service.setValue(to, ore, setting, integer)));
            service.setActivated(to, ore, oreSettings.isActivated());
        });
    }

    /**
     * Copy the OreSettings from the given Ore and the given WorldOreConfig to a new WorldOreConfig and a new.
     *
     * @param from    the source of the values that get copy
     * @param to      the destinations of the values
     * @param fromOre the source Ore
     * @param toOre   the destinations Ore
     * @throws IllegalArgumentException if one of the arguments is null
     * @throws IllegalArgumentException if the WorldOreConfigs and the Ores are the same
     * @throws IllegalArgumentException if the given Ores dont have the same Settings
     */
    public static void copy(@NotNull final OreControlService service, @NotNull final WorldOreConfig from, @NotNull final WorldOreConfig to, @NotNull final Ore fromOre, @NotNull final Ore toOre) {//TODO add test cases
        Validate.notNull(service, "OreControlService cannot be null");
        Validate.notNull(from, "Source WorldOreConfig cannot be null");
        Validate.notNull(to, "Target WorldOreConfig cannot be null");
        Validate.notNull(fromOre, "Source Ore cannot be null");
        Validate.notNull(toOre, "Target Ore cannot be null");
        valid(fromOre, toOre);
        valid(from, to, fromOre, toOre);
        ResetUtil.reset(to, toOre);

        from.getOreSettings(fromOre).ifPresent(oreSettings -> {
            oreSettings.getSettings().forEach(((setting, integer) -> service.setValue(to, toOre, setting, integer)));
            service.setActivated(to, toOre, oreSettings.isActivated());
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
     * @throws IllegalArgumentException if one of the arguments is null
     * @throws IllegalArgumentException if the given Ores dont have the same Settings
     * @throws IllegalArgumentException if the Biome destinations dont have the given Ore destinations
     */
    public static void copy(@NotNull final OreControlService service, @NotNull final WorldOreConfig from, @NotNull final WorldOreConfig to, @NotNull final Ore fromOre, @NotNull final Ore toOre, @NotNull final Biome toBiome) { //TODO add test cases
        Validate.notNull(service, "OreControlService cannot be null");
        Validate.notNull(from, "Source WorldOreConfig cannot be null");
        Validate.notNull(to, "Target WorldOreConfig cannot be null");
        Validate.notNull(fromOre, "Source Ore cannot be null");
        Validate.notNull(toOre, "Target Ore cannot be null");
        Validate.notNull(toBiome, "Target Biome cannot be null");
        valid(fromOre, toOre);
        valid(toBiome, toOre);
        ResetUtil.reset(to, toOre, toBiome);

        from.getOreSettings(fromOre).ifPresent(oreSettings -> {
            oreSettings.getSettings().forEach(((setting, integer) -> service.setValue(to, toBiome, toOre, setting, integer)));
            service.setActivated(to, toBiome, toOre, oreSettings.isActivated());
        });
    }

    /**
     * Copy the OreSettings from a specific Biome to an other OreSettings.
     *
     * @param from      the source of the values that get copy
     * @param to        the destinations of the values
     * @param fromOre   the source Ore
     * @param fromBiome the source Biome
     * @param toOre     the destinations Ore
     * @throws IllegalArgumentException if one of the arguments is null
     * @throws IllegalArgumentException if the given Ores dont have the same Settings
     * @throws IllegalArgumentException if the Biome source dont have the given Ore source
     */
    public static void copy(@NotNull final OreControlService service, @NotNull final WorldOreConfig from, @NotNull final WorldOreConfig to, @NotNull final Ore fromOre, @NotNull final Biome fromBiome, @NotNull final Ore toOre) { //TODO add test cases
        Validate.notNull(service, "OreControlService cannot be null");
        Validate.notNull(from, "Source WorldOreConfig cannot be null");
        Validate.notNull(to, "Target WorldOreConfig cannot be null");
        Validate.notNull(fromOre, "Source Ore cannot be null");
        Validate.notNull(fromBiome, "Source Biome cannot be null");
        Validate.notNull(toOre, "Target Ore cannot be null");
        valid(fromBiome, fromOre);
        valid(fromOre, toOre);
        ResetUtil.reset(to, toOre);

        from.getBiomeOreSettings(fromBiome).flatMap(biomeOreSettings -> biomeOreSettings.getOreSettings(fromOre)).ifPresent(oreSettings -> {
            oreSettings.getSettings().forEach((setting, integer) -> service.setValue(to, toOre, setting, integer));
            service.setActivated(to, toOre, oreSettings.isActivated());
        });

    }

    /**
     * Copy the OreSettings from a specific Biome to an other OreSettings in a specific Biome.
     *
     * @param from      the source of the values that get copy
     * @param to        the destinations of the values
     * @param fromOre   the source Ore
     * @param fromBiome the source Biome
     * @param toOre     the destinations Ore
     * @param toBiome   the destinations Biome
     * @throws IllegalArgumentException if one of the arguments is null
     * @throws IllegalArgumentException if the given Ores dont have the same Settings
     * @throws IllegalArgumentException if the Biome source dont have the given Ore source
     * @throws IllegalArgumentException if the Biome destination dont have the given Ore destination
     * @throws IllegalArgumentException if the WorldOreConfig, Ore and Biome are the same
     */
    public static void copy(@NotNull final OreControlService service, @NotNull final WorldOreConfig from, @NotNull final WorldOreConfig to, @NotNull final Ore fromOre, @NotNull final Biome fromBiome, @NotNull final Ore toOre, @NotNull final Biome toBiome) { //TODO add test cases
        Validate.notNull(service, "OreControlService cannot be null");
        Validate.notNull(from, "Source WorldOreConfig cannot be null");
        Validate.notNull(to, "Target WorldOreConfig cannot be null");
        Validate.notNull(fromOre, "Source Ore cannot be null");
        Validate.notNull(fromBiome, "Source Biome cannot be null");
        Validate.notNull(toOre, "Target Ore cannot be null");
        Validate.notNull(toBiome, "Target Biome cannot be null");
        valid(fromOre, toOre);
        valid(fromBiome, fromOre);
        valid(toBiome, toOre);
        valid(from, to, toOre, fromOre, toBiome, fromBiome);
        ResetUtil.reset(to, toOre, toBiome);

        from.getBiomeOreSettings(fromBiome).flatMap(biomeOreSettings -> biomeOreSettings.getOreSettings(fromOre)).ifPresent(oreSettings -> {
            oreSettings.getSettings().forEach((setting, integer) -> service.setValue(to, toBiome, toOre, setting, integer));
            service.setActivated(to, toBiome, toOre, oreSettings.isActivated());
        });
    }

    /**
     * Copy all OreSettings from the given Biome to an other Biome. If the Target Biome dont need one OreSettings,
     * for example EMERALD than this OreSetting dont get copy.
     *
     * @param from      the source of the values that get copy
     * @param to        the destinations of the values
     * @param fromBiome the source Biome
     * @param toBiome   the destinations Biome
     * @throws IllegalArgumentException if one of the arguments is null
     * @throws IllegalArgumentException if the given WorldOreConfig and the given Biome are the same
     */
    public static void copy(@NotNull final OreControlService service, @NotNull final WorldOreConfig from, @NotNull final WorldOreConfig to, @NotNull final Biome fromBiome, @NotNull final Biome toBiome) { //TODO add test cases
        Validate.notNull(service, "OreControlService cannot be null");
        Validate.notNull(from, "Source WorldOreConfig cannot be null");
        Validate.notNull(to, "Target WorldOreConfig cannot be null");
        Validate.notNull(fromBiome, "Source Biome cannot be null");
        Validate.notNull(toBiome, "Target Biome cannot be null");
        valid(from, to, fromBiome, toBiome);
        ResetUtil.reset(to, toBiome);

        from.getBiomeOreSettings(fromBiome).ifPresent(biomeOreSettings -> biomeOreSettings.getOreSettings().values().stream().filter(oreSettings -> {
            try {
                valid(toBiome, oreSettings.getOre());
            } catch (final IllegalArgumentException e) {
                return false;
            }

            return true;
        }).forEach(oreSettings -> {
            oreSettings.getSettings().forEach((setting, integer) -> service.setValue(to, toBiome, oreSettings.getOre(), setting, integer));
            service.setActivated(to, toBiome, oreSettings.getOre(), oreSettings.isActivated());
        }));

    }

    /**
     * Copy the value of the given Setting, Ore and WorldOreConfig to an other WorldOreConfig, Ore and Setting.
     *
     * @param from        the source of the value that get copy
     * @param to          the destination of the value
     * @param fromOre     the source Ore
     * @param fromSetting the source Setting
     * @param toOre       the destination Ore
     * @param toSetting   the destination Setting
     * @throws IllegalArgumentException if one of the arguments is null
     * @throws IllegalArgumentException if the given WorldOreConfig, Ore and Setting are the same
     * @throws IllegalArgumentException if the Ore source dont have the given Setting source
     * @throws IllegalArgumentException if the Ore destination dont have the given Setting destination
     */
    public static void copy(@NotNull final OreControlService service, @NotNull final WorldOreConfig from, @NotNull final WorldOreConfig to, @NotNull final Ore fromOre, @NotNull final Setting fromSetting, @NotNull final Ore toOre, @NotNull final Setting toSetting) { //TODO add test cases
        Validate.notNull(service, "OreControlService cannot be null");
        Validate.notNull(from, "Source WorldOreConfig cannot be null");
        Validate.notNull(to, "Target WorldOreConfig cannot be null");
        Validate.notNull(fromOre, "Source Ore cannot be null");
        Validate.notNull(fromSetting, "Source Setting cannot be null");
        Validate.notNull(toOre, "Target Ore cannot be null");
        Validate.notNull(toSetting, "Target Setting cannot be null");
        valid(from, to, fromOre, toOre, fromSetting, toSetting);
        valid(fromOre, fromSetting);
        valid(toOre, toSetting);
        ResetUtil.reset(to, toOre, toSetting);

        from.getOreSettings(fromOre).flatMap(oreSettings -> oreSettings.getValue(fromSetting)).ifPresent(integer -> service.setValue(to, toOre, toSetting, integer));
    }

    /**
     * Copy the value of the given Setting, Ore, Biome and WorldOreConfig to an other WorldOreConfig, Ore and Setting
     *
     * @param from        the source of the value that get copy
     * @param to          the destination of the value
     * @param fromOre     the source Ore
     * @param fromBiome   the source Biome
     * @param fromSetting the source Setting
     * @param toOre       the destination Ore
     * @param toSetting   the destination Setting
     * @throws IllegalArgumentException if one of the arguments is null
     * @throws IllegalArgumentException if the Biome source dont have the given Ore source
     * @throws IllegalArgumentException if the Ore source dont have the given Setting source
     * @throws IllegalArgumentException if the Ore destination dont have the given Setting destination
     */
    public static void copy(@NotNull final OreControlService service, @NotNull final WorldOreConfig from, @NotNull final WorldOreConfig to, @NotNull final Ore fromOre, @NotNull final Biome fromBiome, @NotNull final Setting fromSetting, @NotNull final Ore toOre, @NotNull final Setting toSetting) { //TODO add test cases
        Validate.notNull(service, "OreControlService cannot be null");
        Validate.notNull(from, "Source WorldOreConfig cannot be null");
        Validate.notNull(to, "Target WorldOreConfig cannot be null");
        Validate.notNull(fromOre, "Source Ore cannot be null");
        Validate.notNull(fromBiome, "Source Biome cannot be null");
        Validate.notNull(fromSetting, "Source Setting cannot be null");
        Validate.notNull(toOre, "Target Ore cannot be null");
        Validate.notNull(toSetting, "Target Setting cannot be null");
        valid(fromBiome, fromOre);
        valid(fromOre, fromSetting);
        valid(toOre, toSetting);
        ResetUtil.reset(to, toOre, toSetting);

        from.getBiomeOreSettings(fromBiome).flatMap(biomeOreSettings -> biomeOreSettings.getOreSettings(fromOre)).flatMap(oreSettings -> oreSettings.getValue(fromSetting)).ifPresent(integer -> service.setValue(to, toOre, toSetting, integer));
    }

    /**
     * Copy the value of the given Setting, Ore and WorldOreConfig to an other WorldOreConfig, Ore, Biome and Setting
     *
     * @param from        the source of the value that get copy
     * @param to          the destination of the value
     * @param fromOre     the source Ore
     * @param fromSetting the source Setting
     * @param toOre       the destination Ore
     * @param toBiome     the destination Biome
     * @param toSetting   the destination Setting
     * @throws IllegalArgumentException if one of the arguments is null
     * @throws IllegalArgumentException if the Ore source dont have the given Setting source
     * @throws IllegalArgumentException if the Ore destination dont have the given Setting destination
     * @throws IllegalArgumentException if the Biome destination dont have the given Ore destination
     */
    public static void copy(@NotNull final OreControlService service, @NotNull final WorldOreConfig from, @NotNull final WorldOreConfig to, @NotNull final Ore fromOre, @NotNull final Setting fromSetting, @NotNull final Ore toOre, @NotNull final Biome toBiome, @NotNull final Setting toSetting) {//TODO add test cases
        Validate.notNull(service, "OreControlService cannot be null");
        Validate.notNull(from, "Source WorldOreConfig cannot be null");
        Validate.notNull(to, "Target WorldOreConfig cannot be null");
        Validate.notNull(fromOre, "Source Ore cannot be null");
        Validate.notNull(fromSetting, "Source Setting cannot be null");
        Validate.notNull(toOre, "Target Ore cannot be null");
        Validate.notNull(toBiome, "Target Biome cannot be null");
        Validate.notNull(toSetting, "Target Setting cannot be null");
        valid(fromOre, fromSetting);
        valid(toBiome, toOre);
        valid(toOre, toSetting);
        ResetUtil.reset(to, toOre, toBiome, toSetting);

        from.getOreSettings(fromOre).flatMap(oreSettings -> oreSettings.getValue(fromSetting)).ifPresent(integer -> service.setValue(to, toBiome, toOre, toSetting, integer));
    }

    /**
     * Copy the value of the given Setting, Ore, Biome and WorldOreConfig to an other WorldOreConfig, Ore, Biome Setting
     *
     * @param from        the source of the value that get copy
     * @param to          the destination of the value
     * @param fromOre     the source Ore
     * @param fromBiome   the source Biome
     * @param fromSetting the source Setting
     * @param toOre       the destination Ore
     * @param toBiome     the destination Biome
     * @param toSetting   the destination Setting
     * @throws IllegalArgumentException if one of the arguments is null
     * @throws IllegalArgumentException if the Biome destination dont have the given Ore destination
     * @throws IllegalArgumentException if the Ore source dont have the given Setting source
     * @throws IllegalArgumentException if the Ore destination dont have the given Setting destination
     * @throws IllegalArgumentException if the Biome destination dont have the given Ore destination
     */
    public static void copy(@NotNull final OreControlService service, @NotNull final WorldOreConfig from, @NotNull final WorldOreConfig to, @NotNull final Ore fromOre, @NotNull final Biome fromBiome, @NotNull final Setting fromSetting, @NotNull final Ore toOre, @NotNull final Biome toBiome, @NotNull final Setting toSetting) {//TODO add test cases
        Validate.notNull(service, "OreControlService cannot be null");
        Validate.notNull(from, "Source WorldOreConfig cannot be null");
        Validate.notNull(to, "Target WorldOreConfig cannot be null");
        Validate.notNull(fromOre, "Source Ore cannot be null");
        Validate.notNull(fromBiome, "Source Biome cannot be null");
        Validate.notNull(fromSetting, "Source Setting cannot be null");
        Validate.notNull(toOre, "Target Ore cannot be null");
        Validate.notNull(toBiome, "Target Biome cannot be null");
        Validate.notNull(toSetting, "Target Setting cannot be null");
        valid(from, to, fromOre, toOre, fromBiome, toBiome, fromSetting, toSetting);
        valid(fromBiome, fromOre);
        valid(fromOre, fromSetting);
        valid(toBiome, toOre);
        valid(toOre, toSetting);
        ResetUtil.reset(to, toOre, toBiome, toSetting);

        from.getBiomeOreSettings(fromBiome).flatMap(biomeOreSettings -> biomeOreSettings.getOreSettings(fromOre)).flatMap(oreSettings -> oreSettings.getValue(fromSetting)).ifPresent(integer -> service.setValue(to, toBiome, toOre, toSetting, integer));
    }

    private static void valid(final Ore ore, final Setting setting) {
        if (!Sets.newHashSet(ore.getSettings()).contains(setting)) {
            throw new IllegalArgumentException("The Ore '" + ore + "' don't have the Setting '" + setting + "'!");
        }
    }

    private static void valid(final Biome biome, final Ore ore) {
        if (!Sets.newHashSet(biome.getOres()).contains(ore)) {
            throw new IllegalArgumentException("The Biome '" + biome + "' don't have the Ore '" + ore + "'!");
        }
    }

    private static void valid(final WorldOreConfig worldOreConfig, final WorldOreConfig worldOreConfig1) {
        if (worldOreConfig == worldOreConfig1 || worldOreConfig.getName().equals(worldOreConfig1.getName())) {
            throw new IllegalArgumentException("The given WorldOreConfig (" + worldOreConfig.getName() + ") are the same!");
        }
    }

    private static void valid(final Ore ore, final Ore ore1) {
        if (!Arrays.equals(ore.getSettings(), ore1.getSettings())) {
            throw new IllegalArgumentException("The given Ore's ('" + ore + "' '" + ore1 + "') have not the same Settings!");
        }
    }

    private static void valid(final WorldOreConfig worldOreConfig, final WorldOreConfig worldOreConfig1, final Ore ore, final Ore ore1) {
        if ((worldOreConfig == worldOreConfig1 || worldOreConfig.getName().equals(worldOreConfig1.getName())) && ore == ore1) {
            throw new IllegalArgumentException("The given WorldOreConfig (" + worldOreConfig.getName() + ") and the given Ores (" + ore + ") are the same!");
        }
    }

    private static void valid(final WorldOreConfig worldOreConfig, final WorldOreConfig worldOreConfig1, final Ore ore, final Ore ore1, final Biome biome, final Biome biome1) {
        if ((worldOreConfig == worldOreConfig1 || worldOreConfig.getName().equals(worldOreConfig1.getName())) && ore == ore1 && biome == biome1) {
            throw new IllegalArgumentException("The given WorldOreConfig (" + worldOreConfig.getName() + "), the given Ores (" + ore + ") and the given Biomes (" + biome + ") are the same!");
        }
    }

    private static void valid(final WorldOreConfig worldOreConfig, final WorldOreConfig worldOreConfig1, final Biome biome, final Biome biome1) {
        if ((worldOreConfig == worldOreConfig1 || worldOreConfig.getName().equals(worldOreConfig1.getName())) && biome == biome1) {
            throw new IllegalArgumentException("The given WorldOreConfig (" + worldOreConfig.getName() + ") and the given Biomes (" + biome + ") are the same!");
        }
    }

    private static void valid(final WorldOreConfig worldOreConfig, final WorldOreConfig worldOreConfig1, final Ore ore, final Ore ore1, final Setting setting, final Setting setting1) {
        if ((worldOreConfig == worldOreConfig1 || worldOreConfig.getName().equals(worldOreConfig1.getName())) && ore == ore1 && setting == setting1) {
            throw new IllegalArgumentException("The given WorldOreConfig (" + worldOreConfig.getName() + "), the given Ores (" + ore + ") and the given Settings (" + setting + ") are the same!");
        }
    }

    private static void valid(final WorldOreConfig worldOreConfig, final WorldOreConfig worldOreConfig1, final Ore ore, final Ore ore1, final Biome biome, final Biome biome1, final Setting setting, final Setting setting1) {
        if ((worldOreConfig == worldOreConfig1 || worldOreConfig.getName().equals(worldOreConfig1.getName())) && ore == ore1 && biome == biome1 && setting == setting1) {
            throw new IllegalArgumentException("The given WorldOreConfig (" + worldOreConfig.getName() + "), the given Ores (" + ore + "), the given Biomes (" + biome + ") and the given Settings (" + setting + ") are the same!");
        }
    }

}
