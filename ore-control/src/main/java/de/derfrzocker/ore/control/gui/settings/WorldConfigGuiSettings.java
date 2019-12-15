package de.derfrzocker.ore.control.gui.settings;

import de.derfrzocker.spigot.utils.gui.BasicSettings;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class WorldConfigGuiSettings extends BasicSettings {

    public WorldConfigGuiSettings(@NotNull JavaPlugin plugin, @NotNull String file) {
        super(plugin, file);
    }

    public WorldConfigGuiSettings(@NotNull JavaPlugin plugin, @NotNull String file, boolean copy) {
        super(plugin, file, copy);
    }

    public WorldConfigGuiSettings(@NotNull JavaPlugin plugin, @NotNull Supplier<ConfigurationSection> configurationSectionSupplier) {
        super(plugin, configurationSectionSupplier);
    }

    public int getBiomeItemStackSlot() {
        return getSection().getInt("biome.slot");
    }

    @NotNull
    public ItemStack getBiomeItemStack() {
        return getSection().getItemStack("biome.item-stack").clone();
    }

    public int getOreItemStackSlot() {
        return getSection().getInt("ore.slot");
    }

    @NotNull
    public ItemStack getOreItemStack() {
        return getSection().getItemStack("ore.item-stack").clone();
    }

    @NotNull
    public ItemStack getBackItemStack() {
        return getSection().getItemStack("back.item-stack").clone();
    }

    @NotNull
    public ItemStack getInfoItemStack() {
        return getSection().getItemStack("info.item-stack").clone();
    }

    public int getInfoSlot() {
        return getSection().getInt("info.slot");
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

    public int getTemplateDeleteSlot() {
        return getSection().getInt("template.delete.slot");
    }

    @NotNull
    public ItemStack getTemplateDeleteItemStack() {
        return getSection().getItemStack("template.delete.item-stack").clone();
    }

}
