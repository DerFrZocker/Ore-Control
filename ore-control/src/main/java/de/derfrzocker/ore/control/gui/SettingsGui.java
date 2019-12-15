package de.derfrzocker.ore.control.gui;

import com.google.common.collect.Sets;
import de.derfrzocker.ore.control.Permissions;
import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.Ore;
import de.derfrzocker.ore.control.api.Setting;
import de.derfrzocker.ore.control.api.WorldOreConfig;
import de.derfrzocker.ore.control.gui.copy.CopySettingAction;
import de.derfrzocker.ore.control.gui.settings.BiomeGuiSettings;
import de.derfrzocker.ore.control.gui.settings.SettingsGuiSettings;
import de.derfrzocker.ore.control.utils.OreControlUtil;
import de.derfrzocker.ore.control.utils.OreControlValues;
import de.derfrzocker.spigot.utils.gui.BasicGui;
import de.derfrzocker.spigot.utils.gui.VerifyGui;
import de.derfrzocker.spigot.utils.message.MessageUtil;
import de.derfrzocker.spigot.utils.message.MessageValue;
import org.apache.commons.lang.Validate;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permissible;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class SettingsGui extends BasicGui {

    private static SettingsGuiSettings settingsGuiSettings;

    @NotNull
    private final OreControlValues oreControlValues;
    @NotNull
    private final WorldOreConfig worldOreConfig;
    @Nullable
    private final Biome biome;
    @Nullable
    private final BiomeGroupGui.BiomeGroup biomeGroup;
    @NotNull
    private final Ore ore;
    @NotNull
    private final Setting setting;
    private final int oreSlot;

    private int current = 0;

    SettingsGui(@NotNull final OreControlValues oreControlValues, @NotNull final Permissible permissible, @NotNull final WorldOreConfig worldOreConfig, @Nullable final Biome biome, @NotNull final Ore ore, @NotNull final Setting setting) {
        super(oreControlValues.getJavaPlugin(), checkSettings(oreControlValues.getJavaPlugin()));

        Validate.notNull(permissible, "Permissible can not be null");
        Validate.notNull(worldOreConfig, "WorldOreConfig can not be null");
        Validate.notNull(ore, "Ore can not be null");
        Validate.notNull(setting, "Setting can not be null");

        this.oreControlValues = oreControlValues;
        this.worldOreConfig = worldOreConfig;
        this.biome = biome;
        this.biomeGroup = null;
        this.ore = ore;
        this.setting = setting;
        this.oreSlot = settingsGuiSettings.getOreSlot();

        addDecorations();

        final JavaPlugin javaPlugin = oreControlValues.getJavaPlugin();
        final Permissions permissions = oreControlValues.getPermissions();

        settingsGuiSettings.getItemStackValues().forEach(value -> addItem(value.getSlot(), MessageUtil.replaceItemStack(javaPlugin, value.getItemStack()), new SettingConsumer(value.getValue())));

        addItem(settingsGuiSettings.getBackSlot(), MessageUtil.replaceItemStack(javaPlugin, settingsGuiSettings.getBackItemStack()),
                event -> new OreSettingsGui(oreControlValues, event.getWhoClicked(), worldOreConfig, biome, ore).openSync(event.getWhoClicked()));

        addItem(settingsGuiSettings.getInfoSlot(), MessageUtil.replaceItemStack(javaPlugin, biome == null ? settingsGuiSettings.getInfoItemStack() : settingsGuiSettings.getInfoBiomeItemStack(), getMessagesValues(false)));

        updateItemStack();

        if (permissions.getValueResetPermission().hasPermission(permissible))
            addItem(settingsGuiSettings.getResetValueSlot(), MessageUtil.replaceItemStack(javaPlugin, settingsGuiSettings.getResetValueItemStack()), this::handleResetValues);

        if (permissions.getValueCopyPermission().hasPermission(permissible))
            addItem(settingsGuiSettings.getCopyValueSlot(), MessageUtil.replaceItemStack(javaPlugin, settingsGuiSettings.getCopyValueItemStack()), event -> new WorldGui(oreControlValues, new CopySettingAction(oreControlValues, worldOreConfig, biome, ore, setting)).openSync(event.getWhoClicked()));
    }

    SettingsGui(@NotNull final OreControlValues oreControlValues, @NotNull final Permissible permissible, @NotNull final WorldOreConfig worldOreConfig, @NotNull final BiomeGroupGui.BiomeGroup biomeGroup, @NotNull final Ore ore, @NotNull final Setting setting, @NotNull final BiomeGuiSettings biomeGuiSettings) {
        super(oreControlValues.getJavaPlugin(), checkSettings(oreControlValues.getJavaPlugin()));

        Validate.notNull(permissible, "Permissible can not be null");
        Validate.notNull(worldOreConfig, "WorldOreConfig can not be null");
        Validate.notNull(ore, "Ore can not be null");
        Validate.notNull(setting, "Setting can not be null");

        this.oreControlValues = oreControlValues;
        this.worldOreConfig = worldOreConfig;
        this.biome = null;
        this.biomeGroup = biomeGroup;
        this.ore = ore;
        this.setting = setting;
        this.oreSlot = settingsGuiSettings.getOreSlot();

        addDecorations();

        final JavaPlugin javaPlugin = oreControlValues.getJavaPlugin();

        settingsGuiSettings.getItemStackValues().forEach(value -> addItem(value.getSlot(), MessageUtil.replaceItemStack(javaPlugin, value.getItemStack()), new SettingBiomeGroupConsumer(value.getValue())));

        addItem(settingsGuiSettings.getInfoSlot(), MessageUtil.replaceItemStack(javaPlugin, settingsGuiSettings.getInfoBiomeItemStack(), getMessagesValues(true)));
        addItem(settingsGuiSettings.getBackSlot(), MessageUtil.replaceItemStack(javaPlugin, settingsGuiSettings.getBackItemStack()),
                event -> new OreSettingsGui(oreControlValues, event.getWhoClicked(), worldOreConfig, biomeGroup, ore, biomeGuiSettings).openSync(event.getWhoClicked()));

        updateBiomeGroupItemStack(true);
    }

    private static SettingsGuiSettings checkSettings(@NotNull final JavaPlugin javaPlugin) {
        if (settingsGuiSettings == null)
            settingsGuiSettings = new SettingsGuiSettings(javaPlugin, "data/gui/settings-gui.yml", true);

        return settingsGuiSettings;
    }

    private MessageValue[] getMessagesValues(final boolean firstUpdate) {
        return new MessageValue[]{new MessageValue("world", worldOreConfig.getName()),
                new MessageValue("biome", biome == null ? biomeGroup == null ? "" : biomeGroup.getName() : biome.toString()),
                new MessageValue("ore", ore.toString()),
                new MessageValue("setting", setting.toString()),
                new MessageValue("amount", String.valueOf(biome == null ? biomeGroup == null ? OreControlUtil.getAmount(ore, setting, worldOreConfig) : firstUpdate ? "N/A" : current : OreControlUtil.getAmount(ore, setting, worldOreConfig, biome)))
        };
    }

    private void updateItemStack() {
        ItemStack itemStack = biome == null ? settingsGuiSettings.getDefaultOreItemStack() : settingsGuiSettings.getDefaultBiomeOreItemStack();
        itemStack.setType(ore.getMaterial());
        itemStack = MessageUtil.replaceItemStack(getPlugin(), itemStack, getMessagesValues(false));
        addItem(oreSlot, itemStack);
    }

    private void updateBiomeGroupItemStack(final boolean firstUpdate) {
        ItemStack itemStack = settingsGuiSettings.getDefaultBiomeOreItemStack();
        itemStack.setType(ore.getMaterial());
        itemStack = MessageUtil.replaceItemStack(getPlugin(), itemStack, getMessagesValues(firstUpdate));
        addItem(oreSlot, itemStack);
    }

    private void handleResetValues(@NotNull final InventoryClickEvent event) {
        if (oreControlValues.getConfigValues().verifyResetAction()) {
            new VerifyGui(getPlugin(), clickEvent -> {
                if (biome != null)
                    OreControlUtil.reset(worldOreConfig, ore, biome, setting);
                else
                    OreControlUtil.reset(worldOreConfig, ore, setting);

                oreControlValues.getService().saveWorldOreConfig(worldOreConfig);
                openSync(event.getWhoClicked());
                oreControlValues.getOreControlMessages().getGuiResetSuccessMessage().sendMessage(event.getWhoClicked());
            }, clickEvent1 -> openSync(event.getWhoClicked())).openSync(event.getWhoClicked());
            return;
        }
        if (biome != null)
            OreControlUtil.reset(worldOreConfig, ore, biome, setting);
        else
            OreControlUtil.reset(worldOreConfig, ore, setting);

        oreControlValues.getService().saveWorldOreConfig(worldOreConfig);
        oreControlValues.getOreControlMessages().getGuiResetSuccessMessage().sendMessage(event.getWhoClicked());
    }

    private final class SettingConsumer implements Consumer<InventoryClickEvent> {

        private final int value;

        private SettingConsumer(final int value) {
            this.value = value;
        }

        @Override
        public void accept(@NotNull final InventoryClickEvent event) {
            int current = biome == null ? OreControlUtil.getAmount(ore, setting, worldOreConfig) : OreControlUtil.getAmount(ore, setting, worldOreConfig, biome);

            int newValue = current + value;

            if (OreControlUtil.isUnSafe(setting, newValue)) {
                if (oreControlValues.getConfigValues().isSafeMode()) {
                    oreControlValues.getOreControlMessages().getNumberNotSafeMessage().sendMessage(event.getWhoClicked(), new MessageValue("value", String.valueOf(newValue)));
                    return;
                }
                oreControlValues.getOreControlMessages().getNumberNotSafeWarningMessage().sendMessage(event.getWhoClicked(), new MessageValue("value", String.valueOf(newValue)));
            }

            if (biome == null)
                OreControlUtil.setAmount(ore, setting, worldOreConfig, newValue);
            else
                OreControlUtil.setAmount(ore, setting, worldOreConfig, newValue, biome);

            oreControlValues.getService().saveWorldOreConfig(worldOreConfig);

            updateItemStack();
        }

    }

    private final class SettingBiomeGroupConsumer implements Consumer<InventoryClickEvent> {

        private final int value;

        private SettingBiomeGroupConsumer(int value) {
            this.value = value;
        }

        @Override
        public void accept(@NotNull final InventoryClickEvent event) {
            int newValue = current + value;

            if (OreControlUtil.isUnSafe(setting, newValue)) {
                if (oreControlValues.getConfigValues().isSafeMode()) {
                    oreControlValues.getOreControlMessages().getNumberNotSafeMessage().sendMessage(event.getWhoClicked(), new MessageValue("value", String.valueOf(newValue)));
                    return;
                }
                oreControlValues.getOreControlMessages().getNumberNotSafeWarningMessage().sendMessage(event.getWhoClicked(), new MessageValue("value", String.valueOf(newValue)));
            }

            current = newValue;

            biomeGroup.getBiomes().stream().filter(biome -> Sets.newHashSet(biome.getOres()).contains(ore)).forEach(biome -> OreControlUtil.setAmount(ore, setting, worldOreConfig, current, biome));

            oreControlValues.getService().saveWorldOreConfig(worldOreConfig);

            updateBiomeGroupItemStack(false);
        }

    }

}
