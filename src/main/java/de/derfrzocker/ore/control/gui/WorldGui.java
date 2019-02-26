package de.derfrzocker.ore.control.gui;

import de.derfrzocker.ore.control.OreControl;
import de.derfrzocker.ore.control.api.WorldOreConfig;
import de.derfrzocker.ore.control.gui.utils.InventoryUtil;
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

import java.util.*;

public class WorldGui implements InventoryGui {


    private final Map<Integer, SubWorldGui> guis = new HashMap<>();

    private final int pages;

    private final int nextPage;

    private final int previousPage;

    private Map<String, WorldOreConfig> worldOreConfigs = new HashMap<>();

    public WorldGui() {
        this.nextPage = Settings.getInstance().getNextPageSlot();
        this.previousPage = Settings.getInstance().getPreviousPageSlot();

        final Set<String> configsSet = new LinkedHashSet<>();

        Bukkit.getWorlds().stream().map(World::getName).forEach(configsSet::add);
        OreControl.getService().getAllWorldOreConfigs().forEach(value -> worldOreConfigs.put(value.getWorld(), value));

        worldOreConfigs.values().stream().filter(value -> !value.isTemplate()).map(WorldOreConfig::getWorld).forEach(configsSet::add);
        configsSet.addAll(worldOreConfigs.keySet());

        final String[] configs = configsSet.toArray(new String[0]);

        int slots = InventoryUtil.calculateSlots(Settings.getInstance().getRows(), Settings.getInstance().getConfigGap());

        pages = InventoryUtil.calculatePages(slots, configs.length);

        for (int i = 0; i < pages; i++) {
            String[] worlds;

            if (i == pages - 1) {
                int rest = configs.length - i * slots;
                worlds = new String[rest];
            } else
                worlds = new String[slots];

            System.arraycopy(configs, i * slots, worlds, 0, worlds.length);

            guis.put(i, new SubWorldGui(worlds, i));
        }

        worldOreConfigs = null;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(Inventory inventory) {
        return this.guis.values().stream().anyMatch(value -> value.contains(inventory));
    }

    @Override
    public Inventory getInventory() {
        return guis.get(0).getInventory();
    }

    private static final class Settings implements ReloadAble {

        private final static String file = "data/world_gui.yml";

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

        private int getRows() {
            return yaml.getInt("inventory.rows");
        }

        private int getConfigGap() {
            return yaml.getInt("inventory.config_gap");
        }

        private ItemStack getWorldItemStack() {
            return yaml.getItemStack("world_item_stack").clone();
        }

        private ItemStack getTemplateItemStack() {
            return yaml.getItemStack("template_item_stack").clone();
        }

        private int getNextPageSlot() {
            return yaml.getInt("next_page.slot");
        }

        private ItemStack getNextPageItemStack() {
            return yaml.getItemStack("next_page.item_stack").clone();
        }

        private int getPreviousPageSlot() {
            return yaml.getInt("previous_page.slot");
        }

        private ItemStack getPreviousPageItemStack() {
            return yaml.getItemStack("previous_page.item_stack").clone();
        }


        @Override
        public void reload() {
            yaml = Config.getConfig(OreControl.getInstance(), file);
        }
    }

    private final class SubWorldGui implements InventoryGui {

        @Getter
        private final Inventory inventory;

        private final int page;

        private final Map<Integer, String> values = new HashMap<>();

        private SubWorldGui(String[] configs, int page) {
            this.page = page;

            MessageValue[] messageValues = new MessageValue[]{new MessageValue("page", String.valueOf(page)), new MessageValue("pages", String.valueOf(pages))};

            this.inventory = Bukkit.createInventory(this, Settings.getInstance().getRows() * 9,
                    MessageUtil.replacePlaceHolder(Settings.getInstance().getInventoryName(), messageValues));

            if (page + 1 != pages)
                inventory.setItem(nextPage, MessageUtil.replaceItemStack(Settings.getInstance().getNextPageItemStack(), messageValues));

            if (page != 0)
                inventory.setItem(previousPage, MessageUtil.replaceItemStack(Settings.getInstance().getPreviousPageItemStack(), messageValues));

            for (int i = 0; i < configs.length; i++) {
                final String configName = configs[i];
                int slot = InventoryUtil.calculateSlot(i, Settings.getInstance().getConfigGap());

                if (worldOreConfigs.containsKey(configName) && worldOreConfigs.get(configName).isTemplate())
                    inventory.setItem(slot, MessageUtil.replaceItemStack(Settings.getInstance().getTemplateItemStack(), new MessageValue("template", configName)));
                else
                    inventory.setItem(slot, MessageUtil.replaceItemStack(Settings.getInstance().getWorldItemStack(), new MessageValue("world", configName)));

                values.put(slot, configName);
            }
        }


        @Override
        public void onInventoryClick(InventoryClickEvent event) {
            if (event.getRawSlot() == previousPage && page != 0) {
                openSync(event.getWhoClicked(), guis.get(page - 1).getInventory());
                return;
            }

            if (event.getRawSlot() == nextPage && page + 1 != pages) {
                openSync(event.getWhoClicked(), guis.get(page + 1).getInventory());
                return;
            }

            if (!values.containsKey(event.getRawSlot()))
                return;

            World world = Bukkit.getWorld(values.get(event.getRawSlot()));

            // TODO

            if (world == null)
                throw new IllegalStateException("The world: " + values.get(event.getRawSlot()) + " cant't be null!");

            openSync(event.getWhoClicked(), new WorldConfigGui(OreControl.getService().getWorldOreConfig(values.get(event.getRawSlot())).orElseGet(() -> OreControl.getService().createWorldOreConfig(values.get(event.getRawSlot()))), event.getWhoClicked()).getInventory());
        }

        @Override
        public boolean contains(Inventory inventory) {
            return this.inventory.equals(inventory);
        }

    }
}
