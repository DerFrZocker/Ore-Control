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

import de.derfrzocker.ore.control.api.ConfigType;
import de.derfrzocker.ore.control.api.WorldOreConfig;
import de.derfrzocker.ore.control.api.dao.WorldOreConfigDao;
import de.derfrzocker.ore.control.impl.WorldOreConfigYamlImpl;
import de.derfrzocker.spigot.utils.Config;
import de.derfrzocker.spigot.utils.ReloadAble;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class WorldOreConfigYamlDao implements WorldOreConfigDao, ReloadAble {

    private final static String GLOBAL_NAME = "__global__";

    private final Map<String, LazyWorldOreConfigCache> lazyWorldOreConfigCacheMap = new HashMap<>();
    private LazyWorldOreConfigCache global;
    @NotNull
    private final File directory;
    @NotNull
    private final File globalDirectory;
    @NotNull
    private final File globalFile;

    public WorldOreConfigYamlDao(@NotNull final File directory) {
        Validate.notNull(directory, "Directory cannot be null");
        if (directory.exists()) {
            Validate.isTrue(directory.isDirectory(), "Directory is not a directory?");
        }

        this.directory = directory;
        this.globalDirectory = new File(directory, GLOBAL_NAME);

        if (globalDirectory.exists()) {
            Validate.isTrue(globalDirectory.isDirectory(), "Global directory is not a directory?");
        }

        this.globalFile = new File(globalDirectory, GLOBAL_NAME + ".yml");

        if (globalFile.exists()) {
            Validate.isTrue(globalFile.isFile(), "Global file is not a file?");
        }

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
            if (key.equals(GLOBAL_NAME)) {
                return Optional.of(global.getWorldOreConfig());
            }
            return Optional.empty();
        }

        final LazyWorldOreConfigCache lazyWorldConfigCache1 = new LazyWorldOreConfigCache(file);

        lazyWorldOreConfigCacheMap.put(key, lazyWorldConfigCache1);

        return Optional.of(lazyWorldConfigCache1.getWorldOreConfig());
    }

    @Override
    public void remove(@NotNull final WorldOreConfig value) {
        Validate.notNull(value, "WorldOreConfig cannot be null");

        if (value.getConfigType() == ConfigType.GLOBAL) {
            global.setWorldConfig(new WorldOreConfigYamlImpl(GLOBAL_NAME, ConfigType.GLOBAL));
            global.save();
            return;
        }

        lazyWorldOreConfigCacheMap.remove(value.getName());
        new File(directory, value.getName() + ".yml").delete();
    }

    @Override
    public void save(@NotNull final WorldOreConfig value) {
        Validate.notNull(value, "WorldOreConfig cannot be null");

        if (value.getConfigType() == ConfigType.GLOBAL) {
            global.setWorldConfig(value);
            global.save();
            return;
        }

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

        worldConfigs.add(global.getWorldOreConfig());

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

            if (file.getName().equals("Default.yml") && !globalFile.exists()) {
                try {
                    globalDirectory.mkdirs();
                    globalFile.createNewFile();

                    Config config = new Config(file);
                    WorldOreConfig worldOreConfig = (WorldOreConfig) config.get("value");

                    Config globalConfig = new Config(globalFile);
                    WorldOreConfig globalWorldOreConfig = worldOreConfig.clone(GLOBAL_NAME);
                    globalWorldOreConfig.setConfigType(ConfigType.GLOBAL);
                    globalConfig.set("value", globalWorldOreConfig);
                    globalConfig.save(globalFile);

                    file.delete();
                    continue;
                } catch (IOException e) {
                    throw new RuntimeException("Error while moving file", e);
                }
            }

            lazyWorldOreConfigCacheMap.put(file.getName().substring(0, file.getName().length() - 4), new LazyWorldOreConfigCache(file));
        }

        if (!globalFile.exists()) {
            try {
                globalDirectory.mkdirs();
                globalFile.createNewFile();

                Config config = new Config(globalFile);
                config.set("value", new WorldOreConfigYamlImpl(GLOBAL_NAME, ConfigType.GLOBAL));
                config.save(globalFile);
            } catch (IOException e) {
                throw new RuntimeException("Error while setting up global", e);
            }
        }

        global = new LazyWorldOreConfigCache(globalFile);
    }

    @NotNull
    @Override
    public WorldOreConfig getGlobalWorldOreConfig() {
        return global.getWorldOreConfig();
    }

}
