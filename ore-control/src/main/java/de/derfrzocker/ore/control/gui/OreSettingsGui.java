package de.derfrzocker.ore.control.gui;

import de.derfrzocker.ore.control.OreControl;
import de.derfrzocker.ore.control.OreControlMessages;
import de.derfrzocker.ore.control.Permissions;
import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.Ore;
import de.derfrzocker.ore.control.api.Setting;
import de.derfrzocker.ore.control.api.WorldOreConfig;
import de.derfrzocker.ore.control.gui.copy.CopyAction;
import de.derfrzocker.ore.control.gui.copy.CopyOreAction;
import de.derfrzocker.ore.control.utils.OreControlUtil;
import de.derfrzocker.spigot.utils.MessageUtil;
import de.derfrzocker.spigot.utils.MessageValue;
import de.derfrzocker.spigot.utils.gui.BasicGui;
import de.derfrzocker.spigot.utils.gui.BasicSettings;
import de.derfrzocker.spigot.utils.gui.VerifyGui;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permissible;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;

public class OreSettingsGui extends BasicGui {

    @NonNull
    private final WorldOreConfig worldOreConfig;

    @NonNull
    private final Ore ore;

    private final Biome biome;

    private final int statusSlot;

    private final CopyAction copyAction;

    private final BiomeGroupGui.BiomeGroup biomeGroup;

    private boolean activated;

    OreSettingsGui(final WorldOreConfig worldOreConfig, final Ore ore, final Biome biome, final Permissible permissible) {
        super(OreSettingsGuiSettings.getInstance());
        this.worldOreConfig = worldOreConfig;
        this.ore = ore;
        this.biome = biome;
        this.statusSlot = OreSettingsGuiSettings.getInstance().getStatusSlot();
        this.activated = biome == null ? OreControlUtil.isActivated(ore, worldOreConfig) : OreControlUtil.isActivated(ore, worldOreConfig, biome);
        this.biomeGroup = null;
        this.copyAction = null;

        final Setting[] settings = ore.getSettings();

        for (int i = 0; i < settings.length; i++)
            addItem(i + OreSettingsGuiSettings.getInstance().getSettingStartSlot(), getSettingItemStack(settings[i]), new SettingConsumer(settings[i]));

        addItem(OreSettingsGuiSettings.getInstance().getBackSlot(), MessageUtil.replaceItemStack(OreSettingsGuiSettings.getInstance().getBackItemStack()),
                event -> openSync(event.getWhoClicked(), new OreGui(worldOreConfig, biome, event.getWhoClicked()).getInventory()));

        addItem(OreSettingsGuiSettings.getInstance().getInfoSlot(), MessageUtil.replaceItemStack(biome == null ? OreSettingsGuiSettings.getInstance().getInfoItemStack() : OreSettingsGuiSettings.getInstance().getInfoBiomeItemStack(), getMessagesValues()));

        addItem(statusSlot, MessageUtil.replaceItemStack(activated ? OreSettingsGuiSettings.getInstance().getDeactivateItemStack() : OreSettingsGuiSettings.getInstance().getActivateItemStack()), event -> handleStatusUpdate());

        if (Permissions.RESET_VALUES_PERMISSION.hasPermission(permissible))
            addItem(OreSettingsGuiSettings.getInstance().getResetValueSlot(), MessageUtil.replaceItemStack(OreSettingsGuiSettings.getInstance().getResetValueItemStack()), this::handleResetValues);

        if (Permissions.COPY_VALUES_PERMISSION.hasPermission(permissible))
            addItem(OreSettingsGuiSettings.getInstance().getCopyValueSlot(), MessageUtil.replaceItemStack(OreSettingsGuiSettings.getInstance().getCopyValueItemStack()), event -> openSync(event.getWhoClicked(), new WorldGui(new CopyOreAction(worldOreConfig, ore, biome)).getInventory()));
    }

    public OreSettingsGui(final WorldOreConfig worldOreConfig, final Ore ore, final Biome biome, final @NonNull CopyAction copyAction) {
        super(OreSettingsGuiSettings.getInstance());
        this.worldOreConfig = worldOreConfig;
        this.ore = ore;
        this.biome = biome;
        this.statusSlot = -1;
        this.copyAction = copyAction;
        this.biomeGroup = null;

        final Set<Setting> settingSet = new LinkedHashSet<>();

        for (Setting setting : ore.getSettings())
            if (copyAction.shouldSet(setting))
                settingSet.add(setting);

        final Setting[] settings = settingSet.toArray(new Setting[0]);

        for (int i = 0; i < settings.length; i++)
            addItem(i + OreSettingsGuiSettings.getInstance().getSettingStartSlot(), getSettingItemStack(settings[i]), new SettingCopyConsumer(settings[i]));

        addItem(OreSettingsGuiSettings.getInstance().getInfoSlot(), MessageUtil.replaceItemStack(biome == null ? OreSettingsGuiSettings.getInstance().getInfoItemStack() : OreSettingsGuiSettings.getInstance().getInfoBiomeItemStack(), getMessagesValues()));
    }

