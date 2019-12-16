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

import java.util.Map;
import java.util.Optional;

/**
 * The WorldOreConfig class represent a ore config for a Minecraft World
 * or for a Template.
 * <p>
 * A Template is a WorldOreConfig that are not directly reference to a Minecraft World,
 * Templates are for pre enter values. A Template becomes a normal WorldOreConfig,
 * if a Minecraft World with the same name exists on the Server.
 * <p>
 * For each World or Template only one WorldOreConfig Object should exists,
 * per Java Runtime.
 * <p>
 * All changes that are made to a WorldOreConfig Object or Objects that are given from the WorldOreConfig
 * are directly apply to the WorldOreConfig and can be access from other Object / Methods.
 * <p>
 * But the changes are not saved to disk, this means after a restart all changes that are made to
 * the WorldOreConfig are gone.
 * <p>
 * To permanently apply the changes to the WorldOreConfig and saved it to disk.
 * See {@link OreControlService#saveWorldOreConfig(WorldOreConfig)}
 */
public interface WorldOreConfig {

    /**
     * Return the name of this config,
     * the name is equal to the world or template
     * name that this WorldOreConfig represent.
     *
     * @return the name of the config
     */
    String getName();

    /**
     * If this config contains the OreSetting of the given Ore,
     * it returns an Optional that contains the OreSettings,
     * otherwise it return an empty Optional.
     *
     * @param ore which must be non-null
     * @return an Optional describing the OreSetting of the given Ore,
     * or an empty Optional if the config not contain the given Ore.
     * @throws NullPointerException if ore is null
     */
    Optional<OreSettings> getOreSettings(Ore ore);

    /**
     * This adds the given OreSetting to this WorldOreConfig.
     * If this Object already have an OreSetting from the Ore,
     * than it replaced the old OreSetting with given OreSetting.
     *
     * @param oreSettings which must be non-null
     * @throws NullPointerException if ore is null
     */
    void setOreSettings(OreSettings oreSettings);

    /**
     * @return the  Map with all  OreSettings that this  WorldOreConfig have.
     */
    Map<Ore, OreSettings> getOreSettings();

    /**
     * If this config contains the  BiomeOreSettings of the given  Biome,
     * it returns an  Optional that contains the  BiomeOreSettings,
     * otherwise it return an empty  Optional.
     *
     * @param biome which must be non- null
     * @return an  Optional describing the BiomeOreSettings of the given  Biome,
     * or an empty  Optional if the config not contain the given  Biome.
     * @throws NullPointerException if biome is  null
     */
    Optional<BiomeOreSettings> getBiomeOreSettings(Biome biome);

    /**
     * This adds the given  BiomeOreSettings to this  WorldOreConfig.
     * If this Object already have an  BiomeOreSettings from the  Biome,
     * than it replaced the old  BiomeOreSettings with given  BiomeOreSettings.
     *
     * @param biomeOreSettings which must be non- null
     * @throws NullPointerException if biomeOreSettings is  null
     */
    void setBiomeOreSettings(BiomeOreSettings biomeOreSettings);

    /**
     * @return the  Map with all  BiomeOreSettings that this  WorldOreConfig have.
     */
    Map<Biome, BiomeOreSettings> getBiomeOreSettings();

    /**
     * @return true if this  WorldOreConfig is a Template
     * otherwise it return  false
     */
    boolean isTemplate();

    /**
     * Set if this  WorldOreConfig is a template or not.
     * <p>
     * Note: If a world exist with the same name, as this  WorldOreConfig the World ignore the Template status
     * and uses the config anyway.
     *
     * @param status true for Template  false for no-Template
     */
    void setTemplate(boolean status);

    /**
     * This copy the current  WorldOreConfig Settings with a new name to a new one.
     * <p>
     * Note: The new  WorldOreConfig is not saved to disk or accessible from
     * {@link OreControlService#getWorldOreConfig(World) or {@link OreControlService#getWorldOreConfig(String)}
     * to save the copy, see {@link OreControlService#saveWorldOreConfig(WorldOreConfig)}.
     *
     * @param name the Name of the new  WorldOreConfig
     * @return a new  WorldOreConfig with the given name.
     * @throws NullPointerException if name is  null
     */
    WorldOreConfig clone(String name);

}