package de.derfrzocker.ore.control.gui;

import de.derfrzocker.ore.control.OreControl;
import lombok.NonNull;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;


public class VerifyGui extends BasicGui {

    public VerifyGui(final @NonNull Consumer<InventoryClickEvent> acceptAction, final @NonNull Consumer<InventoryClickEvent> denyAction) {
        addItem(getSettings().getAcceptSlot(), getSettings().getAcceptItemStack(), acceptAction);
        addItem(getSettings().getDenySlot(), getSettings().getDenyItemStack(), denyAction);
    }

    @Override
    public VerifyGuiSettings getSettings() {
        return VerifyGuiSettings.getInstance();
    }

    private static final class VerifyGuiSettings extends BasicSettings {

        private static VerifyGuiSettings instance = null;

        private static VerifyGuiSettings getInstance() {
            if (instance == null)
                instance = new VerifyGuiSettings();

            return instance;
        }

        private VerifyGuiSettings() {
            super(OreControl.getInstance(), "data/verify_gui.yml");
        }

        private int getAcceptSlot() {
            return getYaml().getInt("accept.slot");
        }

        private ItemStack getAcceptItemStack() {
            return getYaml().getItemStack("accept.item_stack").clone();
        }

        private ItemStack getDenyItemStack() {
            return getYaml().getItemStack("deny.item_stack").clone();
        }

        private int getDenySlot() {
            return getYaml().getInt("deny.slot");
        }

    }

}
