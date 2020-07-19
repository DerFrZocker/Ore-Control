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

package de.derfrzocker.ore.control.api;

import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;

public interface OreControlService {

    /**
     * @return the NMSService
     */
    @NotNull
    NMSService getNMSService();

    /**
     * If a WorldOreConfig for the given world exist
     * it returns an Optional which contains the WorldOreConfig,
     * Otherwise it return an empty Optional
     *
     * @param world of the WorldOreConfig
     * @return an Optional that hold the value for the given world,
     * or an empty Optional if the WorldOreConfig doesn't exist
     * @throws IllegalArgumentException if world is null
     */
    @NotNull
    Optional<WorldOreConfig> getWorldOreConfig(@NotNull World world);

    /**
     * If a WorldOreConfig with the given name exist
     * it returns an Optional which contains the WorldOreConfig,
     * Otherwise it return an empty Optional
     *
     * @param name the name of the WorldOreConfig
     * @return an Optional that hold the value of the given name,
     * or an empty Optional if the WorldOreConfig doesn't exist
     * @throws IllegalArgumentException if name is null
     */
    @NotNull
    Optional<WorldOreConfig> getWorldOreConfig(@NotNull String name);

    /**
     * The values in this WorldOreConfig are used when no other value is specific in a
     * World specific WorldOreConfig
     * <p>
     * The name of the WorldOreConfig is "Default"
     *
     * @return the default WorldOreConfig
     */
    @NotNull
    WorldOreConfig getDefaultWorldOreConfig();

    /**
     * Creates a new WorldOreConfig for the given world.
     * The create WorldOreConfig gets automatically saved to disk,
     * this is done in the called Thread, it is not recommend to call this method from Minecraft's main Thread.
     * <p>
     * If a WorldOreConfig for the world already exists, it will override the old one with the new one.
     *
     * @param world to create for
     * @return a new WorldOreConfig
     * @throws IllegalArgumentException if world is null
     */
    @NotNull
    WorldOreConfig createWorldOreConfig(@NotNull World world);

    /**
     * Creates a new WorldOreConfig with the given name.
     * The create WorldOreConfig gets automatically saved to disk,
     * this is done in the called Thread, it is not recommend to call this method from Minecraft's main Thread.
     * <p>
     * If a WorldOreConfig with the same name already exists, it will override the old one with the new one.
     *
     * @param name of the template
     * @return a new WorldOreConfig
     * @throws IllegalArgumentException if name is null, empty or only contains spaces
     */
    @NotNull
    WorldOreConfig createWorldOreConfigTemplate(@NotNull String name);

    /**
     * Saves the given WorldOreConfig to disk,
     * this is done in the called Thread, it is not recommend to call this method from Minecraft's main Thread
     *
     * @param worldOreConfig to save
     * @throws IllegalArgumentException if worldOreConfig is null
     */
    void saveWorldOreConfig(@NotNull WorldOreConfig worldOreConfig);

    /**
     * Removes the given WorldOreConfig
     *
     * @param worldOreConfig to remove
     * @throws IllegalArgumentException if worldOreConfig is null
     */
    void removeWorldOreConfig(@NotNull WorldOreConfig worldOreConfig);

    /**
     * @return a new set which contains all known WorldOreConfigs
     */
    @NotNull
    Set<WorldOreConfig> getAllWorldOreConfigs();


    /**
     * Returns the default value for the given Ore and Setting.
     * <p>
     * The default value is not the value from the default WorldOreConfig, but the value which Minecraft use
     *
     * @param ore     which must be non-null
     * @param setting which must be non-null
     * @return the default value
     * @throws IllegalArgumentException if ore or setting  is null
     * @throws IllegalArgumentException if the Ore dont have the given Setting
     */
    double getDefaultValue(@NotNull Ore ore, @NotNull Setting setting);

    /**
     * Returns the default value for the given Biome, Ore and Setting.
     * <p>
     * If no default value for the given values is present, it will return
     * the default value for only the Ore and Setting.
     * <p>
     * The default value is not the value of the Default WorldOreConfig, but the value which Minecraft use
     *
     * @param biome   which must be non-null
     * @param ore     which must be non-null
     * @param setting which must be non-null
     * @return the default value
     * @throws IllegalArgumentException if ore or setting  is null
     * @throws IllegalArgumentException if the Biome dont have the given Ore
     * @throws IllegalArgumentException if the Ore dont have the given Setting
     */
    double getDefaultValue(@NotNull Biome biome, @NotNull Ore ore, @NotNull Setting setting);

    /**
     * Returns the value for the given Setting from the given WorldOreConfig, Biome and Ore.
     * If the WorldOreConfig dont have the BiomeOreSettings of the given Biomes or the BiomeOreSettings dont have the
     * OreSettings for the Ore, it checks if the WorldOreConfig have the OreSettings
     * of the given Ore. If the WorldOreConfig have the OreSettings it checks if it also have the given Setting. If true it returns the value.
     * otherwise it while check in the default WorldOreConfig, if the default WorldOreConfig dont have a value too, than it will return the default value.
     * <p>
     * This means the Priority is: "Biome specific settings" -> "Ore specific settings"
     * -> "Default WorldOreConfig Biome specific settings" -> "Default WorldOreConfig Ore specific settings" ->"default settings"
     * <p>
     * To see which Biome have which Ore see: {@link Biome#getOres()}
     * To see which Ore have which Setting see: {@link Ore#getSettings()}
     *
     * @param worldOreConfig which must be non-null
     * @param biome          which must be non-null
     * @param ore            which must be non-null
     * @param setting        which must be non-null
     * @return the value if present or the default value.
     * @throws IllegalArgumentException if worldOreConfig, biome, ore or setting is null
     * @throws IllegalArgumentException if the Biome dont have the given Ore
     * @throws IllegalArgumentException if the Ore dont have the given Setting
     */
    double getValue(@NotNull WorldOreConfig worldOreConfig, @NotNull Biome biome, @NotNull Ore ore, @NotNull Setting setting);

