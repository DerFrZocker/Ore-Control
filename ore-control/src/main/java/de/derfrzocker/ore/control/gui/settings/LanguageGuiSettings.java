package de.derfrzocker.ore.control.gui.settings;

import de.derfrzocker.spigot.utils.Language;
import de.derfrzocker.spigot.utils.gui.BasicSettings;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class LanguageGuiSettings extends BasicSettings {

    public LanguageGuiSettings(@NotNull JavaPlugin plugin, @NotNull String file) {
        super(plugin, file);
    }

    public LanguageGuiSettings(@NotNull JavaPlugin plugin, @NotNull String file, boolean copy) {
        super(plugin, file, copy);
    }

    public LanguageGuiSettings(@NotNull JavaPlugin plugin, @NotNull Supplier<ConfigurationSection> configurationSectionSupplier) {
        super(plugin, configurationSectionSupplier);
    }

    @NotNull
    public ItemStack getLanguageItemStack(@NotNull final Language language) {
        return getSection().getItemStack("language." + language).clone();
    }

    public int getLanguageGap() {
        return getSection().getInt("inventory.language-gap");
    }

    @NotNull
    public ItemStack getInfoItemStack() {
        return getSection().getItemStack("info.item-stack").clone();
    }

    public int getInfoSlot() {
        return getSection().getInt("info.slot");
    }

}
