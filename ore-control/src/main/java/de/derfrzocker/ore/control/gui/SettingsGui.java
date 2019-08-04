package de.derfrzocker.ore.control.gui;

import com.google.common.collect.Sets;
import de.derfrzocker.ore.control.OreControl;
import de.derfrzocker.ore.control.OreControlMessages;
import de.derfrzocker.ore.control.Permissions;
import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.Ore;
import de.derfrzocker.ore.control.api.Setting;
import de.derfrzocker.ore.control.api.WorldOreConfig;
import de.derfrzocker.ore.control.gui.copy.CopySettingAction;
import de.derfrzocker.ore.control.utils.OreControlUtil;
import de.derfrzocker.spigot.utils.gui.BasicGui;
import de.derfrzocker.spigot.utils.gui.BasicSettings;
import de.derfrzocker.spigot.utils.gui.VerifyGui;
import de.derfrzocker.spigot.utils.message.MessageUtil;
import de.derfrzocker.spigot.utils.message.MessageValue;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permissible;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import static de.derfrzocker.ore.control.OreControlMessages.SET_NOT_SAFE;
import static de.derfrzocker.ore.control.OreControlMessages.SET_NOT_SAFE_WARNING;

public class SettingsGui extends BasicGui {

    @NonNull
    private final WorldOreConfig worldOreConfig;

    @NonNull
    private final Ore ore;

    @NonNull
    private final Setting setting;

    private final Biome biome;

    private final BiomeGroupGui.BiomeGroup biomeGroup;

    private final int oreSlot;

    private int current = 0;

    SettingsGui(final WorldOreConfig worldOreConfig, final Ore ore, final Setting setting, final Biome biome, final Permissible permissible) {
        super(OreControl.getInstance(), SettingsGuiSettings.getInstance());
        this.worldOreConfig = worldOreConfig;
        this.ore = ore;
        this.setting = setting;
        this.biome = biome;
        this.oreSlot = SettingsGuiSettings.getInstance().getOreSlot();
        this.biomeGroup = null;

        SettingsGuiSettings.getInstance().getItemStackValues().forEach(value -> addItem(value.getSlot(), MessageUtil.replaceItemStack(OreControl.getInstance(), value.getItemStack()), new SettingConsumer(value.getValue())));

        addItem(SettingsGuiSettings.getInstance().getBackSlot(), MessageUtil.replaceItemStack(OreControl.getInstance(), SettingsGuiSettings.getInstance().getBackItemStack()),
                event -> new OreSettingsGui(worldOreConfig, ore, biome, event.getWhoClicked()).openSync(event.getWhoClicked()));

        addItem(SettingsGuiSettings.getInstance().getInfoSlot(), MessageUtil.replaceItemStack(OreControl.getInstance(), biome == null ? SettingsGuiSettings.getInstance().getInfoItemStack() : SettingsGuiSettings.getInstance().getInfoBiomeItemStack(), getMessagesValues(false)));

        updateItemStack();

        if (Permissions.RESET_VALUE_PERMISSION.hasPermission(permissible))
            addItem(SettingsGuiSettings.getInstance().getResetValueSlot(), MessageUtil.replaceItemStack(OreControl.getInstance(), SettingsGuiSettings.getInstance().getResetValueItemStack()), this::handleResetValues);

        if (Permissions.COPY_VALUE_PERMISSION.hasPermission(permissible))
            addItem(SettingsGuiSettings.getInstance().getCopyValueSlot(), MessageUtil.replaceItemStack(OreControl.getInstance(), SettingsGuiSettings.getInstance().getCopyValueItemStack()), event -> new WorldGui(new CopySettingAction(worldOreConfig, ore, biome, setting)).openSync(event.getWhoClicked()));
    }

    SettingsGui(final WorldOreConfig worldOreConfig, final Ore ore, final Setting setting, final BiomeGroupGui.BiomeGroup biomeGroup) {
        super(OreControl.getInstance(), SettingsGuiSettings.getInstance());
        this.worldOreConfig = worldOreConfig;
        this.ore = ore;
        this.setting = setting;
        this.biome = null;
        this.oreSlot = SettingsGuiSettings.getInstance().getOreSlot();
        this.biomeGroup = biomeGroup;

        SettingsGuiSettings.getInstance().getItemStackValues().forEach(value -> addItem(value.getSlot(), MessageUtil.replaceItemStack(OreControl.getInstance(), value.getItemStack()), new SettingBiomeGroupConsumer(value.getValue())));

        addItem(SettingsGuiSettings.getInstance().getInfoSlot(), MessageUtil.replaceItemStack(OreControl.getInstance(), SettingsGuiSettings.getInstance().getInfoBiomeItemStack(), getMessagesValues(true)));
        addItem(SettingsGuiSettings.getInstance().getBackSlot(), MessageUtil.replaceItemStack(OreControl.getInstance(), SettingsGuiSettings.getInstance().getBackItemStack()),
                event -> new OreSettingsGui(worldOreConfig, ore, biomeGroup).openSync(event.getWhoClicked()));

        updateBiomeGroupItemStack(true);
    }

    private MessageValue[] getMessagesValues(boolean firstUpdate) {
        return new MessageValue[]{new MessageValue("world", worldOreConfig.getName()),
                new MessageValue("biome", biome == null ? biomeGroup == null ? "" : biomeGroup.getName() : biome.toString()),
                new MessageValue("ore", ore.toString()),
                new MessageValue("setting", setting.toString()),
                new MessageValue("amount", String.valueOf(biome == null ? biomeGroup == null ? OreControlUtil.getAmount(ore, setting, worldOreConfig) : firstUpdate ? "N/A" : current : OreControlUtil.getAmount(ore, setting, worldOreConfig, biome)))
        };
    }

