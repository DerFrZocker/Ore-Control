package de.derfrzocker.ore.control.gui;

import de.derfrzocker.ore.control.OreControl;
import de.derfrzocker.ore.control.Permissions;
import de.derfrzocker.ore.control.api.WorldOreConfig;
import de.derfrzocker.ore.control.utils.MessageUtil;
import de.derfrzocker.ore.control.utils.MessageValue;
import de.derfrzocker.ore.control.utils.OreControlUtil;
import lombok.NonNull;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permissible;

public class WorldConfigGui extends BasicGui {

    @NonNull
    private final WorldOreConfig worldOreConfig;

    WorldConfigGui(final WorldOreConfig worldOreConfig, final @NonNull Permissible permissible) {
        this.worldOreConfig = worldOreConfig;

        if (Permissions.SET_PERMISSION.hasPermission(permissible))
            addItem(getSettings().getOreItemStackSlot(), MessageUtil.replaceItemStack(getSettings().getOreItemStack()), event -> openSync(event.getWhoClicked(), new OreGui(worldOreConfig, null, event.getWhoClicked()).getInventory()));

        if (Permissions.SET_BIOME_PERMISSION.hasPermission(permissible))
            addItem(getSettings().getBiomeItemStackSlot(), MessageUtil.replaceItemStack(getSettings().getBiomeItemStack()), event -> openSync(event.getWhoClicked(), new BiomeGui(event.getWhoClicked(), worldOreConfig).getInventory()));

        if (Permissions.RESET_VALUES_PERMISSION.hasPermission(permissible))
            addItem(getSettings().getResetValueSlot(), MessageUtil.replaceItemStack(getSettings().getResetValueItemStack()), this::handleResetValues);

        if (Permissions.COPY_VALUES_PERMISSION.hasPermission(permissible))
            addItem(getSettings().getCopyValueSlot(), MessageUtil.replaceItemStack(getSettings().getCopyValueItemStack()), event -> openSync(event.getWhoClicked(), new WorldGui(worldOreConfig).getInventory()));

        if (Permissions.DELETE_TEMPLATE_PERMISSION.hasPermission(permissible) && worldOreConfig.isTemplate())
            addItem(getSettings().getTemplateDeleteSlot(), MessageUtil.replaceItemStack(getSettings().getTemplateDeleteItemStack()), this::handleDeleteTemplate); // TODO add right consumer

        addItem(getSettings().getBackSlot(), MessageUtil.replaceItemStack(getSettings().getBackItemStack()), event -> openSync(event.getWhoClicked(), new WorldGui(event.getWhoClicked()).getInventory()));
        addItem(getSettings().getInfoSlot(), MessageUtil.replaceItemStack(getSettings().getInfoItemStack(), getMessagesValues()));
    }

    @Override
    public WorldConfigGuiSettings getSettings() {
        return WorldConfigGuiSettings.getInstance();
    }

    private MessageValue[] getMessagesValues() {
        return new MessageValue[]{new MessageValue("world", worldOreConfig.getName())};
    }

    private void handleResetValues(final InventoryClickEvent event) {
        if (OreControl.getInstance().getConfigValues().verifyResetAction()) {
            openSync(event.getWhoClicked(), new VerifyGui(clickEvent -> {
                OreControlUtil.reset(this.worldOreConfig);
                OreControl.getService().saveWorldOreConfig(worldOreConfig);
                closeSync(event.getWhoClicked());
            }, clickEvent1 -> openSync(event.getWhoClicked(), getInventory())).getInventory());
            return;
        }

        OreControlUtil.reset(worldOreConfig);
        OreControl.getService().saveWorldOreConfig(worldOreConfig);
        closeSync(event.getWhoClicked());
    }

    private void handleDeleteTemplate(final InventoryClickEvent event) {
        openSync(event.getWhoClicked(), new VerifyGui(clickEvent -> {
            OreControl.getService().removeWorldOreConfig(worldOreConfig);
            closeSync(event.getWhoClicked());
        }, clickEvent1 -> openSync(event.getWhoClicked(), getInventory())).getInventory());
    }

    private static final class WorldConfigGuiSettings extends BasicSettings {
        private static WorldConfigGuiSettings instance = null;

        private static WorldConfigGuiSettings getInstance() {
            if (instance == null)
                instance = new WorldConfigGuiSettings();

            return instance;
        }

        private WorldConfigGuiSettings() {
            super(OreControl.getInstance(), "data/world_config_gui.yml");
        }

        private int getBiomeItemStackSlot() {
            return getYaml().getInt("biome.slot");
        }

        private ItemStack getBiomeItemStack() {
            return getYaml().getItemStack("biome.item_stack").clone();
        }

        private int getOreItemStackSlot() {
            return getYaml().getInt("ore.slot");
        }

        private ItemStack getOreItemStack() {
            return getYaml().getItemStack("ore.item_stack").clone();
        }

        private ItemStack getBackItemStack() {
            return getYaml().getItemStack("back.item_stack").clone();
        }

        private ItemStack getInfoItemStack() {
            return getYaml().getItemStack("info.item_stack").clone();
        }

        private int getInfoSlot() {
            return getYaml().getInt("info.slot");
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

        private int getTemplateDeleteSlot() {
            return getYaml().getInt("template.delete.slot");
        }

        private ItemStack getTemplateDeleteItemStack() {
            return getYaml().getItemStack("template.delete.item_stack").clone();
        }

    }
}
