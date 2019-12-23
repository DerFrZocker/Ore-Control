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

import java.util.Optional;
import java.util.Set;

public interface OreControlService {

    NMSService getNMSService();

    Optional<WorldOreConfig> getWorldOreConfig(World world);

    Optional<WorldOreConfig> getWorldOreConfig(String name);

    WorldOreConfig createWorldOreConfig(World world);

    WorldOreConfig createWorldOreConfigTemplate(String name);

    void saveWorldOreConfig(WorldOreConfig config);

    void removeWorldOreConfig(WorldOreConfig config);

    Set<WorldOreConfig> getAllWorldOreConfigs();

    double getValue(Ore ore, Setting setting, WorldOreConfig worldOreConfig, Biome biome);

    boolean isActivated(Ore ore, WorldOreConfig worldOreConfig, Biome biome);

    boolean isOre(String string);

    boolean isBiome(String string);

    boolean isSetting(String string);

}
