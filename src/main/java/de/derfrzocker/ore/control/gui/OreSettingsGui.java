package de.derfrzocker.ore.control.gui;

import de.derfrzocker.ore.control.OreControl;
import de.derfrzocker.ore.control.Permissions;
import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.Ore;
import de.derfrzocker.ore.control.api.Setting;
import de.derfrzocker.ore.control.api.WorldOreConfig;
import de.derfrzocker.ore.control.gui.copy.CopyAction;
import de.derfrzocker.ore.control.gui.copy.CopyOreAction;
import de.derfrzocker.ore.control.utils.MessageUtil;
import de.derfrzocker.ore.control.utils.MessageValue;
import de.derfrzocker.ore.control.utils.OreControlUtil;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permissible;

import java.util.function.Consumer;

public class OreSettingsGui extends BasicGui {

    @NonNull
    private final WorldOreConfig worldOreConfig;

    @NonNull
    private final Ore ore;

    private final Biome biome;

    private final int statusSlot;

    private final CopyAction copyAction;

    private boolean activated;

    OreSettingsGui(final WorldOreConfig worldOreConfig, final Ore ore, final Biome biome, final Permissible permissible) {
        this.worldOreConfig = worldOreConfig;
        this.ore = ore;
        this.biome = biome;
        this.statusSlot = getSettings().getStatusSlot();
        copyAction = null;

        final Setting[] settings = ore.getSettings();

        for (int i = 0; i < settings.length; i++)
            addItem(i + getSettings().getSettingStartSlot(), getSettingItemStack(settings[i]), new SettingConsumer(settings[i]));

        addItem(getSettings().getBackSlot(), MessageUtil.replaceItemStack(getSettings().getBackItemStack()),
                event -> openSync(event.getWhoClicked(), new OreGui(worldOreConfig, biome, event.getWhoClicked()).getInventory()));

        addItem(getSettings().getInfoSlot(), MessageUtil.replaceItemStack(biome == null ? getSettings().getInfoItemStack() : getSettings().getInfoBiomeItemStack(), getMessagesValues()));

        addItem(statusSlot, MessageUtil.replaceItemStack(activated ? getSettings().getDeactivateItemStack() : getSettings().getActivateItemStack()), event -> handleStatusUpdate());

        if (Permissions.RESET_VALUES_PERMISSION.hasPermission(permissible))
            addItem(getSettings().getResetValueSlot(), MessageUtil.replaceItemStack(getSettings().getResetValueItemStack()), this::handleResetValues);

        if (Permissions.COPY_VALUES_PERMISSION.hasPermission(permissible))
            addItem(getSettings().getCopyValueSlot(), MessageUtil.replaceItemStack(getSettings().getCopyValueItemStack()), event -> openSync(event.getWhoClicked(), new WorldGui(new CopyOreAction(worldOreConfig, ore, biome)).getInventory()));
    }

    public OreSettingsGui(final WorldOreConfig worldOreConfig, final Ore ore, final Biome biome, final @NonNull CopyAction copyAction) {
        this.worldOreConfig = worldOreConfig;
        this.ore = ore;
        this.biome = biome;
        this.statusSlot = -1;
        this.copyAction = copyAction;

        final Setting[] settings = ore.getSettings();

        for (int i = 0; i < settings.length; i++)
            addItem(i + getSettings().getSettingStartSlot(), getSettingItemStack(settings[i]), new SettingCopyConsumer(settings[i]));

        addItem(getSettings().getInfoSlot(), MessageUtil.replaceItemStack(biome == null ? getSettings().getInfoItemStack() : getSettings().getInfoBiomeItemStack(), getMessagesValues()));
    }

    @Override
    public OreSettingsGuiSettings getSettings() {
        return OreSettingsGuiSettings.getInstance();
    }

    private ItemStack getSettingItemStack(final Setting setting) {
        final ItemStack itemStack;

        if (biome == null)
            itemStack = MessageUtil.replaceItemStack(getSettings().getSettingsItemStack(setting), new MessageValue("amount", String.valueOf(OreControlUtil.getAmount(ore, setting, worldOreConfig))));
        else
            itemStack = MessageUtil.replaceItemStack(getSettings().getSettingsItemStack(setting), new MessageValue("amount", String.valueOf(OreControlUtil.getAmount(ore, setting, worldOreConfig, biome))));

        return itemStack;
    }

