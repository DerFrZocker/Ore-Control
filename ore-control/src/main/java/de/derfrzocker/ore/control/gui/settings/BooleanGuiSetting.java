package de.derfrzocker.ore.control.gui.settings;

import de.derfrzocker.spigot.utils.gui.BasicSettings;
import de.derfrzocker.spigot.utils.gui.VerifyGui;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class BooleanGuiSetting extends BasicSettings implements VerifyGui.VerifyGuiSettingsInterface {

    public BooleanGuiSetting(@NotNull JavaPlugin plugin, @NotNull String file) {
        super(plugin, file);
    }

    public BooleanGuiSetting(@NotNull JavaPlugin plugin, @NotNull String file, boolean copy) {
        super(plugin, file, copy);
    }

    public BooleanGuiSetting(@NotNull JavaPlugin plugin, @NotNull Supplier<ConfigurationSection> configurationSectionSupplier) {
        super(plugin, configurationSectionSupplier);
    }

    @Override
    public int getAcceptSlot() {
        return getSection().getInt("true.slot");
    }

    @Override
    public ItemStack getAcceptItemStack() {
        return getSection().getItemStack("true.item-stack").clone();
    }

    @Override
    public ItemStack getDenyItemStack() {
        return getSection().getItemStack("false.item-stack").clone();
    }

    @Override
    public int getDenySlot() {
        return getSection().getInt("false.slot");
    }

    @NotNull
    public ItemStack getInfoItemStack() {
        return getSection().getItemStack("info.item-stack").clone();
    }

    public int getInfoSlot() {
        return getSection().getInt("info.slot");
    }

}