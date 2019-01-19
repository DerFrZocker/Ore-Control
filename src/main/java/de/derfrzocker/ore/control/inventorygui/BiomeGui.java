package de.derfrzocker.ore.control.inventorygui;

import com.google.common.base.Preconditions;
import de.derfrzocker.ore.control.OreControl;
import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.WorldOreConfig;
import de.derfrzocker.ore.control.inventorygui.utils.InventoryUtil;
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

public class BiomeGui implements InventoryGui {

    @NonNull
    private final World world;

    private final Map<Integer, SubBiomeGui> guis = new HashMap<>();

    private final int pages;

    private final int backSlot;

    private final int nextPage;

    private final int previousPage;

    public BiomeGui(WorldOreConfig config) {
        this.world = Bukkit.getWorld(config.getWorld());
        this.backSlot = Settings.getInstance().getBackSlot();
        this.nextPage = Settings.getInstance().getNextPageSlot();
        this.previousPage = Settings.getInstance().getPreviousPageSlot();

        Biome[] biome = Biome.values();

        int slots = InventoryUtil.calculateSlots(Settings.getInstance().getRows(), Settings.getInstance().getBiomeGap());

        pages = InventoryUtil.calculatePages(slots, biome.length);

        for (int i = 0; i < pages; i++) {
            Biome[] biomes;

            if (i == pages - 1) {
                int rest = biome.length - i * slots;
                biomes = new Biome[rest];
            } else
                biomes = new Biome[slots];

            System.arraycopy(biome, i * slots, biomes, 0, biomes.length);

            guis.put(i, new SubBiomeGui(biomes, i));
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

        private final static String file = "data/biome_gui.yml";

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

        private int getBiomeGap() {
            return yaml.getInt("inventory.biome_gap");
        }

        private ItemStack getBiomeItemStack(Biome biome) {
            return yaml.getItemStack("biomes." + biome.toString()).clone();
        }

        private ItemStack getBackItemStack() {
            return yaml.getItemStack("back.item_stack").clone();
        }

        private int getBackSlot() {
            return yaml.getInt("back.slot");
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

    private final class SubBiomeGui implements InventoryGui {

        @Getter
        private final Inventory inventory;

        private int page;

        private final Map<Integer, Biome> values = new HashMap<>();

        private SubBiomeGui(Biome[] biomes, int page) {
            this.page = page;
            this.inventory = Bukkit.createInventory(this, Settings.getInstance().getRows() * 9,
                    MessageUtil.replacePlaceHolder(Settings.getInstance().getInventoryName(),
                            new MessageValue("page", String.valueOf(page)),
                            new MessageValue("pages", String.valueOf(pages)),
                            new MessageValue("world", world.getName())));

            inventory.setItem(backSlot, Settings.getInstance().getBackItemStack());
            if (page + 1 != pages)
                inventory.setItem(nextPage, Settings.getInstance().getNextPageItemStack());

            if (page != 0)
                inventory.setItem(previousPage, Settings.getInstance().getPreviousPageItemStack());

            for (int i = 0; i < biomes.length; i++) {
                int slot = InventoryUtil.calculateSlot(i, Settings.getInstance().getBiomeGap());
                inventory.setItem(slot, Settings.getInstance().getBiomeItemStack(biomes[i]));
                values.put(slot, biomes[i]);
            }
        }


        @Override
        public void onInventoryClick(InventoryClickEvent event) {
            if (event.getRawSlot() == backSlot) {
                openSync(event.getWhoClicked(), new WorldConfigGui(OreControl.getService().getWorldOreConfig(world).get(), event.getWhoClicked()).getInventory());
                return;
            }

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

            Biome biome = values.get(event.getRawSlot());

            Preconditions.checkNotNull(biome);

            openSync(event.getWhoClicked(), new OreGui(OreControl.getService().getWorldOreConfig(world).get(), biome).getInventory());
        }

        @Override
        public boolean contains(Inventory inventory) {
            return this.inventory.equals(inventory);
        }

    }

}