    private MessageValue[] getMessagesValues() {
        return new MessageValue[]{new MessageValue("world", worldOreConfig.getName()),
                new MessageValue("biome", biome == null ? "" : biome.toString()),
                new MessageValue("ore", ore.toString())};
    }

    private void handleStatusUpdate() {
        activated = !activated;

        if (biome == null)
            OreControlUtil.setActivated(ore, worldOreConfig, activated);
        else
            OreControlUtil.setActivated(ore, worldOreConfig, activated, biome);

        getInventory().setItem(statusSlot, MessageUtil.replaceItemStack(activated ? getSettings().getDeactivateItemStack() : getSettings().getActivateItemStack()));

        OreControl.getService().saveWorldOreConfig(worldOreConfig);
    }

    private void handleResetValues(final InventoryClickEvent event) {
        if (OreControl.getInstance().getConfigValues().verifyResetAction()) {
            openSync(event.getWhoClicked(), new VerifyGui(clickEvent -> {
                if (biome != null)
                    OreControlUtil.reset(worldOreConfig, ore, biome);
                else
                    OreControlUtil.reset(worldOreConfig, ore);

                OreControl.getService().saveWorldOreConfig(worldOreConfig);
                closeSync(event.getWhoClicked());
            }, clickEvent1 -> openSync(event.getWhoClicked(), getInventory())).getInventory());
            return;
        }
        if (biome != null)
            OreControlUtil.reset(worldOreConfig, ore, biome);
        else
            OreControlUtil.reset(worldOreConfig, ore);

        OreControl.getService().saveWorldOreConfig(worldOreConfig);
        closeSync(event.getWhoClicked());
    }

    private static final class OreSettingsGuiSettings extends BasicSettings {

        private static OreSettingsGuiSettings instance = null;

        private static OreSettingsGuiSettings getInstance() {
            if (instance == null)
                instance = new OreSettingsGuiSettings();

            return instance;
        }

        private OreSettingsGuiSettings() {
            super(OreControl.getInstance(), "data/ore_settings_gui.yml");
        }

        private int getSettingStartSlot() {
            return getYaml().getInt("inventory.setting_start_slot", 1);
        }

        private ItemStack getSettingsItemStack(Setting setting) {
            return getYaml().getItemStack("settings_item_stack." + setting.toString()).clone();
        }

        private ItemStack getInfoItemStack() {
            return getYaml().getItemStack("info.item_stack").clone();
        }

        private ItemStack getInfoBiomeItemStack() {
            return getYaml().getItemStack("info.biome_item_stack").clone();
        }

        private int getInfoSlot() {
            return getYaml().getInt("info.slot");
        }

        private ItemStack getBackItemStack() {
            return getYaml().getItemStack("back.item_stack").clone();
        }

        private int getBackSlot() {
            return getYaml().getInt("back.slot", 0);
        }

        private int getStatusSlot() {
            return getYaml().getInt("status.slot", 8);
        }

        private ItemStack getActivateItemStack() {
            return getYaml().getItemStack("status.activate").clone();
        }

        private ItemStack getDeactivateItemStack() {
            return getYaml().getItemStack("status.deactivate").clone();
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

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private final class SettingConsumer implements Consumer<InventoryClickEvent> {

        private final Setting setting;

        @Override
        public void accept(final InventoryClickEvent event) {
            openSync(event.getWhoClicked(), new SettingsGui(worldOreConfig, ore, setting, biome, event.getWhoClicked()).getInventory());
        }
    }

    @SuppressWarnings("ConstantConditions")
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private final class SettingCopyConsumer implements Consumer<InventoryClickEvent> {

        private final Setting setting;

        @Override
        public void accept(final InventoryClickEvent event) {
            copyAction.setSettingTarget(setting);

            copyAction.next(event.getWhoClicked(), OreSettingsGui.this);
        }
    }

}
