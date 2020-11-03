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

package de.derfrzocker.ore.control.gui.settings;

import de.derfrzocker.spigot.utils.gui.PageSettings;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class WorldGuiSettings extends PageSettings {

    public WorldGuiSettings(@NotNull Plugin plugin, @NotNull String file) {
        super(plugin, file);
    }

    public WorldGuiSettings(@NotNull Plugin plugin, @NotNull String file, boolean copy) {
        super(plugin, file, copy);
    }

    public WorldGuiSettings(@NotNull Plugin plugin, @NotNull Supplier<ConfigurationSection> configurationSectionSupplier) {
        super(plugin, configurationSectionSupplier);
    }

    @NotNull
    public ItemStack getWorldItemStack() {
        return getSection().getItemStack("world-item-stack").clone();
    }

    @NotNull
    public ItemStack getTemplateItemStack() {
        return getSection().getItemStack("template.item-stack").clone();
    }

    @NotNull
    public ItemStack getCreateTemplateItemStack() {
        return getSection().getItemStack("template.create.item-stack").clone();
    }

    public int getCreateTemplateSlot() {
        return getSection().getInt("template.create.slot");
    }

    public int getEditConfigSlot() {
        return getSection().getInt("config.edit.slot");
    }

    @NotNull
    public ItemStack getEditConfigItemStack() {
        return getSection().getItemStack("config.edit.item-stack").clone();
    }

}
