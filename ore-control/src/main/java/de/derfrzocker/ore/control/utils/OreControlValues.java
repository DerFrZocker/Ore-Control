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

package de.derfrzocker.ore.control.utils;

import de.derfrzocker.ore.control.ConfigValues;
import de.derfrzocker.ore.control.OreControlMessages;
import de.derfrzocker.ore.control.Permissions;
import de.derfrzocker.ore.control.api.OreControlService;
import de.derfrzocker.spigot.utils.Version;
import org.apache.commons.lang.Validate;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class OreControlValues {

    @NotNull
    private final Supplier<OreControlService> serviceSupplier;
    @NotNull
    private final Plugin plugin;
    @NotNull
    private final ConfigValues configValues;
    @NotNull
    private final OreControlMessages oreControlMessages;
    @NotNull
    private final Permissions permissions;
    @NotNull
    private final Version version;

    public OreControlValues(@NotNull final Supplier<OreControlService> serviceSupplier, @NotNull final Plugin plugin, @NotNull final ConfigValues configValues, @NotNull final OreControlMessages oreControlMessages, @NotNull final Permissions permissions, @NotNull final Version version) {
        Validate.notNull(serviceSupplier, "Service supplier cannot be null");
        Validate.notNull(plugin, "Plugin cannot be null");
        Validate.notNull(configValues, "ConfigValues cannot be null");
        Validate.notNull(oreControlMessages, "OreControlMessages cannot be null");
        Validate.notNull(permissions, "Permissions cannot be null");
        Validate.notNull(version, "Version cannot be null");

        this.serviceSupplier = serviceSupplier;
        this.plugin = plugin;
        this.configValues = configValues;
        this.oreControlMessages = oreControlMessages;
        this.permissions = permissions;
        this.version = version;
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
    public Plugin getPlugin() {
        return plugin;
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

    @NotNull
    public Version getVersion() {
        return version;
    }

}
