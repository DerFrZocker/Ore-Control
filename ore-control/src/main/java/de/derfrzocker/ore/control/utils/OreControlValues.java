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

package de.derfrzocker.ore.control.utils;

import de.derfrzocker.ore.control.ConfigValues;
import de.derfrzocker.ore.control.OreControlMessages;
import de.derfrzocker.ore.control.Permissions;
import de.derfrzocker.ore.control.api.OreControlService;
import org.apache.commons.lang.Validate;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class OreControlValues {

    @NotNull
    private final Supplier<OreControlService> serviceSupplier;
    @NotNull
    private final JavaPlugin javaPlugin;
    @NotNull
    private final ConfigValues configValues;
    @NotNull
    private final OreControlMessages oreControlMessages;
    @NotNull
    private final Permissions permissions;

    public OreControlValues(@NotNull final Supplier<OreControlService> serviceSupplier, @NotNull final JavaPlugin javaPlugin, @NotNull final ConfigValues configValues, @NotNull final OreControlMessages oreControlMessages, @NotNull final Permissions permissions) {
        Validate.notNull(serviceSupplier, "Service supplier cannot be null");
        Validate.notNull(javaPlugin, "JavaPlugin cannot be null");
        Validate.notNull(configValues, "ConfigValues cannot be null");
        Validate.notNull(oreControlMessages, "OreControlMessages cannot be null");
        Validate.notNull(permissions, "Permissions cannot be null");

        this.serviceSupplier = serviceSupplier;
        this.javaPlugin = javaPlugin;
        this.configValues = configValues;
        this.oreControlMessages = oreControlMessages;
        this.permissions = permissions;
    }

    @NotNull
    public Supplier<OreControlService> getServiceSupplier() {
        return serviceSupplier;
    }

    @NotNull
    public OreControlService getService() {
        return getServiceSupplier().get();
    }

    @NotNull
    public JavaPlugin getJavaPlugin() {
        return javaPlugin;
    }

    @NotNull
    public ConfigValues getConfigValues() {
        return configValues;
    }

    @NotNull
    public OreControlMessages getOreControlMessages() {
        return oreControlMessages;
    }

    @NotNull
    public Permissions getPermissions() {
        return permissions;
    }

}