    OreSettingsGui(final WorldOreConfig worldOreConfig, final Ore ore, final BiomeGroupGui.BiomeGroup biomeGroup) {
        super(OreSettingsGuiSettings.getInstance());
        this.worldOreConfig = worldOreConfig;
        this.ore = ore;
        this.biome = null;
        this.statusSlot = OreSettingsGuiSettings.getInstance().getStatusSlot();
        this.activated = true;
        copyAction = null;
        this.biomeGroup = biomeGroup;

        final Setting[] settings = ore.getSettings();

        for (int i = 0; i < settings.length; i++)
            addItem(i + OreSettingsGuiSettings.getInstance().getSettingStartSlot(), getSettingItemStack(settings[i]), new SettingBiomeGroupConsumer(settings[i]));

        addItem(OreSettingsGuiSettings.getInstance().getInfoSlot(), MessageUtil.replaceItemStack(OreSettingsGuiSettings.getInstance().getInfoBiomeItemStack(), getMessagesValues()));

        addItem(statusSlot, MessageUtil.replaceItemStack(OreSettingsGuiSettings.getInstance().getDeactivateItemStack()), event -> handleBiomeGroupStatusUpdate());
        addItem(OreSettingsGuiSettings.getInstance().getBackSlot(), MessageUtil.replaceItemStack(OreSettingsGuiSettings.getInstance().getBackItemStack()),
                event -> openSync(event.getWhoClicked(), new OreGui(worldOreConfig, biomeGroup).getInventory()));
    }

    private ItemStack getSettingItemStack(final Setting setting) {
        final ItemStack itemStack;

        if (biome == null)
            itemStack = MessageUtil.replaceItemStack(OreSettingsGuiSettings.getInstance().getSettingsItemStack(setting), new MessageValue("amount", String.valueOf(OreControlUtil.getAmount(ore, setting, worldOreConfig))));
        else
            itemStack = MessageUtil.replaceItemStack(OreSettingsGuiSettings.getInstance().getSettingsItemStack(setting), new MessageValue("amount", String.valueOf(OreControlUtil.getAmount(ore, setting, worldOreConfig, biome))));

        return itemStack;
    }

    private MessageValue[] getMessagesValues() {
        return new MessageValue[]{new MessageValue("world", worldOreConfig.getName()),
                new MessageValue("biome", biome == null ? biomeGroup == null ? "" : biomeGroup.getName() : biome.toString()),
                new MessageValue("ore", ore.toString())};
    }

    private void handleStatusUpdate() {
        activated = !activated;

        if (biome == null)
            OreControlUtil.setActivated(ore, worldOreConfig, activated);
        else
            OreControlUtil.setActivated(ore, worldOreConfig, activated, biome);

        getInventory().setItem(statusSlot, MessageUtil.replaceItemStack(activated ? OreSettingsGuiSettings.getInstance().getDeactivateItemStack() : OreSettingsGuiSettings.getInstance().getActivateItemStack()));

        OreControl.getService().saveWorldOreConfig(worldOreConfig);
    }

    private void handleBiomeGroupStatusUpdate() {
        activated = !activated;

        biomeGroup.getBiomes().forEach(biome -> OreControlUtil.setActivated(ore, worldOreConfig, activated, biome));

        getInventory().setItem(statusSlot, MessageUtil.replaceItemStack(activated ? OreSettingsGuiSettings.getInstance().getDeactivateItemStack() : OreSettingsGuiSettings.getInstance().getActivateItemStack()));

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
                activated = biome == null ? OreControlUtil.isActivated(ore, worldOreConfig) : OreControlUtil.isActivated(ore, worldOreConfig, biome);
                getInventory().setItem(statusSlot, MessageUtil.replaceItemStack(activated ? OreSettingsGuiSettings.getInstance().getDeactivateItemStack() : OreSettingsGuiSettings.getInstance().getActivateItemStack()));
                openSync(event.getWhoClicked(), getInventory());
                OreControlMessages.RESET_VALUE_SUCCESS.sendMessage(event.getWhoClicked());
            }, clickEvent1 -> openSync(event.getWhoClicked(), getInventory())).getInventory());
            return;
        }
        if (biome != null)
            OreControlUtil.reset(worldOreConfig, ore, biome);
        else
            OreControlUtil.reset(worldOreConfig, ore);

        OreControl.getService().saveWorldOreConfig(worldOreConfig);
        activated = biome == null ? OreControlUtil.isActivated(ore, worldOreConfig) : OreControlUtil.isActivated(ore, worldOreConfig, biome);
        getInventory().setItem(statusSlot, MessageUtil.replaceItemStack(activated ? OreSettingsGuiSettings.getInstance().getDeactivateItemStack() : OreSettingsGuiSettings.getInstance().getActivateItemStack()));
        OreControlMessages.RESET_VALUE_SUCCESS.sendMessage(event.getWhoClicked());
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

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private final class SettingBiomeGroupConsumer implements Consumer<InventoryClickEvent> {

        private final Setting setting;

        @Override
        public void accept(final InventoryClickEvent event) {
            openSync(event.getWhoClicked(), new SettingsGui(worldOreConfig, ore, setting, biomeGroup).getInventory());
        }
    }

}
