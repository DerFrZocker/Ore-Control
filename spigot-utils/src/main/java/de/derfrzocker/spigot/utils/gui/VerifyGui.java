package de.derfrzocker.spigot.utils.gui;

import de.derfrzocker.spigot.utils.MessageUtil;
import lombok.NonNull;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;


public class VerifyGui extends BasicGui {


    public VerifyGui(final @NonNull Consumer<InventoryClickEvent> acceptAction, final @NonNull Consumer<InventoryClickEvent> denyAction) {
        this(acceptAction, denyAction, VerifyGuiSettings.getInstance());
    }

    public <T extends BasicSettings & VerifyGui.VerifyGuiSettingsInterface> VerifyGui(final @NonNull Consumer<InventoryClickEvent> acceptAction, final @NonNull Consumer<InventoryClickEvent> denyAction, final T setting) {
        super(setting);
        addItem(setting.getAcceptSlot(), MessageUtil.replaceItemStack(setting.getAcceptItemStack()), acceptAction);
        addItem(setting.getDenySlot(), MessageUtil.replaceItemStack(setting.getDenyItemStack()), denyAction);
    }

    public static final class VerifyGuiSettings extends BasicSettings implements VerifyGui.VerifyGuiSettingsInterface {

        private static VerifyGuiSettings instance = null;

        public static VerifyGuiSettings getInstance() {
            if (instance == null)
                instance = new VerifyGuiSettings();

            return instance;
        }

        private VerifyGuiSettings() {
            super(InventoryClickListener.getPlugin(), "data/verify_gui.yml");
        }

        @Override
        public int getAcceptSlot() {
            return getYaml().getInt("accept.slot");
        }

        @Override
        public ItemStack getAcceptItemStack() {
            return getYaml().getItemStack("accept.item_stack").clone();
        }

        @Override
        public ItemStack getDenyItemStack() {
            return getYaml().getItemStack("deny.item_stack").clone();
        }

        @Override
        public int getDenySlot() {
            return getYaml().getInt("deny.slot");
        }

    }

    public interface VerifyGuiSettingsInterface {

        int getAcceptSlot();

        ItemStack getAcceptItemStack();

        ItemStack getDenyItemStack();

        int getDenySlot();

    }

}
