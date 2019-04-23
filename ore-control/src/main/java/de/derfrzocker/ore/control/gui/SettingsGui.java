package de.derfrzocker.ore.control.gui;

import de.derfrzocker.ore.control.OreControl;
import de.derfrzocker.ore.control.OreControlMessages;
import de.derfrzocker.ore.control.Permissions;
import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.Ore;
import de.derfrzocker.ore.control.api.Setting;
import de.derfrzocker.ore.control.api.WorldOreConfig;
import de.derfrzocker.ore.control.gui.copy.CopySettingAction;
import de.derfrzocker.ore.control.utils.OreControlUtil;
import de.derfrzocker.spigot.utils.MessageUtil;
import de.derfrzocker.spigot.utils.MessageValue;
import de.derfrzocker.spigot.utils.gui.BasicGui;
import de.derfrzocker.spigot.utils.gui.BasicSettings;
import de.derfrzocker.spigot.utils.gui.VerifyGui;
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

    private final int oreSlot;

    SettingsGui(final WorldOreConfig worldOreConfig, final Ore ore, final Setting setting, final Biome biome, final Permissible permissible) {
        super(SettingsGuiSettings.getInstance());
        this.worldOreConfig = worldOreConfig;
        this.ore = ore;
        this.setting = setting;
        this.biome = biome;
        this.oreSlot = SettingsGuiSettings.getInstance().getOreSlot();

        SettingsGuiSettings.getInstance().getItemStackValues().forEach(value -> addItem(value.getSlot(), MessageUtil.replaceItemStack(value.getItemStack()), new SettingConsumer(value.getValue())));

        addItem(SettingsGuiSettings.getInstance().getBackSlot(), MessageUtil.replaceItemStack(SettingsGuiSettings.getInstance().getBackItemStack()),
                event -> openSync(event.getWhoClicked(), new OreSettingsGui(worldOreConfig, ore, biome, event.getWhoClicked()).getInventory()));

        addItem(SettingsGuiSettings.getInstance().getInfoSlot(), MessageUtil.replaceItemStack(biome == null ? SettingsGuiSettings.getInstance().getInfoItemStack() : SettingsGuiSettings.getInstance().getInfoBiomeItemStack(), getMessagesValues()));

        updateItemStack();

        if (Permissions.RESET_VALUES_PERMISSION.hasPermission(permissible))
            addItem(SettingsGuiSettings.getInstance().getResetValueSlot(), MessageUtil.replaceItemStack(SettingsGuiSettings.getInstance().getResetValueItemStack()), this::handleResetValues);

        if (Permissions.COPY_VALUES_PERMISSION.hasPermission(permissible))
            addItem(SettingsGuiSettings.getInstance().getCopyValueSlot(), MessageUtil.replaceItemStack(SettingsGuiSettings.getInstance().getCopyValueItemStack()), event -> openSync(event.getWhoClicked(), new WorldGui(new CopySettingAction(worldOreConfig, ore, biome, setting)).getInventory()));
    }

    private MessageValue[] getMessagesValues() {
        return new MessageValue[]{new MessageValue("world", worldOreConfig.getName()),
                new MessageValue("biome", biome == null ? "" : biome.toString()),
                new MessageValue("ore", ore.toString()),
                new MessageValue("setting", setting.toString()),
                new MessageValue("amount", String.valueOf(biome == null ? OreControlUtil.getAmount(ore, setting, worldOreConfig) : OreControlUtil.getAmount(ore, setting, worldOreConfig, biome)))
        };
    }

    private void updateItemStack() {
        ItemStack itemStack = biome == null ? SettingsGuiSettings.getInstance().getDefaultOreItemStack() : SettingsGuiSettings.getInstance().getDefaultBiomeOreItemStack();
        itemStack.setType(ore.getMaterial());
        itemStack = MessageUtil.replaceItemStack(itemStack, getMessagesValues());
        getInventory().setItem(oreSlot, itemStack);
    }

    private void handleResetValues(final InventoryClickEvent event) {
        if (OreControl.getInstance().getConfigValues().verifyResetAction()) {
            openSync(event.getWhoClicked(), new VerifyGui(clickEvent -> {
                if (biome != null)
                    OreControlUtil.reset(worldOreConfig, ore, biome, setting);
                else
                    OreControlUtil.reset(worldOreConfig, ore, setting);

                OreControl.getService().saveWorldOreConfig(worldOreConfig);
                openSync(event.getWhoClicked(), getInventory());
                OreControlMessages.RESET_VALUE_SUCCESS.sendMessage(event.getWhoClicked());
            }, clickEvent1 -> openSync(event.getWhoClicked(), getInventory())).getInventory());
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

}
