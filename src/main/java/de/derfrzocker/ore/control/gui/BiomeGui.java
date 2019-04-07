package de.derfrzocker.ore.control.gui;

import de.derfrzocker.ore.control.OreControl;
import de.derfrzocker.ore.control.Permissions;
import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.WorldOreConfig;
import de.derfrzocker.ore.control.gui.copy.CopyBiomesAction;
import de.derfrzocker.ore.control.utils.MessageUtil;
import de.derfrzocker.ore.control.utils.MessageValue;
import de.derfrzocker.ore.control.utils.OreControlUtil;
import lombok.NonNull;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permissible;

class BiomeGui extends PageGui<Biome> {

    @NonNull
    private final WorldOreConfig worldOreConfig;

    BiomeGui(final Permissible permissible, final WorldOreConfig worldOreConfig) {
        this.worldOreConfig = worldOreConfig;

        init(Biome.values(), Biome[]::new, BiomeGuiSettings.getInstance(), this::getItemStack, this::handleNormalClick);

        addItem(BiomeGuiSettings.getInstance().getInfoSlot(), MessageUtil.replaceItemStack(BiomeGuiSettings.getInstance().getInfoItemStack(), getMessagesValues()));
        addItem(BiomeGuiSettings.getInstance().getBackSlot(), MessageUtil.replaceItemStack(BiomeGuiSettings.getInstance().getBackItemStack()), event -> openSync(event.getWhoClicked(), new WorldConfigGui(worldOreConfig, event.getWhoClicked()).getInventory()));

        if (Permissions.RESET_VALUES_PERMISSION.hasPermission(permissible))
            addItem(BiomeGuiSettings.getInstance().getResetValueSlot(), MessageUtil.replaceItemStack(BiomeGuiSettings.getInstance().getResetValueItemStack()), this::handleResetValues);

        if (Permissions.COPY_VALUES_PERMISSION.hasPermission(permissible))
            addItem(BiomeGuiSettings.getInstance().getCopyValueSlot(), MessageUtil.replaceItemStack(BiomeGuiSettings.getInstance().getCopyValueItemStack()), event -> openSync(event.getWhoClicked(), new WorldGui(new CopyBiomesAction(worldOreConfig, Biome.values())).getInventory()));
    }

    private ItemStack getItemStack(final Biome biome) {
        return MessageUtil.replaceItemStack(BiomeGuiSettings.getInstance().getBiomeItemStack(biome));
    }

    private void handleNormalClick(final Biome biome, final InventoryClickEvent event) {
        openSync(event.getWhoClicked(), new OreGui(worldOreConfig, biome, event.getWhoClicked()).getInventory());
    }

    private void handleResetValues(final InventoryClickEvent event) {
        if (OreControl.getInstance().getConfigValues().verifyResetAction()) {
            openSync(event.getWhoClicked(), new VerifyGui(clickEvent -> {
                for (Biome biome : Biome.values())
                    OreControlUtil.reset(this.worldOreConfig, biome);

                OreControl.getService().saveWorldOreConfig(worldOreConfig);
                closeSync(event.getWhoClicked());
            }, clickEvent1 -> openSync(event.getWhoClicked(), getInventory())).getInventory());
            return;
        }

        for (Biome biome : Biome.values())
            OreControlUtil.reset(this.worldOreConfig, biome);

        OreControl.getService().saveWorldOreConfig(worldOreConfig);
        closeSync(event.getWhoClicked());
    }

    private MessageValue[] getMessagesValues() {
        return new MessageValue[]{new MessageValue("world", worldOreConfig.getName())};
    }

    private static final class BiomeGuiSettings extends PageSettings {
        private static BiomeGuiSettings instance = null;

        private static BiomeGuiSettings getInstance() {
            if (instance == null)
                instance = new BiomeGuiSettings();

            return instance;
        }

        private BiomeGuiSettings() {
            super(OreControl.getInstance(), "data/biome_gui.yml");
        }

        private ItemStack getBiomeItemStack(final Biome biome) {
            return getYaml().getItemStack("biomes." + biome.toString()).clone();
        }

        private ItemStack getInfoItemStack() {
            return getYaml().getItemStack("info.item_stack").clone();
        }

        private int getInfoSlot() {
            return getYaml().getInt("info.slot");
        }

        private ItemStack getBackItemStack() {
            return getYaml().getItemStack("back.item_stack").clone();
        }

        private int getBackSlot() {
            return getYaml().getInt("back.slot");
        }

        private int getResetValueSlot() {
            return getYaml().getInt("value.reset.slot");
        }

        private ItemStack getResetValueItemStack() {
            return getYaml().getItemStack("value.reset.item_stack").clone();
        }

        private int getCopyValueSlot() {
            return getYaml().getInt("value.copy.slot");
        }

        private ItemStack getCopyValueItemStack() {
            return getYaml().getItemStack("value.copy.item_stack").clone();
        }
    }

}
