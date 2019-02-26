package de.derfrzocker.ore.control.gui;

import de.derfrzocker.ore.control.OreControl;
import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.Ore;
import de.derfrzocker.ore.control.api.WorldOreConfig;
import de.derfrzocker.ore.control.gui.utils.InventoryUtil;
import de.derfrzocker.ore.control.utils.Config;
import de.derfrzocker.ore.control.utils.MessageUtil;
import de.derfrzocker.ore.control.utils.MessageValue;
import de.derfrzocker.ore.control.utils.ReloadAble;
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

public class OreGui implements InventoryGui {

    @Getter
    private final Inventory inventory;

    private final WorldOreConfig config;

    private final Map<Integer, Ore> values = new HashMap<>();

    private final Biome biome;

    private final int backSlot;

    OreGui(WorldOreConfig config, Biome biome) {
        this.config = config;
        this.biome = biome;
        this.inventory = Bukkit.createInventory(this, Settings.getInstance().getSlots(), MessageUtil.replacePlaceHolder(biome == null ? Settings.getInstance().getInventoryName() : Settings.getInstance().getBiomeInventoryName(), getMessagesValues()));

        this.backSlot = Settings.getInstance().getBackSlot();

        inventory.setItem(backSlot, MessageUtil.replaceItemStack(Settings.getInstance().getBackItemStack()));
        inventory.setItem(Settings.getInstance().getInfoSlot(), MessageUtil.replaceItemStack(biome == null ? Settings.getInstance().getInfoItemStack() : Settings.getInstance().getInfoBiomeItemStack(), getMessagesValues()));

        Ore[] ores = biome == null ? Ore.values() : biome.getOres();

        for (int i = 0; i < ores.length; i++) {
            int slot = InventoryUtil.calculateSlot(i, Settings.getInstance().getOreGap());
            inventory.setItem(slot, getOreItemStack(config, ores[i]));
            values.put(slot, ores[i]);
        }
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getRawSlot() == backSlot) {
            openSync(event.getWhoClicked(), biome == null ? new WorldConfigGui(config, event.getWhoClicked()).getInventory() : new BiomeGui(config).getInventory());
            return;
        }

        if (!values.containsKey(event.getRawSlot()))
            return;

        Ore ore = values.get(event.getRawSlot());

        openSync(event.getWhoClicked(), new OreSettingsGui(config, ore, biome).getInventory());
    }

    @Override
    public boolean contains(Inventory inventory) {
        return this.inventory.equals(inventory);
    }

    private MessageValue[] getMessagesValues() {
        return new MessageValue[]{new MessageValue("world", config.getWorld()),
                new MessageValue("biome", biome == null ? "" : biome.toString())};
    }

    private ItemStack getOreItemStack(WorldOreConfig config, Ore ore) {
        ItemStack itemStack = Settings.getInstance().getDefaultOreItemStack();

        itemStack.setType(ore.getMaterial());

        itemStack = MessageUtil.replaceItemStack(itemStack, new MessageValue("ore", ore.toString()));

        return itemStack;
    }

    private static final class Settings implements ReloadAble {

        private final static String file = "data/ore_gui.yml";

        private YamlConfiguration yaml;

        private static Settings instance = null;

        private static Settings getInstance() {
            if (instance == null)
                instance = new Settings();

            return instance;
        }

        private Settings() {
            yaml = Config.getConfig(OreControl.getInstance(), file);
            RELOAD_ABLES.add(this);
        }

        private String getInventoryName() {
            return yaml.getString("inventory.name");
        }

        private String getBiomeInventoryName() {
            return yaml.getString("inventory.biome_name");
        }

        private int getSlots() {
            return yaml.getInt("inventory.rows") * 9;
        }

        private int getOreGap() {
            return yaml.getInt("inventory.ore_gap");
        }

        private ItemStack getInfoItemStack() {
            return yaml.getItemStack("info.item_stack").clone();
        }

        private ItemStack getInfoBiomeItemStack() {
            return yaml.getItemStack("info.biome_item_stack").clone();
        }

        private int getInfoSlot() {
            return yaml.getInt("info.slot");
        }

        private ItemStack getDefaultOreItemStack() {
            return yaml.getItemStack("default_ore_item_stack").clone();
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
