package de.derfrzocker.ore.control.gui.settings;

import de.derfrzocker.spigot.utils.gui.BasicSettings;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class ConfigGuiSettings extends BasicSettings {

    public ConfigGuiSettings(@NotNull JavaPlugin plugin, @NotNull String file) {
        super(plugin, file);
    }

    public ConfigGuiSettings(@NotNull JavaPlugin plugin, @NotNull String file, boolean copy) {
        super(plugin, file, copy);
    }

    public ConfigGuiSettings(@NotNull JavaPlugin plugin, @NotNull Supplier<ConfigurationSection> configurationSectionSupplier) {
        super(plugin, configurationSectionSupplier);
    }

    public int getsafeModeSlot() {
        return getSection().getInt("safe-mode.slot");
    }

    @NotNull
    public ItemStack getsafeModeItemStack() {
        return getSection().getItemStack("safe-mode.item-stack").clone();
    }

    public int getLanguageSlot() {
        return getSection().getInt("language.slot");
    }

    @NotNull
    public ItemStack getLanguageItemStack() {
        return getSection().getItemStack("language.item-stack").clone();
    }

    public int getTranslateTabCompilationSlot() {
        return getSection().getInt("translate-tab-compilation.slot");
    }

    @NotNull
    public ItemStack getTranslateTabCompilationItemStack() {
        return getSection().getItemStack("translate-tab-compilation.item-stack").clone();
    }

    public int getVerifyCopyActionSlot() {
        return getSection().getInt("verify.copy-action.slot");
    }

    @NotNull
    public ItemStack getVerifyCopyActionItemStack() {
        return getSection().getItemStack("verify.copy-action.item-stack").clone();
    }

    public int getVerifyResetActionSlot() {
        return getSection().getInt("verify.reset-action.slot");
    }

    @NotNull
    public ItemStack getVerifyResetActionItemStack() {
        return getSection().getItemStack("verify.reset-action.item-stack").clone();
    }

}
