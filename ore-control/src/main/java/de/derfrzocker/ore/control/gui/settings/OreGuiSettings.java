package de.derfrzocker.ore.control.gui.settings;

import de.derfrzocker.spigot.utils.gui.BasicSettings;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class OreGuiSettings extends BasicSettings {

    public OreGuiSettings(@NotNull JavaPlugin plugin, @NotNull String file) {
        super(plugin, file);
    }

    public OreGuiSettings(@NotNull JavaPlugin plugin, @NotNull String file, boolean copy) {
        super(plugin, file, copy);
    }

    public OreGuiSettings(@NotNull JavaPlugin plugin, @NotNull Supplier<ConfigurationSection> configurationSectionSupplier) {
        super(plugin, configurationSectionSupplier);
    }

    public int getOreGap() {
        return getSection().getInt("ore-gap");
    }

    public ItemStack getInfoItemStack() {
        return getSection().getItemStack("info.item-stack").clone();
    }

    public ItemStack getInfoBiomeItemStack() {
        return getSection().getItemStack("info.biome-item-stack").clone();
    }

    public int getInfoSlot() {
        return getSection().getInt("info.slot");
    }

    public ItemStack getDefaultOreItemStack() {
        return getSection().getItemStack("default-ore-item_stack").clone();
    }

    public ItemStack getBackItemStack() {
        return getSection().getItemStack("back.item-stack").clone();
    }

    public int getBackSlot() {
        return getSection().getInt("back.slot");
    }

    public int getResetValueSlot() {
        return getSection().getInt("value.reset.slot");
    }

    public ItemStack getResetValueItemStack() {
        return getSection().getItemStack("value.reset.item-stack").clone();
    }

    public int getCopyValueSlot() {
        return getSection().getInt("value.copy.slot");
    }

    public ItemStack getCopyValueItemStack() {
        return getSection().getItemStack("value.copy.item-stack").clone();
    }

}