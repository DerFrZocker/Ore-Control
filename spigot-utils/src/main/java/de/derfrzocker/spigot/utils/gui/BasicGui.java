package de.derfrzocker.spigot.utils.gui;

import de.derfrzocker.spigot.utils.MessageUtil;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public abstract class BasicGui implements InventoryGui {

    @Getter
    private final Inventory inventory;

    private final Map<Integer, Consumer<InventoryClickEvent>> button = new HashMap<>();

    private final BasicSettings basicSettings;

    public BasicGui(final BasicSettings basicSettings) {
        this.basicSettings = basicSettings;
        inventory = Bukkit.createInventory(this, basicSettings.getRows() * 9, MessageUtil.replacePlaceHolder(basicSettings.getInventoryName()));
    }

    public void addItem(final int slot, final @NonNull ItemStack itemStack) {
        getInventory().setItem(slot, itemStack);
    }

    public void addItem(final int slot, final @NonNull ItemStack itemStack, final @NonNull Consumer<InventoryClickEvent> consumer) {
        button.put(slot, consumer);
        getInventory().setItem(slot, itemStack);
    }

    @Override
    public void onInventoryClick(final InventoryClickEvent event) {
        final Consumer<InventoryClickEvent> consumer = button.get(event.getRawSlot());

        if (consumer != null)
            consumer.accept(event);
    }
}
