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

package de.derfrzocker.ore.control.gui.settings;

import de.derfrzocker.spigot.utils.gui.BasicSettings;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class SettingsGuiSettings extends BasicSettings {

    public SettingsGuiSettings(@NotNull Plugin plugin, @NotNull String file) {
        super(plugin, file);
    }

    public SettingsGuiSettings(@NotNull Plugin plugin, @NotNull String file, boolean copy) {
        super(plugin, file, copy);
    }

    public SettingsGuiSettings(@NotNull Plugin plugin, @NotNull Supplier<ConfigurationSection> configurationSectionSupplier) {
        super(plugin, configurationSectionSupplier);
    }

    public int getOreSlot() {
        return getSection().getInt("ore.slot");
    }

    @NotNull
    public ItemStack getInfoItemStack() {
        return getSection().getItemStack("info.item-stack").clone();
    }

    @NotNull
    public ItemStack getInfoBiomeItemStack() {
        return getSection().getItemStack("info.biome-item-stack").clone();
    }

    public int getInfoSlot() {
        return getSection().getInt("info.slot");
    }

    @NotNull
    public Set<SettingsGuiSettings.ItemStackValues> getItemStackValues() {
        final Set<SettingsGuiSettings.ItemStackValues> set = new HashSet<>();
        getSection().getConfigurationSection("items").
                getKeys(false).stream().
                map(value -> getSection().getConfigurationSection("items." + value)).
                map(value -> new SettingsGuiSettings.ItemStackValues(value.getInt("slot", 0), value.getDouble("value", 0), value.getItemStack("item-stack").clone())).
                forEach(set::add);
        return set;
    }

    @NotNull
    public ItemStack getDefaultOreItemStack() {
        return getSection().getItemStack("default-ore-item-stack").clone();
    }

    @NotNull
    public ItemStack getDefaultBiomeOreItemStack() {
        return getSection().getItemStack("default-biome-ore-item-stack").clone();
    }

    @NotNull
    public ItemStack getBackItemStack() {
        return getSection().getItemStack("back.item-stack").clone();
    }

    public int getBackSlot() {
        return getSection().getInt("back.slot");
    }

    public int getResetValueSlot() {
        return getSection().getInt("value.reset.slot");
    }

    @NotNull
    public ItemStack getResetValueItemStack() {
        return getSection().getItemStack("value.reset.item-stack").clone();
    }

    public int getCopyValueSlot() {
        return getSection().getInt("value.copy.slot");
    }

    @NotNull
    public ItemStack getCopyValueItemStack() {
        return getSection().getItemStack("value.copy.item-stack").clone();
    }

    public static final class ItemStackValues {
        private final int slot;
        private final double value;
        @NotNull
        private final ItemStack itemStack;

        private ItemStackValues(final int slot, final double value, @NotNull final ItemStack itemStack) {
            this.slot = slot;
            this.value = value;
            this.itemStack = itemStack;
        }

        public int getSlot() {
            return slot;
        }

        public double getValue() {
            return value;
        }

        @NotNull
        public ItemStack getItemStack() {
            return itemStack;
        }

    }

}
