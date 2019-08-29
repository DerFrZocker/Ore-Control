package de.derfrzocker.ore.control.gui;

import de.derfrzocker.ore.control.OreControl;
import de.derfrzocker.ore.control.OreControlMessages;
import de.derfrzocker.ore.control.Permissions;
import de.derfrzocker.ore.control.api.*;
import de.derfrzocker.ore.control.gui.copy.CopyAction;
import de.derfrzocker.ore.control.gui.copy.CopyOreAction;
import de.derfrzocker.ore.control.utils.OreControlUtil;
import de.derfrzocker.spigot.utils.gui.BasicGui;
import de.derfrzocker.spigot.utils.gui.BasicSettings;
import de.derfrzocker.spigot.utils.gui.VerifyGui;
import de.derfrzocker.spigot.utils.message.MessageUtil;
import de.derfrzocker.spigot.utils.message.MessageValue;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permissible;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

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

    @NonNull
    private final Supplier<OreControlService> serviceSupplier;

    OreSettingsGui(final WorldOreConfig worldOreConfig, final Ore ore, final Biome biome, final @NonNull Permissible permissible, final Supplier<OreControlService> serviceSupplier) {
        super(OreControl.getInstance(), OreSettingsGuiSettings.getInstance());
        this.worldOreConfig = worldOreConfig;
        this.ore = ore;
        this.biome = biome;
        this.statusSlot = OreSettingsGuiSettings.getInstance().getStatusSlot();
        this.activated = biome == null ? OreControlUtil.isActivated(ore, worldOreConfig) : OreControlUtil.isActivated(ore, worldOreConfig, biome);
        this.biomeGroup = null;
        this.copyAction = null;
        this.serviceSupplier = serviceSupplier;

        final Setting[] settings = ore.getSettings();

        for (int i = 0; i < settings.length; i++)
            addItem(i + OreSettingsGuiSettings.getInstance().getSettingStartSlot(), getSettingItemStack(settings[i]), new SettingConsumer(settings[i]));

        addItem(OreSettingsGuiSettings.getInstance().getBackSlot(), MessageUtil.replaceItemStack(OreControl.getInstance(), OreSettingsGuiSettings.getInstance().getBackItemStack()),
                event -> new OreGui(worldOreConfig, biome, event.getWhoClicked(), serviceSupplier).openSync(event.getWhoClicked()));

        addItem(OreSettingsGuiSettings.getInstance().getInfoSlot(), MessageUtil.replaceItemStack(OreControl.getInstance(), biome == null ? OreSettingsGuiSettings.getInstance().getInfoItemStack() : OreSettingsGuiSettings.getInstance().getInfoBiomeItemStack(), getMessagesValues()));

        addItem(statusSlot, MessageUtil.replaceItemStack(OreControl.getInstance(), activated ? OreSettingsGuiSettings.getInstance().getDeactivateItemStack() : OreSettingsGuiSettings.getInstance().getActivateItemStack()), event -> handleStatusUpdate());

        if (Permissions.RESET_VALUE_PERMISSION.hasPermission(permissible))
            addItem(OreSettingsGuiSettings.getInstance().getResetValueSlot(), MessageUtil.replaceItemStack(OreControl.getInstance(), OreSettingsGuiSettings.getInstance().getResetValueItemStack()), this::handleResetValues);

        if (Permissions.COPY_VALUE_PERMISSION.hasPermission(permissible))
            addItem(OreSettingsGuiSettings.getInstance().getCopyValueSlot(), MessageUtil.replaceItemStack(OreControl.getInstance(), OreSettingsGuiSettings.getInstance().getCopyValueItemStack()), event -> new WorldGui(new CopyOreAction(worldOreConfig, ore, biome, serviceSupplier), serviceSupplier).openSync(event.getWhoClicked()));
    }

    public OreSettingsGui(final WorldOreConfig worldOreConfig, final Ore ore, final Biome biome, final @NonNull CopyAction copyAction, final Supplier<OreControlService> serviceSupplier) {
        super(OreControl.getInstance(), OreSettingsGuiSettings.getInstance());
        this.worldOreConfig = worldOreConfig;
        this.ore = ore;
        this.biome = biome;
        this.statusSlot = -1;
        this.copyAction = copyAction;
        this.biomeGroup = null;
        this.serviceSupplier = serviceSupplier;

        final Set<Setting> settingSet = new LinkedHashSet<>();

        for (final Setting setting : ore.getSettings())
            if (copyAction.shouldSet(setting))
                settingSet.add(setting);

        final Setting[] settings = settingSet.toArray(new Setting[0]);

        for (int i = 0; i < settings.length; i++)
            addItem(i + OreSettingsGuiSettings.getInstance().getSettingStartSlot(), getSettingItemStack(settings[i]), new SettingCopyConsumer(settings[i]));

        addItem(OreSettingsGuiSettings.getInstance().getInfoSlot(), MessageUtil.replaceItemStack(OreControl.getInstance(), biome == null ? OreSettingsGuiSettings.getInstance().getInfoItemStack() : OreSettingsGuiSettings.getInstance().getInfoBiomeItemStack(), getMessagesValues()));
    }

    OreSettingsGui(final WorldOreConfig worldOreConfig, final Ore ore, final @NonNull BiomeGroupGui.BiomeGroup biomeGroup, final Supplier<OreControlService> serviceSupplier) {
        super(OreControl.getInstance(), OreSettingsGuiSettings.getInstance());
        this.worldOreConfig = worldOreConfig;
        this.ore = ore;
        this.biome = null;
        this.statusSlot = OreSettingsGuiSettings.getInstance().getStatusSlot();
        this.activated = true;
        copyAction = null;
        this.biomeGroup = biomeGroup;
        this.serviceSupplier = serviceSupplier;

        final Setting[] settings = ore.getSettings();

        for (int i = 0; i < settings.length; i++)
            addItem(i + OreSettingsGuiSettings.getInstance().getSettingStartSlot(), getSettingItemStack(settings[i]), new SettingBiomeGroupConsumer(settings[i]));

        addItem(OreSettingsGuiSettings.getInstance().getInfoSlot(), MessageUtil.replaceItemStack(OreControl.getInstance(), OreSettingsGuiSettings.getInstance().getInfoBiomeItemStack(), getMessagesValues()));

        addItem(statusSlot, MessageUtil.replaceItemStack(OreControl.getInstance(), OreSettingsGuiSettings.getInstance().getDeactivateItemStack()), event -> handleBiomeGroupStatusUpdate());
        addItem(OreSettingsGuiSettings.getInstance().getBackSlot(), MessageUtil.replaceItemStack(OreControl.getInstance(), OreSettingsGuiSettings.getInstance().getBackItemStack()),
                event -> new OreGui(worldOreConfig, biomeGroup, serviceSupplier).openSync(event.getWhoClicked()));
    }

    private ItemStack getSettingItemStack(final @NonNull Setting setting) {
        final ItemStack itemStack;

        if (biome == null)
            itemStack = MessageUtil.replaceItemStack(OreControl.getInstance(), OreSettingsGuiSettings.getInstance().getSettingsItemStack(setting), new MessageValue("amount", String.valueOf(OreControlUtil.getAmount(ore, setting, worldOreConfig))));
        else
            itemStack = MessageUtil.replaceItemStack(OreControl.getInstance(), OreSettingsGuiSettings.getInstance().getSettingsItemStack(setting), new MessageValue("amount", String.valueOf(OreControlUtil.getAmount(ore, setting, worldOreConfig, biome))));

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

        addItem(statusSlot, MessageUtil.replaceItemStack(OreControl.getInstance(), activated ? OreSettingsGuiSettings.getInstance().getDeactivateItemStack() : OreSettingsGuiSettings.getInstance().getActivateItemStack()));

        serviceSupplier.get().saveWorldOreConfig(worldOreConfig);
    }

    private void handleBiomeGroupStatusUpdate() {
        activated = !activated;

        biomeGroup.getBiomes().forEach(biome -> OreControlUtil.setActivated(ore, worldOreConfig, activated, biome));

        addItem(statusSlot, MessageUtil.replaceItemStack(OreControl.getInstance(), activated ? OreSettingsGuiSettings.getInstance().getDeactivateItemStack() : OreSettingsGuiSettings.getInstance().getActivateItemStack()));

        serviceSupplier.get().saveWorldOreConfig(worldOreConfig);
    }

    private void handleResetValues(final @NonNull InventoryClickEvent event) {
        if (OreControl.getInstance().getConfigValues().verifyResetAction()) {
            new VerifyGui(OreControl.getInstance(), clickEvent -> {
                if (biome != null)
                    OreControlUtil.reset(worldOreConfig, ore, biome);
                else
                    OreControlUtil.reset(worldOreConfig, ore);

                serviceSupplier.get().saveWorldOreConfig(worldOreConfig);
                activated = biome == null ? OreControlUtil.isActivated(ore, worldOreConfig) : OreControlUtil.isActivated(ore, worldOreConfig, biome);
                addItem(statusSlot, MessageUtil.replaceItemStack(OreControl.getInstance(), activated ? OreSettingsGuiSettings.getInstance().getDeactivateItemStack() : OreSettingsGuiSettings.getInstance().getActivateItemStack()));
                openSync(event.getWhoClicked());
                OreControlMessages.RESET_VALUE_SUCCESS.sendMessage(event.getWhoClicked());
            }, clickEvent1 -> openSync(event.getWhoClicked())).openSync(event.getWhoClicked());
            return;
        }
        if (biome != null)
            OreControlUtil.reset(worldOreConfig, ore, biome);
        else
            OreControlUtil.reset(worldOreConfig, ore);

        serviceSupplier.get().saveWorldOreConfig(worldOreConfig);
        activated = biome == null ? OreControlUtil.isActivated(ore, worldOreConfig) : OreControlUtil.isActivated(ore, worldOreConfig, biome);
        addItem(statusSlot, MessageUtil.replaceItemStack(OreControl.getInstance(), activated ? OreSettingsGuiSettings.getInstance().getDeactivateItemStack() : OreSettingsGuiSettings.getInstance().getActivateItemStack()));
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

        private ItemStack getSettingsItemStack(final @NonNull Setting setting) {
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
        public void accept(final @NonNull InventoryClickEvent event) {
            new SettingsGui(worldOreConfig, ore, setting, biome, event.getWhoClicked(), serviceSupplier).openSync(event.getWhoClicked());
        }
    }

    @SuppressWarnings("ConstantConditions")
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private final class SettingCopyConsumer implements Consumer<InventoryClickEvent> {

        private final Setting setting;

        @Override
        public void accept(final @NonNull InventoryClickEvent event) {
            copyAction.setSettingTarget(setting);

            copyAction.next(event.getWhoClicked(), OreSettingsGui.this);
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private final class SettingBiomeGroupConsumer implements Consumer<InventoryClickEvent> {

        private final Setting setting;

        @Override
        public void accept(final @NonNull InventoryClickEvent event) {
            new SettingsGui(worldOreConfig, ore, setting, biomeGroup, serviceSupplier).openSync(event.getWhoClicked());
        }
    }

}
