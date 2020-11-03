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
import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.Ore;
import de.derfrzocker.ore.control.api.Setting;
import de.derfrzocker.ore.control.api.WorldOreConfig;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;

public class ResetUtil {

    /**
     * This clear all set values from the given WorldOreConfig, it not remove the OreSettings or the BiomeOreSettings Object itself.
     *
     * @param worldOreConfig that should reset
     * @throws IllegalArgumentException if WorldOreConfig is null
     */
    public static void reset(@NotNull final WorldOreConfig worldOreConfig) { //TODO add test cases
        Validate.notNull(worldOreConfig, "WorldOreConfig cannot be null");

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
     * @throws IllegalArgumentException if WorldOreConfig or Ore is null
     */
    public static void reset(@NotNull final WorldOreConfig worldOreConfig, @NotNull final Ore ore) { //TODO add test cases
        Validate.notNull(worldOreConfig, "WorldOreConfig cannot be null");
        Validate.notNull(ore, "Ore cannot be null");

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
     * @throws IllegalArgumentException if WorldOreConfig, Ore or Setting is null
     * @throws IllegalArgumentException if the Ore dont have the given Setting
     */
    public static void reset(@NotNull final WorldOreConfig worldOreConfig, @NotNull final Ore ore, @NotNull final Setting setting) { //TODO add test cases
        Validate.notNull(worldOreConfig, "WorldOreConfig cannot be null");
        Validate.notNull(ore, "Ore cannot be null");
        Validate.notNull(setting, "Setting cannot be null");

        valid(ore, setting);

        worldOreConfig.getOreSettings(ore).ifPresent(oreSettings -> oreSettings.getSettings().remove(setting));
    }

    /**
     * Reset all OreSettings from the given Biome in the given WorldOreConfig.
     *
     * @param worldOreConfig which must be non-null
     * @param biome          which must be non-null
     * @throws IllegalArgumentException if WorldOreConfig or Biome is null
     */
    public static void reset(@NotNull final WorldOreConfig worldOreConfig, @NotNull final Biome biome) { //TODO add test cases
        Validate.notNull(worldOreConfig, "WorldOreConfig cannot be null");
        Validate.notNull(biome, "Biome cannot be null");

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
     * @throws IllegalArgumentException if WorldOreConfig, Ore or Biome is null
     * @throws IllegalArgumentException if the Biome dont have the given Ore
     */
    public static void reset(@NotNull final WorldOreConfig worldOreConfig, @NotNull final Ore ore, @NotNull final Biome biome) {//TODO add test cases
        Validate.notNull(worldOreConfig, "WorldOreConfig cannot be null");
        Validate.notNull(ore, "Ore cannot be null");
        Validate.notNull(biome, "Biome cannot be null");
        valid(biome, ore);

        worldOreConfig.getBiomeOreSettings(biome).flatMap(biomeOreSettings -> biomeOreSettings.getOreSettings(ore)).ifPresent(oreSettings -> {
            oreSettings.getSettings().clear();
            oreSettings.setActivated(true);
        });
    }

    /**
     * Reset the Setting in the given Ore, in the given Biome, in the given WorldOreConfig.
     *
     * @param worldOreConfig which must be non-null
     * @param ore            which must be non-null
     * @param biome          which must be non-null
     * @param setting        which must be non-null
     * @throws IllegalArgumentException if WorldOreConfig, Ore, Setting or Biome is null
     * @throws IllegalArgumentException if the Biome dont have the given Ore
     * @throws IllegalArgumentException if the Ore dont have the given Setting
     */
    public static void reset(@NotNull final WorldOreConfig worldOreConfig, @NotNull final Ore ore, @NotNull final Biome biome, @NotNull final Setting setting) {//TODO add test cases
        Validate.notNull(worldOreConfig, "WorldOreConfig cannot be null");
        Validate.notNull(ore, "Ore cannot be null");
        Validate.notNull(biome, "Biome cannot be null");
        Validate.notNull(setting, "Setting cannot be null");

        valid(biome, ore);
        valid(ore, setting);

        worldOreConfig.getBiomeOreSettings(biome).flatMap(biomeOreSettings -> biomeOreSettings.getOreSettings(ore)).ifPresent(oreSettings -> oreSettings.getSettings().remove(setting));
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

}
