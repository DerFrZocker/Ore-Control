package de.derfrzocker.ore.control.inventorygui;

import de.derfrzocker.ore.control.OreControl;
import de.derfrzocker.ore.control.Permissions;
import de.derfrzocker.ore.control.api.WorldOreConfig;
import de.derfrzocker.ore.control.utils.Config;
import de.derfrzocker.ore.control.utils.MessageUtil;
import de.derfrzocker.ore.control.utils.MessageValue;
import de.derfrzocker.ore.control.utils.ReloadAble;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permissible;

public class WorldConfigGui implements InventoryGui {

    @Getter
    private final Inventory inventory;

    private final int biome;

    private final int ores;

    private final int backSlot;

    private final World world;

    public WorldConfigGui(WorldOreConfig config, Permissible permissible) {
        this.world = Bukkit.getWorld(config.getWorld());
        this.backSlot = Settings.getInstance().getBackSlot();

        inventory = Bukkit.createInventory(this, Settings.getInstance().getSlots(), MessageUtil.replacePlaceHolder(Settings.getInstance().getInventoryName(), new MessageValue("world", this.world.getName())));

        if (Permissions.SET_PERMISSION.hasPermission(permissible)) {
            ores = Settings.getInstance().getOreItemStackSlot();
            inventory.setItem(ores, Settings.getInstance().getOreItemStack());
        } else
            ores = -245;

        if (Permissions.SET_BIOME_PERMISSION.hasPermission(permissible)) {
            biome = Settings.getInstance().getBiomeItemStackSlot();
            inventory.setItem(biome, Settings.getInstance().getBiomeItemStack());
        } else
            biome = -245;

        inventory.setItem(backSlot, Settings.getInstance().getBackItemStack());
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getRawSlot() == backSlot) {
            openSync(event.getWhoClicked(), new WorldGui().getInventory());
            return;
        }

        WorldOreConfig config = OreControl.getService().getWorldOreConfig(world).get();

        if (event.getRawSlot() == ores) {
            openSync(event.getWhoClicked(), new OreGui(config, null).getInventory());
            return;
        }

        if (event.getRawSlot() == biome) {
            openSync(event.getWhoClicked(), new BiomeGui(config).getInventory());
            return;
        }
    }

    @Override
    public boolean contains(Inventory inventory) {
        return this.inventory.equals(inventory);
    }

    private static final class Settings implements ReloadAble {

        private final static String file = "data/world_config_gui.yml";

        private YamlConfiguration yaml;

        private static Settings instance = null;

        private static Settings getInstance() {
            if (instance == null)
                instance = new Settings();

            return instance;
        }

        private Settings() {
            yaml = Config.getConfig(OreControl.getInstance(), file);
            OreControl.getInstance().getReloadAbles().add(this);
        }

        private String getInventoryName() {
            return yaml.getString("inventory.name");
        }

        private int getSlots() {
            return yaml.getInt("inventory.rows") * 9;
        }

        private int getBiomeItemStackSlot() {
            return yaml.getInt("biome.slot");
        }

        private ItemStack getBiomeItemStack() {
            return yaml.getItemStack("biome.item_stack").clone();
        }

        private int getOreItemStackSlot() {
            return yaml.getInt("ore.slot");
        }

        private ItemStack getOreItemStack() {
            return yaml.getItemStack("ore.item_stack").clone();
        }

        private ItemStack getBackItemStack() {
            return yaml.getItemStack("back.item_stack").clone();
        }

        private int getBackSlot() {
            return yaml.getInt("back.slot");
        }

        @Override
        public void reload() {
            yaml = Config.getConfig(OreControl.getInstance(), file);
        }
    }

}
