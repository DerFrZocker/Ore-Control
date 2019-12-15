package de.derfrzocker.ore.control.gui.settings;

import de.derfrzocker.spigot.utils.gui.PageSettings;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class BiomeGuiSettings extends PageSettings {

    public BiomeGuiSettings(@NotNull JavaPlugin plugin, @NotNull String file) {
        super(plugin, file);
    }

    public BiomeGuiSettings(@NotNull JavaPlugin plugin, @NotNull String file, boolean copy) {
        super(plugin, file, copy);
    }

    public BiomeGuiSettings(@NotNull JavaPlugin plugin, @NotNull Supplier<ConfigurationSection> configurationSectionSupplier) {
        super(plugin, configurationSectionSupplier);
    }

    @NotNull
    public ItemStack getBiomeItemStack(@NotNull final String biome) {
        return getSection().getItemStack("biomes." + biome).clone();
    }

    public int getBiomeGroupSwitchSlot() {
        return getSection().getInt("biome-group.slot");
    }

    @NotNull
    public ItemStack getBiomeGroupItemStack() {
        return getSection().getItemStack("biome-group.group.item-stack").clone();
    }

    @NotNull
    public ItemStack getBiomeItemStack() {
        return getSection().getItemStack("biome-group.biome.item-stack").clone();
    }

    @NotNull
    public ItemStack getInfoItemStack() {
        return getSection().getItemStack("info.item-stack").clone();
    }

    public int getInfoSlot() {
        return getSection().getInt("info.slot");
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

}
