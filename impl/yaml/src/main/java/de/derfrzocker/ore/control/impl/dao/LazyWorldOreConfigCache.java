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
import de.derfrzocker.ore.control.impl.WorldOreConfigYamlImpl;
import de.derfrzocker.spigot.utils.Config;
import de.derfrzocker.spigot.utils.ReloadAble;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;

public class LazyWorldOreConfigCache implements ReloadAble {

    private final Object look = new Object();
    @NotNull
    private final File file;

    @Nullable
    private WorldOreConfig worldOreConfig;

    public LazyWorldOreConfigCache(@NotNull final File file) {
        Validate.notNull(file, "File cannot be null");
        Validate.isTrue(file.getName().endsWith(".yml"), "File " + file + " has not valid extension, must be '.yml'");

        if (file.exists()) {
            Validate.isTrue(file.isFile(), "File " + file + " is not a File?");
        }

        this.file = file;
    }

    /**
     * @param worldOreConfig to set
     * @throws IllegalArgumentException if worldOreConfig is null
     * @throws RuntimeException         if the file name and the worldOreConfig name does't match
     */
    public void setWorldConfig(@NotNull final WorldOreConfig worldOreConfig) {
        Validate.notNull(worldOreConfig, "worldOreConfig cannot be null");

        if (!worldOreConfig.getName().equals(file.getName().substring(0, file.getName().length() - 4))) {
            throw new RuntimeException("File name " + file.getName() + " and WorldConfig name " + worldOreConfig.getName() + " does not match");
        }

        this.worldOreConfig = worldOreConfig;
    }

    /**
     * When this LazyWorldOreConfigCache has a WorldOreConfig loaded, it will save it to disk.
     * Nothing will happen, if this LazyWorldOreConfigCache does not holds a WorldOreConfig
     */
    public void save() {
        if (worldOreConfig == null) {
            return;
        }

        final WorldOreConfig worldOreConfig;

        if (!(this.worldOreConfig instanceof ConfigurationSerializable)) {
            worldOreConfig = new WorldOreConfigYamlImpl(this.worldOreConfig.getName(), this.worldOreConfig.isTemplate(), this.worldOreConfig.getOreSettings(), this.worldOreConfig.getBiomeOreSettings());
        } else {
            worldOreConfig = this.worldOreConfig;
        }

        final Config config = new Config(file);

        config.set("value", worldOreConfig);

        try {
            config.options().header("This file is machine generated, please use the in game commands and gui to change values. \nModifying this file per hand on your own risk.").copyHeader(true);
            config.save(file);
        } catch (final IOException e) {
            throw new RuntimeException("Unexpected error while saving WorldOreConfig " + worldOreConfig.getName() + " to disk!", e);
        }
    }

    /**
     * When this LazyWorldOreConfigCache has a WorldOreConfig loaded, it will return it.
     * If not, then it will try to load if from a file and set it to the cache.
     *
     * @return the cached or fresh loaded WorldOreConfig
     * @throws RuntimeException if no WorldOreConfig is Cached and the file does not exists
     * @throws RuntimeException if no WorldOreConfig is Cached and the file does not contains a WorldOreConfig under the key "value"
     * @throws RuntimeException if no WorldOreConfig is Cached and the file name and WorldOreConfig name does not match
     */
    @NotNull
    public WorldOreConfig getWorldOreConfig() {
        if (worldOreConfig != null) {
            return worldOreConfig;
        }

        synchronized (look) {
            if (worldOreConfig != null) {
                return worldOreConfig;
            }

            if (!file.exists())
                throw new RuntimeException("File " + file + " does not exists, cannot load WorldOreConfig from none existing file");

            final Config config = new Config(file);

            final Object object = config.get("value");

            if (!(object instanceof WorldOreConfig)) {
                throw new RuntimeException("File " + file + " does not have a WorldOreConfig under the key 'value'");
            }

            final WorldOreConfig worldOreConfig = (WorldOreConfig) object;

            if (!worldOreConfig.getName().equals(file.getName().substring(0, file.getName().length() - 4))) {
                throw new RuntimeException("File name " + file.getName() + " and WorldOreConfig name " + worldOreConfig.getName() + " does not match");
            }

            this.worldOreConfig = worldOreConfig;
        }

        return worldOreConfig;
    }

    @Override
    public void reload() {
        worldOreConfig = null;
    }

}
