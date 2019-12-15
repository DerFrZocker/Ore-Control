package de.derfrzocker.ore.control.gui.settings;

import de.derfrzocker.spigot.utils.gui.PageSettings;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class WorldGuiSettings extends PageSettings {

    public WorldGuiSettings(@NotNull JavaPlugin plugin, @NotNull String file) {
        super(plugin, file);
    }

    public WorldGuiSettings(@NotNull JavaPlugin plugin, @NotNull String file, boolean copy) {
        super(plugin, file, copy);
    }

    public WorldGuiSettings(@NotNull JavaPlugin plugin, @NotNull Supplier<ConfigurationSection> configurationSectionSupplier) {
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
