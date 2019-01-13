package de.derfrzocker.ore.control.inventorygui;

import de.derfrzocker.ore.control.OreControl;
import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.Ore;
import de.derfrzocker.ore.control.api.Setting;
import de.derfrzocker.ore.control.api.WorldOreConfig;
import de.derfrzocker.ore.control.utils.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static de.derfrzocker.ore.control.OreControlMessages.SET_NOT_SAVE;
import static de.derfrzocker.ore.control.OreControlMessages.SET_NOT_SAVE_WARNING;

public class SettingsGui implements InventoryGui {

    @Getter
    @NonNull
    private final Inventory inventory;

    @NonNull
    private final World world;

    @NonNull
    private final Setting setting;

    @NonNull
    private final Ore ore;

    private final Map<Integer, Integer> values = new HashMap<>();

    private final int oreSlot;

    private final Biome biome;

    public SettingsGui(WorldOreConfig config, Ore ore, Setting setting) {
        this(config, ore, setting, null);
    }

    public SettingsGui(WorldOreConfig config, Ore ore, Setting setting, Biome biome) {
        this.ore = ore;
        this.setting = setting;
        this.world = Bukkit.getWorld(config.getWorld());
        this.biome = biome;
        this.inventory = Bukkit.createInventory(null, Settings.getInstance().getSlots(), Settings.getInstance().getInventoryName());

        Settings.getInstance().getItemStackValues().forEach(value -> {
            inventory.setItem(value.getSlot(), MessageUtil.replaceItemStack(value.getItemStack()));
            values.put(value.getSlot(), value.getValue());
        });

        oreSlot = Settings.getInstance().getOreSlot();

        //TODO set ore itemstack
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        if (!values.containsKey(event.getRawSlot()))
            return;

        WorldOreConfig config = OreControl.getService().getWorldOreConfig(world).get();

        int value = values.get(event.getRawSlot());

        int current;

        if (biome == null)
            current = OreControlUtil.getAmount(ore, setting, config);
        else
            current = OreControlUtil.getAmount(ore, setting, config, biome);

        int newValue = current + value;

        if (!OreControlUtil.isSave(ore, setting, newValue)) {
            if (OreControl.getInstance().getConfigValues().isSaveMode()) {
                SET_NOT_SAVE.sendMessage(event.getWhoClicked(), new MessageValue("value", String.valueOf(newValue)));
                return;
            }
            SET_NOT_SAVE_WARNING.sendMessage(event.getWhoClicked(), new MessageValue("value", String.valueOf(newValue)));
        }

        if (biome == null)
            OreControlUtil.setAmount(ore, setting, config, newValue);
        else
            OreControlUtil.setAmount(ore, setting, config, newValue, biome);

        OreControl.getService().saveWorldOreConfig(config);

        //TODO update ore itemstack
    }

    @Override
    public boolean contains(Inventory inventory) {
        return this.inventory.equals(inventory);
    }

    private static final class Settings implements ReloadAble {

        private final static String file = "data/settings_gui.yml";

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
            return MessageUtil.replacePlaceHolder(yaml.getString("inventory.name", "Settings Inventory")); //TODO add PlaceHolders
        }

        private int getSlots() {
            return yaml.getInt("inventory.rows", 1) * 9;
        }

        private int getOreSlot() {
            return yaml.getInt("inventory.ore.slot");
        }

        private Set<ItemStackValues> getItemStackValues() {
            Set<ItemStackValues> set = new HashSet<>();
            yaml.getConfigurationSection("inventory.items").
                    getKeys(false).stream().
                    map(value -> yaml.getConfigurationSection("inventory.items." + value)).
                    map(value -> new ItemStackValues(value.getInt("slot", 0), value.getInt("value", 0), value.getItemStack("item_stack"))).
                    forEach(set::add);
            return set;
        }


        @Override
        public void reload() {
            yaml = Config.getConfig(OreControl.getInstance(), file);
        }

        @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
        @Getter(value = AccessLevel.PRIVATE)
        private final class ItemStackValues {
            private final int slot;
            private final int value;
            private final ItemStack itemStack;
        }

    }

}
