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

package de.derfrzocker.ore.control;

import de.derfrzocker.ore.control.api.OreControlService;
import org.apache.commons.lang.Validate;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServiceRegisterEvent;
import org.bukkit.event.server.ServiceUnregisterEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class OreControlServiceSupplier implements Supplier<OreControlService>, Listener {

    @NotNull
    private final Plugin plugin;
    @Nullable
    private OreControlService oreControlService;
    private boolean registered = false;

    public OreControlServiceSupplier(@NotNull final Plugin plugin) {
        Validate.notNull(plugin, "Plugin cannot be null");

        this.plugin = plugin;
    }

    public void registerEvents() {
        if (this.registered) {
            throw new IllegalStateException("Events already registered");
        }

        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);

        this.registered = true;
    }

    @Override
    public OreControlService get() {
        if (this.oreControlService == null) {
            updateProvider();

            if (this.oreControlService == null) {
                throw new RuntimeException("The Bukkit Service has no OreControlService and no OreControlService is cached!");
            }
        }

        return this.oreControlService;
    }

    @EventHandler
    public void onServiceRegister(ServiceRegisterEvent event) {
        if (event.getProvider().getProvider() instanceof OreControlService) {
            updateProvider();
        }
    }

    @EventHandler
    public void onServiceUnregister(ServiceUnregisterEvent event) {
        if (event.getProvider().getProvider() instanceof OreControlService) {
            updateProvider();
        }
    }

    private void updateProvider() {
        final OreControlService service = this.plugin.getServer().getServicesManager().load(OreControlService.class);

        if (service != null) {
            this.oreControlService = service;
        }

    }

}
