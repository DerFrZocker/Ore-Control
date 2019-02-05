package de.derfrzocker.ore.control.inventorygui;

import de.derfrzocker.ore.control.OreControl;
import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.Ore;
import de.derfrzocker.ore.control.api.Setting;
import de.derfrzocker.ore.control.api.WorldOreConfig;
import de.derfrzocker.ore.control.utils.*;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class OreSettingsGui implements InventoryGui {

    @Getter
    @NonNull
    private final Inventory inventory;

    @NonNull
    private final World world;

    @NonNull
    private final Ore ore;

    private final Map<Integer, Setting> values = new HashMap<>();

    private final Biome biome;

    private final int backSlot;

    private final int statusSlot;

    private boolean activated;

    OreSettingsGui(WorldOreConfig config, Ore ore, Biome biome) {
        this.ore = ore;
        this.world = Bukkit.getWorld(config.getWorld());
        this.biome = biome;
        this.inventory = Bukkit.createInventory(this, Settings.getInstance().getSlots(), MessageUtil.replacePlaceHolder(biome == null ? Settings.getInstance().getInventoryName() : Settings.getInstance().getBiomeInventoryName(),
                new MessageValue("world", world.getName()),
                new MessageValue("biome", biome == null ? "" : biome.toString().toLowerCase()),
                new MessageValue("ore", ore.toString().toLowerCase())));

        this.backSlot = Settings.getInstance().getBackSlot();
        this.statusSlot = Settings.getInstance().getStatusSlot();

        activated = biome == null ? OreControlUtil.isActivated(ore, config) : OreControlUtil.isActivated(ore, config, biome);

        inventory.setItem(statusSlot, activated ? Settings.getInstance().getDeactivateItemStack() : Settings.getInstance().getActivateItemStack());
        inventory.setItem(backSlot, Settings.getInstance().getBackItemStack());

        Setting[] settings = ore.getSettings();

        for (int i = 0; i < settings.length; i++) {
            inventory.setItem(i + Settings.getInstance().getSettingStartSlot(), getSettingItemStack(config, settings[i]));
            values.put(i + Settings.getInstance().getSettingStartSlot(), settings[i]);
        }
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        WorldOreConfig config = OreControl.getService().getWorldOreConfig(world).get();

        if (event.getRawSlot() == backSlot) {
            openSync(event.getWhoClicked(), new OreGui(config, biome).getInventory());
            return;
        }

        if (event.getRawSlot() == statusSlot) {
            activated = !activated;

            if (biome == null)
                OreControlUtil.setActivated(ore, config, activated);
            else
                OreControlUtil.setActivated(ore, config, activated, biome);

            inventory.setItem(statusSlot, activated ? Settings.getInstance().getDeactivateItemStack() : Settings.getInstance().getActivateItemStack());

            OreControl.getService().saveWorldOreConfig(config);
            return;
        }

        if (!values.containsKey(event.getRawSlot()))
            return;

        Setting setting = values.get(event.getRawSlot());

        openSync(event.getWhoClicked(), new SettingsGui(config, ore, setting, biome).getInventory());
    }

    @Override
    public boolean contains(Inventory inventory) {
        return this.inventory.equals(inventory);
    }

    private ItemStack getSettingItemStack(WorldOreConfig config, Setting setting) {
        ItemStack itemStack = Settings.getInstance().getSettingsItemStack(setting);

        if (biome == null)
            itemStack = MessageUtil.replaceItemStack(itemStack, new MessageValue("amount", String.valueOf(OreControlUtil.getAmount(ore, setting, config))));
        else
            itemStack = MessageUtil.replaceItemStack(itemStack, new MessageValue("amount", String.valueOf(OreControlUtil.getAmount(ore, setting, config, biome))));

        return itemStack;
    }

    private static final class Settings implements ReloadAble {

        private final static String file = "data/ore_settings_gui.yml";

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

        private String getBiomeInventoryName() {
            return yaml.getString("inventory.biome_name");
        }

        private int getSlots() {
            return yaml.getInt("inventory.rows", 1) * 9;
        }

        private int getSettingStartSlot() {
            return yaml.getInt("inventory.setting_start_slot", 1);
        }

        private ItemStack getSettingsItemStack(Setting setting) {
            return yaml.getItemStack("settings_item_stack." + setting.toString()).clone();
        }

        private ItemStack getBackItemStack() {
            return yaml.getItemStack("back.item_stack").clone();
        }

        private int getBackSlot() {
            return yaml.getInt("back.slot", 0);
        }

        private int getStatusSlot() {
            return yaml.getInt("status.slot", 8);
        }

        private ItemStack getActivateItemStack() {
            return yaml.getItemStack("status.activate").clone();
        }

        private ItemStack getDeactivateItemStack() {
            return yaml.getItemStack("status.deactivate").clone();
        }

        @Override
        public void reload() {
            yaml = Config.getConfig(OreControl.getInstance(), file);
        }
    }
}
