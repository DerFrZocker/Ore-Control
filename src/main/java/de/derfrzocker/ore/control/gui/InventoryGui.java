package de.derfrzocker.ore.control.gui;

import de.derfrzocker.ore.control.OreControl;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.concurrent.ExecutionException;

interface InventoryGui extends InventoryHolder {

    void onInventoryClick(InventoryClickEvent event);

    default void openSync(final @NonNull HumanEntity entity, final @NonNull Inventory inventory) {
        if (Bukkit.isPrimaryThread()) {
            entity.openInventory(inventory);
            return;
        }

        try {
            Bukkit.getScheduler().callSyncMethod(OreControl.getInstance(), () -> entity.openInventory(inventory)).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    default void closeSync(final @NonNull HumanEntity entity) {
        if (Bukkit.isPrimaryThread()) {
            entity.closeInventory();
            return;
        }

        try {
            Bukkit.getScheduler().callSyncMethod(OreControl.getInstance(), () -> {
                entity.closeInventory();
                return true;
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

}
