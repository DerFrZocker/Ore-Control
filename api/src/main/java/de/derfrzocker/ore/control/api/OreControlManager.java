/*
 * MIT License
 *
 * Copyright (c) 2019 - 2021 Marvin (DerFrZocker)
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

package de.derfrzocker.ore.control.api;

import de.derfrzocker.ore.control.api.config.ConfigManager;
import org.bukkit.World;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class OreControlManager {

    private final OreControlRegistries registries;
    private final ConfigManager configManager;
    private final Function<World, Set<Biome>> biomeFunction;
    private final List<Runnable> valueChangeListener = new LinkedList<>();

    public OreControlManager(OreControlRegistries registries, ConfigManager configManager, Function<World, Set<Biome>> biomeFunction) {
        this.registries = registries;
        this.configManager = configManager;
        this.biomeFunction = biomeFunction;
    }

    public OreControlRegistries getRegistries() {
        return registries;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public Set<Biome> getBiomes(World world) {
        return biomeFunction.apply(world);
    }

    public void addValueChangeListener(Runnable listener) {
        valueChangeListener.add(listener);
    }

    public void onValueChange() {
        valueChangeListener.forEach(Runnable::run);
    }
}
