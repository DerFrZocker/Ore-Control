package de.derfrzocker.spigot.utils.gui;

import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class PageSettings extends BasicSettings {

    public PageSettings(JavaPlugin plugin, String file) {
        super(plugin, file);
    }

    public int getNextPageSlot() {
        return getYaml().getInt("next_page.slot");
    }

    public ItemStack getNextPageItemStack() {
        return getYaml().getItemStack("next_page.item_stack").clone();
    }

    public int getPreviousPageSlot() {
        return getYaml().getInt("previous_page.slot");
    }

    public ItemStack getPreviousPageItemStack() {
        return getYaml().getItemStack("previous_page.item_stack").clone();
    }

    public int getGap() {
        return getYaml().getInt("inventory.gap");
    }

    public int getEmptyRows() {
        return getYaml().getInt("inventory.empty_rows");
    }

}
