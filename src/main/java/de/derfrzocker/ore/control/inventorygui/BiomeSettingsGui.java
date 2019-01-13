package de.derfrzocker.ore.control.inventorygui;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashSet;
import java.util.Set;

public class BiomeSettingsGui implements InventoryGui{

    @Override
    public void onInventoryClick(InventoryClickEvent event) {

    }

    @Override
    public boolean contains(Inventory inventory) {
        return false;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }

    private final class SubBiomeInventory implements InventoryGui{

        private final Set<InventoryButton<OreSettingsGui>> buttons = new HashSet<>();

        private final Inventorys

        @Override
        public void onInventoryClick(InventoryClickEvent event) {

        }

        @Override
        public boolean contains(Inventory inventory) {
            return false;
        }

        @Override
        public Inventory getInventory() {
            return null;
        }
    }

}