    private void updateItemStack() {
        ItemStack itemStack = biome == null ? SettingsGuiSettings.getInstance().getDefaultOreItemStack() : SettingsGuiSettings.getInstance().getDefaultBiomeOreItemStack();
        itemStack.setType(ore.getMaterial());
        itemStack = MessageUtil.replaceItemStack(OreControl.getInstance(), itemStack, getMessagesValues(false));
        addItem(oreSlot, itemStack);
    }

    private void updateBiomeGroupItemStack(boolean firstUpdate) {
        ItemStack itemStack = SettingsGuiSettings.getInstance().getDefaultBiomeOreItemStack();
        itemStack.setType(ore.getMaterial());
        itemStack = MessageUtil.replaceItemStack(OreControl.getInstance(), itemStack, getMessagesValues(firstUpdate));
        addItem(oreSlot, itemStack);
    }

    private void handleResetValues(final InventoryClickEvent event) {
        if (OreControl.getInstance().getConfigValues().verifyResetAction()) {
            new VerifyGui(OreControl.getInstance(), clickEvent -> {
                if (biome != null)
                    OreControlUtil.reset(worldOreConfig, ore, biome, setting);
                else
                    OreControlUtil.reset(worldOreConfig, ore, setting);

                OreControl.getService().saveWorldOreConfig(worldOreConfig);
                openSync(event.getWhoClicked());
                OreControlMessages.RESET_VALUE_SUCCESS.sendMessage(event.getWhoClicked());
            }, clickEvent1 -> openSync(event.getWhoClicked())).openSync(event.getWhoClicked());
            return;
        }
        if (biome != null)
            OreControlUtil.reset(worldOreConfig, ore, biome, setting);
        else
            OreControlUtil.reset(worldOreConfig, ore, setting);

        OreControl.getService().saveWorldOreConfig(worldOreConfig);
        OreControlMessages.RESET_VALUE_SUCCESS.sendMessage(event.getWhoClicked());
    }

    private static final class SettingsGuiSettings extends BasicSettings {

        private static SettingsGuiSettings instance = null;

        private static SettingsGuiSettings getInstance() {
            if (instance == null)
                instance = new SettingsGuiSettings();

            return instance;
        }

        private SettingsGuiSettings() {
            super(OreControl.getInstance(), "data/settings_gui.yml");
        }

        private int getOreSlot() {
            return getYaml().getInt("inventory.ore.slot");
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

        private Set<SettingsGuiSettings.ItemStackValues> getItemStackValues() {
            Set<SettingsGuiSettings.ItemStackValues> set = new HashSet<>();
            getYaml().getConfigurationSection("inventory.items").
                    getKeys(false).stream().
                    map(value -> getYaml().getConfigurationSection("inventory.items." + value)).
                    map(value -> new SettingsGuiSettings.ItemStackValues(value.getInt("slot", 0), value.getInt("value", 0), value.getItemStack("item_stack"))).
                    forEach(set::add);
            return set;
        }

        private ItemStack getDefaultOreItemStack() {
            return getYaml().getItemStack("default_ore_item_stack").clone();
        }

        private ItemStack getDefaultBiomeOreItemStack() {
            return getYaml().getItemStack("default_biome_ore_item_stack").clone();
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

        @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
        @Getter(value = AccessLevel.PRIVATE)
        private final class ItemStackValues {
            private final int slot;
            private final int value;
            private final ItemStack itemStack;
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private final class SettingConsumer implements Consumer<InventoryClickEvent> {

        private final int value;

        @Override
        public void accept(final InventoryClickEvent event) {
            int current = biome == null ? OreControlUtil.getAmount(ore, setting, worldOreConfig) : OreControlUtil.getAmount(ore, setting, worldOreConfig, biome);

            int newValue = current + value;

            if (OreControlUtil.isUnSafe(setting, newValue)) {
                if (OreControl.getInstance().getConfigValues().isSafeMode()) {
                    SET_NOT_SAFE.sendMessage(event.getWhoClicked(), new MessageValue("value", String.valueOf(newValue)));
                    return;
                }
                SET_NOT_SAFE_WARNING.sendMessage(event.getWhoClicked(), new MessageValue("value", String.valueOf(newValue)));
            }

            if (biome == null)
                OreControlUtil.setAmount(ore, setting, worldOreConfig, newValue);
            else
                OreControlUtil.setAmount(ore, setting, worldOreConfig, newValue, biome);

            OreControl.getService().saveWorldOreConfig(worldOreConfig);

            updateItemStack();
        }
    }

    @SuppressWarnings("ConstantConditions")
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private final class SettingBiomeGroupConsumer implements Consumer<InventoryClickEvent> {

        private final int value;

        @Override
        public void accept(final InventoryClickEvent event) {
            int newValue = current + value;

            if (OreControlUtil.isUnSafe(setting, newValue)) {
                if (OreControl.getInstance().getConfigValues().isSafeMode()) {
                    SET_NOT_SAFE.sendMessage(event.getWhoClicked(), new MessageValue("value", String.valueOf(newValue)));
                    return;
                }
                SET_NOT_SAFE_WARNING.sendMessage(event.getWhoClicked(), new MessageValue("value", String.valueOf(newValue)));
            }

            current = newValue;

            biomeGroup.getBiomes().stream().filter(biome -> Sets.newHashSet(biome.getOres()).contains(ore)).forEach(biome -> OreControlUtil.setAmount(ore, setting, worldOreConfig, current, biome));

            OreControl.getService().saveWorldOreConfig(worldOreConfig);

            updateBiomeGroupItemStack(false);
        }
    }

}
