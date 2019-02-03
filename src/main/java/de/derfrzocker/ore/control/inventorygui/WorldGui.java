package de.derfrzocker.ore.control.inventorygui;

import de.derfrzocker.ore.control.OreControl;
import de.derfrzocker.ore.control.inventorygui.utils.InventoryUtil;
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

import java.util.HashMap;
import java.util.Map;

public class WorldGui implements InventoryGui {


    private final Map<Integer, SubWorldGui> guis = new HashMap<>();

    private final int pages;

    private final int nextPage;

    private final int previousPage;

    public WorldGui() {
        this.nextPage = Settings.getInstance().getNextPageSlot();
        this.previousPage = Settings.getInstance().getPreviousPageSlot();

        String[] world = Bukkit.getWorlds().stream().map(World::getName).toArray(String[]::new);

        int slots = InventoryUtil.calculateSlots(Settings.getInstance().getRows(), Settings.getInstance().getWorldGap());

        pages = InventoryUtil.calculatePages(slots, world.length);

        for (int i = 0; i < pages; i++) {
            String[] worlds;

            if (i == pages - 1) {
                int rest = world.length - i * slots;
                worlds = new String[rest];
            } else
                worlds = new String[slots];

            System.arraycopy(world, i * slots, worlds, 0, worlds.length);

            guis.put(i, new SubWorldGui(worlds, i));
        }
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
            OreControl.getInstance().getReloadAbles().add(this);
        }

        private String getInventoryName() {
            return yaml.getString("inventory.name");
        }

        private int getRows() {
            return yaml.getInt("inventory.rows");
        }

        private int getWorldGap() {
            return yaml.getInt("inventory.world_gap");
        }

        private ItemStack getWorldItemStack() {
            return yaml.getItemStack("world_item_stack").clone();
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

        private SubWorldGui(String[] worlds, int page) {
            this.page = page;
            this.inventory = Bukkit.createInventory(this, Settings.getInstance().getRows() * 9,
                    MessageUtil.replacePlaceHolder(Settings.getInstance().getInventoryName(),
                            new MessageValue("page", String.valueOf(page)),
                            new MessageValue("pages", String.valueOf(pages))));

            if (page + 1 != pages)
                inventory.setItem(nextPage, Settings.getInstance().getNextPageItemStack());

            if (page != 0)
                inventory.setItem(previousPage, Settings.getInstance().getPreviousPageItemStack());

            for (int i = 0; i < worlds.length; i++) {
                int slot = InventoryUtil.calculateSlot(i, Settings.getInstance().getWorldGap());
                inventory.setItem(slot, MessageUtil.replaceItemStack(Settings.getInstance().getWorldItemStack(), new MessageValue("world", worlds[i])));
                values.put(slot, worlds[i]);
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

            if (world == null)
                throw new IllegalStateException("The world: " + values.get(event.getRawSlot()) + " cant't be null!");

            openSync(event.getWhoClicked(), new WorldConfigGui(OreControl.getService().getWorldOreConfig(world).orElseGet(() -> OreControl.getService().createWorldOreConfig(world)), event.getWhoClicked()).getInventory());
        }

        @Override
        public boolean contains(Inventory inventory) {
            return this.inventory.equals(inventory);
        }

    }
}
