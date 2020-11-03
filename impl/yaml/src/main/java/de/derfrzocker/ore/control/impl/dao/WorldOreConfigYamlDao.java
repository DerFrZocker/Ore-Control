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

package de.derfrzocker.ore.control.impl.dao;

import de.derfrzocker.ore.control.api.WorldOreConfig;
import de.derfrzocker.ore.control.api.dao.WorldOreConfigDao;
import de.derfrzocker.spigot.utils.ReloadAble;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

public class WorldOreConfigYamlDao implements WorldOreConfigDao, ReloadAble {

    private final Map<String, LazyWorldOreConfigCache> lazyWorldOreConfigCacheMap = new HashMap<>();
    @NotNull
    private final File directory;

    public WorldOreConfigYamlDao(@NotNull final File directory) {
        Validate.notNull(directory, "Directory cannot be null");
        if (directory.exists()) {
            Validate.isTrue(directory.isDirectory(), "Directory is not a directory?");
        }

        this.directory = directory;

        RELOAD_ABLES.add(this);
    }

    @Override
    public Optional<WorldOreConfig> get(@NotNull final String key) {
        Validate.notNull(key, "Key cannot be null");
        Validate.notEmpty(key, "Key cannot be empty");

        final LazyWorldOreConfigCache lazyWorldOreConfigCache = lazyWorldOreConfigCacheMap.get(key);

        if (lazyWorldOreConfigCache != null) {
            return Optional.of(lazyWorldOreConfigCache.getWorldOreConfig());
        }

        final File file = new File(directory, key + ".yml");

        if (!file.exists() || !file.isFile()) {
            return Optional.empty();
        }

        final LazyWorldOreConfigCache lazyWorldConfigCache1 = new LazyWorldOreConfigCache(file);

        lazyWorldOreConfigCacheMap.put(key, lazyWorldConfigCache1);

        return Optional.of(lazyWorldConfigCache1.getWorldOreConfig());
    }

    @Override
    public void remove(@NotNull final WorldOreConfig value) {
        Validate.notNull(value, "WorldOreConfig cannot be null");

        lazyWorldOreConfigCacheMap.remove(value.getName());
        new File(directory, value.getName() + ".yml").delete();
    }

    @Override
    public void save(@NotNull final WorldOreConfig value) {
        Validate.notNull(value, "WorldOreConfig cannot be null");

        LazyWorldOreConfigCache lazyWorldOreConfigCache = lazyWorldOreConfigCacheMap.get(value.getName());

        if (lazyWorldOreConfigCache == null) {
            lazyWorldOreConfigCache = new LazyWorldOreConfigCache(new File(directory, value.getName() + ".yml"));
            lazyWorldOreConfigCacheMap.put(value.getName(), lazyWorldOreConfigCache);
        }

        lazyWorldOreConfigCache.setWorldConfig(value);
        lazyWorldOreConfigCache.save();
    }

    @Override
    public Set<WorldOreConfig> getAll() {
        final Set<WorldOreConfig> worldConfigs = new LinkedHashSet<>();

        lazyWorldOreConfigCacheMap.forEach((name, lazyWorldConfigCache) -> worldConfigs.add(lazyWorldConfigCache.getWorldOreConfig()));

        return worldConfigs;
    }

    @Override
    public void reload() {
        lazyWorldOreConfigCacheMap.clear();

        final File[] files = directory.listFiles();

        if (files == null)
            return;

        for (final File file : files) {
            if (!file.isFile()) {
                continue;
            }

            if (!file.getName().endsWith(".yml")) {
                continue;
            }

            lazyWorldOreConfigCacheMap.put(file.getName().substring(0, file.getName().length() - 4), new LazyWorldOreConfigCache(file));
        }
    }
}
