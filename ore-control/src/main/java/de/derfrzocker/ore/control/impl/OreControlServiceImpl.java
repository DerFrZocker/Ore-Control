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

package de.derfrzocker.ore.control.impl;

import de.derfrzocker.ore.control.api.*;
import de.derfrzocker.ore.control.api.dao.WorldOreConfigDao;
import de.derfrzocker.ore.control.utils.OreControlUtil;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.bukkit.World;

import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@ToString
public class OreControlServiceImpl implements OreControlService {

    @Getter
    @NonNull
    private final NMSService NMSService;

    @NonNull
    private final WorldOreConfigDao dao;

    @Override
    public Optional<WorldOreConfig> getWorldOreConfig(final @NonNull World world) {
        return dao.get(world.getName());
    }

    @Override
    public Optional<WorldOreConfig> getWorldOreConfig(final @NonNull String name) {
        return dao.get(name);
    }

    @Override
    public WorldOreConfig createWorldOreConfig(final @NonNull World world) {
        final WorldOreConfig worldOreConfig = new WorldOreConfigYamlImpl(world.getName(), false);

        saveWorldOreConfig(worldOreConfig);

        return worldOreConfig;
    }

    @Override
    public WorldOreConfig createWorldOreConfigTemplate(final @NonNull String name) {
        final WorldOreConfig worldOreConfig = new WorldOreConfigYamlImpl(name, true);

        saveWorldOreConfig(worldOreConfig);

        return worldOreConfig;
    }

    @Override
    public void saveWorldOreConfig(final @NonNull WorldOreConfig config) {
        dao.save(config);
    }

    @Override
    public void removeWorldOreConfig(final @NonNull WorldOreConfig config) {
        dao.remove(config);
    }

    @Override
    public Set<WorldOreConfig> getAllWorldOreConfigs() {
        return dao.getAll();
    }

    @Override
    public int getValue(final @NonNull Ore ore, final @NonNull Setting setting, final @NonNull WorldOreConfig worldOreConfig, final @NonNull Biome biome) {
        return OreControlUtil.getAmount(ore, setting, worldOreConfig, biome);
    }

    @Override
    public boolean isActivated(final @NonNull Ore ore, final @NonNull WorldOreConfig worldOreConfig, final @NonNull Biome biome) {
        return OreControlUtil.isActivated(ore, worldOreConfig, biome);
    }

    @Override
    public boolean isOre(final @NonNull String string) {
        return OreControlUtil.isOre(string);
    }

    @Override
    public boolean isBiome(final @NonNull String string) {
        return OreControlUtil.isBiome(string);
    }

    @Override
    public boolean isSetting(final @NonNull String string) {
        return OreControlUtil.isSetting(string);
    }
}
