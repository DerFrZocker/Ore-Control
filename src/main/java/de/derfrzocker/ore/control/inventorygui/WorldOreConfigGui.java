package de.derfrzocker.ore.control.inventorygui;

import com.google.common.base.Preconditions;
import de.derfrzocker.ore.control.OreControl;
import de.derfrzocker.ore.control.api.WorldOreConfig;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class WorldOreConfigGui implements InventoryGui{

    private final InventoryButton<BiomeSettingsGui> biomeInventory;

    private final InventoryButton<OreSettingsGui> oreInventory;

    @Getter
    private final Inventory inventory;

    private final String world;

    public WorldOreConfigGui(@NonNull String world){
        World world1 = Bukkit.getWorld(world);

        Preconditions.checkNotNull(world1);

        this.world = world;

        WorldOreConfig config = OreControl.getService().getWorldOreConfig(world1).orElseGet(() ->OreControl.getService().createWorldOreConfig(world1));

        inventory = Bukkit.createInventory(null, 9, ""); // TODO read from settings

        biomeInventory = new InventoryButton<>(2); // TODO read from settings
        oreInventory = new InventoryButton<>(4); // TODO read from settings
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        if(event.getRawSlot() == biomeInventory.getSlot()){
            if(biomeInventory.getValue() == null){
                // TODO create biome Inventory
            }
            openSync(event.getWhoClicked(), biomeInventory.getValue().getInventory());
            return;
        }
        if(event.getRawSlot() == oreInventory.getSlot()){
            if(oreInventory.getValue() == null){
                // TODO create biome Inventory
            }
           openSync(event.getWhoClicked(), oreInventory.getValue().getInventory());
        }
    }

    @Override
    public boolean contains(Inventory inventory) {
        return this.inventory.equals(inventory);
    }



}