    /**
     * Returns the value for the given Setting from the given WorldOreConfig and Ore.
     * If the WorldOreConfig dont have the OreSettings of the given Ore it returns the default value.
     * If the OreSettings dont have the Setting, but the Ore have it, than it while check the default WorldOreConfig, if it dont have a value too, than it will return the default value
     * <p>
     * To see which Ore have which Setting see: {@link Ore#getSettings()}
     *
     * @param worldOreConfig which must be non-null
     * @param ore            which must be non-null
     * @param setting        which must be non-null
     * @return the value if present or the default value.
     * @throws IllegalArgumentException if worldOreConfig, ore or setting is null
     * @throws IllegalArgumentException if the Ore dont have the given Setting
     */
    double getValue(@NotNull WorldOreConfig worldOreConfig, @NotNull Ore ore, @NotNull Setting setting);

    /**
     * This set the value to the given Ore, Setting and Biome in the given WorldOreConfig
     * If the WorldOreConfig dont have the BiomeOreSettings for the given value it creates a new one.
     * If the BiomeOreSettings dont have the OreSettings for the given Ore it create a new one
     * <p>
     * To see which Biome have which Ore see: {@link Biome#getOres()}
     * To see which Ore have which Setting see: {@link Ore#getSettings()}
     *
     * @param worldOreConfig which must be non-null
     * @param biome          which must be non-null
     * @param ore            which must be non-null
     * @param setting        which must be non-null
     * @param value          to set
     * @throws IllegalArgumentException if worldOreConfig, biome, ore or setting is null
     * @throws IllegalArgumentException if the Biome dont have the given Ore
     * @throws IllegalArgumentException if the Ore dont have the given Setting
     */
    void setValue(@NotNull WorldOreConfig worldOreConfig, @NotNull Biome biome, @NotNull Ore ore, @NotNull Setting setting, double value);

    /**
     * This set the value to the given Ore and Setting in the given WorldOreConfig
     * If the WorldOreConfig dont have the OreSetting for the given value it creates a new one.
     * <p>
     * To see which Ore have which Setting see: {@link Ore#getSettings()}
     *
     * @param worldOreConfig which must be non-null
     * @param ore            which must be non-null
     * @param setting        which must be non-null
     * @param value          to set
     * @throws IllegalArgumentException if worldOreConfig, ore or setting is null
     * @throws IllegalArgumentException if the Ore dont have the given Setting
     */
    void setValue(@NotNull WorldOreConfig worldOreConfig, @NotNull Ore ore, @NotNull Setting setting, double value);

    /**
     * Checks if the given Ore is in the given Biome activated or not.
     * If the WorldOreConfig dont have the BiomeOreSettings or the BiomeOreSettings dont have the OreSettings,
     * than it checks the normal OreSettings, if this Settings also not exists, it while check the default WorldOreConfig for the OreSetting,
     * if the default WorldOreConfig have no OreSetting too, than it returns true.
     *
     * @param worldOreConfig which must be non-null
     * @param biome          which must be non-null
     * @param ore            which must be non-null
     * @return true if activated false if not
     * @throws IllegalArgumentException if worldOreConfig, biome or ore is null
     * @throws IllegalArgumentException if the Biome dont have the given Ore
     */
    boolean isActivated(@NotNull WorldOreConfig worldOreConfig, @NotNull Biome biome, @NotNull Ore ore);

    /**
     * Checks if the given Ore is activated or not.
     * If the WorldOreConfig dont have the OreSettings and the default WorldOreConfig to, than it returns true.
     *
     * @param worldOreConfig which must be non-null
     * @param ore            which must be non-null
     * @return true if activated false if not
     * @throws IllegalArgumentException if worldOreConfig or ore is null
     */
    boolean isActivated(@NotNull WorldOreConfig worldOreConfig, @NotNull Ore ore);

    /**
     * Set the given status to the OreSettings in the BiomeOreSettings in.
     * If the WorldOreConfig dont have the BiomeOreSettings or the BiomeOreSettings dont have the OreSettings, than it create new ones.
     *
     * @param worldOreConfig which must be non-null
     * @param biome          which must be non-null
     * @param ore            which must be non-null
     * @param status         true for activated false for not activated
     * @throws IllegalArgumentException if worldOreConfig, biome or ore is null
     * @throws IllegalArgumentException if the Biome dont have the given Ore
     */
    void setActivated(@NotNull WorldOreConfig worldOreConfig, @NotNull Biome biome, @NotNull Ore ore, boolean status);

    /**
     * Set the given status to the OreSettings. If the WorldOreConfig dont have the OreSettings it created a new one and
     * set the value to the new OreSettings.
     *
     * @param worldOreConfig which must be non-null
     * @param ore            which must be non-null
     * @param status         true for activated false for not activated
     * @throws IllegalArgumentException if Ore or WorldOreConfig is null
     */
    void setActivated(@NotNull WorldOreConfig worldOreConfig, @NotNull Ore ore, boolean status);

}
