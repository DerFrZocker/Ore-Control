package de.derfrzocker.ore.control.gui.settings;

import de.derfrzocker.ore.control.api.Setting;
import de.derfrzocker.spigot.utils.gui.BasicSettings;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class OreSettingsGuiSettings extends BasicSettings {

    public OreSettingsGuiSettings(@NotNull JavaPlugin plugin, @NotNull String file) {
        super(plugin, file);
    }

    public OreSettingsGuiSettings(@NotNull JavaPlugin plugin, @NotNull String file, boolean copy) {
        super(plugin, file, copy);
    }

    public OreSettingsGuiSettings(@NotNull JavaPlugin plugin, @NotNull Supplier<ConfigurationSection> configurationSectionSupplier) {
        super(plugin, configurationSectionSupplier);
    }

    public int getSettingStartSlot() {
        return getSection().getInt("setting-start-slot");
    }

    @NotNull
    public ItemStack getSettingsItemStack(@NotNull final Setting setting) {
        return getSection().getItemStack("settings-item-stack." + setting.toString()).clone();
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
    public ItemStack getBackItemStack() {
        return getSection().getItemStack("back.item-stack").clone();
    }

    public int getBackSlot() {
        return getSection().getInt("back.slot");
    }

    public int getStatusSlot() {
        return getSection().getInt("status.slot");
    }

    @NotNull
    public ItemStack getActivateItemStack() {
        return getSection().getItemStack("status.activate").clone();
    }

    @NotNull
    public ItemStack getDeactivateItemStack() {
        return getSection().getItemStack("status.deactivate").clone();
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
