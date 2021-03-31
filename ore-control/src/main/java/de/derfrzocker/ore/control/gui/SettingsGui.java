/*
 * MIT License
 *
 * Copyright (c) 2019 - 2021 Marvin (DerFrZocker)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package de.derfrzocker.ore.control.gui;

import com.google.common.collect.Sets;
import de.derfrzocker.ore.control.Permissions;
import de.derfrzocker.ore.control.api.*;
import de.derfrzocker.ore.control.gui.copy.CopySettingAction;
import de.derfrzocker.ore.control.gui.settings.GuiSettings;
import de.derfrzocker.ore.control.gui.settings.SettingsGuiSettings;
import de.derfrzocker.ore.control.utils.BaseComponentUtil;
import de.derfrzocker.ore.control.utils.OreControlUtil;
import de.derfrzocker.ore.control.utils.OreControlValues;
import de.derfrzocker.ore.control.utils.ResetUtil;
import de.derfrzocker.spigot.utils.gui.BasicGui;
import de.derfrzocker.spigot.utils.gui.VerifyGui;
import de.derfrzocker.spigot.utils.message.MessageUtil;
import de.derfrzocker.spigot.utils.message.MessageValue;
import net.md_5.bungee.api.chat.BaseComponent;
import org.apache.commons.lang.Validate;
import org.bukkit.command.CommandSender;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permissible;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.function.Consumer;

public class SettingsGui extends BasicGui {

    @NotNull
    private final GuiSettings guiSettings;
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

    private double current = 0;

    SettingsGui(@NotNull final GuiSettings guiSettings, @NotNull final OreControlValues oreControlValues, @NotNull final Permissible permissible, @NotNull final WorldOreConfig worldOreConfig, @Nullable Dimension dimension, @Nullable final Biome biome, @NotNull final Ore ore, @NotNull final Setting setting) {
        super(oreControlValues.getPlugin(), guiSettings.getSettingsGuiSettings());

        Validate.notNull(permissible, "Permissible cannot be null");
        Validate.notNull(worldOreConfig, "WorldOreConfig cannot be null");
        Validate.notNull(ore, "Ore cannot be null");
        Validate.notNull(setting, "Setting cannot be null");

        this.guiSettings = guiSettings;
        this.oreControlValues = oreControlValues;
        this.worldOreConfig = worldOreConfig;
        this.biome = biome;
        this.biomeGroup = null;
        this.ore = ore;
        this.setting = setting;
        this.oreSlot = guiSettings.getSettingsGuiSettings().getOreSlot();

        addDecorations();

        final SettingsGuiSettings settingsGuiSettings = guiSettings.getSettingsGuiSettings();
        final Plugin plugin = oreControlValues.getPlugin();
        final Permissions permissions = oreControlValues.getPermissions();

        settingsGuiSettings.getItemStackValues().forEach(value -> addItem(value.getSlot(), MessageUtil.replaceItemStack(plugin, value.getItemStack(), getMessagesValues(true, value.getValue())), new SettingConsumer(value.getValue())));

        addItem(settingsGuiSettings.getBackSlot(), MessageUtil.replaceItemStack(plugin, settingsGuiSettings.getBackItemStack()),
                event -> new OreSettingsGui(guiSettings, oreControlValues, event.getWhoClicked(), worldOreConfig, dimension, biome, ore).openSync(event.getWhoClicked()));

        addItem(settingsGuiSettings.getInfoSlot(), MessageUtil.replaceItemStack(plugin, biome == null ? settingsGuiSettings.getInfoItemStack() : settingsGuiSettings.getInfoBiomeItemStack(), getMessagesValues(false)));

        updateItemStack();

        if (permissions.getValueResetPermission().hasPermission(permissible)) {
            addItem(settingsGuiSettings.getResetValueSlot(), MessageUtil.replaceItemStack(plugin, settingsGuiSettings.getResetValueItemStack()), this::handleResetValues);
        }

        if (permissions.getValueCopyPermission().hasPermission(permissible)) {
            addItem(settingsGuiSettings.getCopyValueSlot(), MessageUtil.replaceItemStack(plugin, settingsGuiSettings.getCopyValueItemStack()),
                    event -> new WorldGui(guiSettings, oreControlValues, new CopySettingAction(guiSettings, oreControlValues,
                            () -> new SettingsGui(guiSettings, oreControlValues, permissible, worldOreConfig, dimension, biome, ore, setting), worldOreConfig, biome, ore, setting)).
                            openSync(event.getWhoClicked()));
        }
    }

    SettingsGui(@NotNull final GuiSettings guiSettings, @NotNull final OreControlValues oreControlValues, @NotNull final Permissible permissible, @NotNull final WorldOreConfig worldOreConfig, @Nullable Dimension dimension, @NotNull final BiomeGroupGui.BiomeGroup biomeGroup, @NotNull final Ore ore, @NotNull final Setting setting) {
        super(oreControlValues.getPlugin(), guiSettings.getSettingsGuiSettings());

        Validate.notNull(permissible, "Permissible cannot be null");
        Validate.notNull(worldOreConfig, "WorldOreConfig cannot be null");
        Validate.notNull(ore, "Ore cannot be null");
        Validate.notNull(setting, "Setting cannot be null");

        this.guiSettings = guiSettings;
        this.oreControlValues = oreControlValues;
        this.worldOreConfig = worldOreConfig;
        this.biome = null;
        this.biomeGroup = biomeGroup;
        this.ore = ore;
        this.setting = setting;
        this.oreSlot = guiSettings.getSettingsGuiSettings().getOreSlot();

        addDecorations();

        final SettingsGuiSettings settingsGuiSettings = guiSettings.getSettingsGuiSettings();
        final Plugin plugin = oreControlValues.getPlugin();

        settingsGuiSettings.getItemStackValues().forEach(value -> addItem(value.getSlot(), MessageUtil.replaceItemStack(plugin, value.getItemStack(), getMessagesValues(true, value.getValue())), new SettingBiomeGroupConsumer(value.getValue())));

        addItem(settingsGuiSettings.getInfoSlot(), MessageUtil.replaceItemStack(plugin, settingsGuiSettings.getInfoBiomeItemStack(), getMessagesValues(true)));
        addItem(settingsGuiSettings.getBackSlot(), MessageUtil.replaceItemStack(plugin, settingsGuiSettings.getBackItemStack()),
                event -> new OreSettingsGui(guiSettings, oreControlValues, event.getWhoClicked(), worldOreConfig, dimension, biomeGroup, ore).openSync(event.getWhoClicked()));

        updateBiomeGroupItemStack(true);
    }

    private MessageValue[] getMessagesValues(final boolean firstUpdate) {
        return new MessageValue[]{new MessageValue("world", worldOreConfig.getName()),
                new MessageValue("biome", biome == null ? biomeGroup == null ? "" : biomeGroup.getName() : biome.toString()),
                new MessageValue("ore", ore.toString()),
                new MessageValue("setting", setting.toString()),
                new MessageValue("amount", String.valueOf(biome == null ? biomeGroup == null ? oreControlValues.getService().getValue(worldOreConfig, ore, setting) : firstUpdate ? "N/A" : current : oreControlValues.getService().getValue(worldOreConfig, biome, ore, setting))),
                new MessageValue("default", String.valueOf(biome == null ? biomeGroup == null ? oreControlValues.getService().getDefaultValue(ore, setting) : "N/A" : oreControlValues.getService().getDefaultValue(biome, ore, setting)))
        };
    }

    private MessageValue[] getMessagesValues(final boolean firstUpdate, double changeValue) {
        return new MessageValue[]{new MessageValue("world", worldOreConfig.getName()),
                new MessageValue("biome", biome == null ? biomeGroup == null ? "" : biomeGroup.getName() : biome.toString()),
                new MessageValue("ore", ore.toString()),
                new MessageValue("setting", setting.toString()),
                new MessageValue("amount", String.valueOf(biome == null ? biomeGroup == null ? oreControlValues.getService().getValue(worldOreConfig, ore, setting) : firstUpdate ? "N/A" : current : oreControlValues.getService().getValue(worldOreConfig, biome, ore, setting))),
                new MessageValue("default", String.valueOf(biome == null ? biomeGroup == null ? oreControlValues.getService().getDefaultValue(ore, setting) : "N/A" : oreControlValues.getService().getDefaultValue(biome, ore, setting))),
                new MessageValue("change-value", Math.abs(changeValue))
        };
    }

    private void updateItemStack() {
        ItemStack itemStack = biome == null ? guiSettings.getSettingsGuiSettings().getDefaultOreItemStack() : guiSettings.getSettingsGuiSettings().getDefaultBiomeOreItemStack();
        itemStack.setType(ore.getMaterial());
        itemStack = MessageUtil.replaceItemStack(getPlugin(), itemStack, getMessagesValues(false));
        addItem(oreSlot, itemStack);

        guiSettings.getSettingsGuiSettings().getItemStackValues().forEach(value -> addItem(value.getSlot(), MessageUtil.replaceItemStack(getPlugin(), value.getItemStack(), getMessagesValues(false, value.getValue()))));
    }

    private void updateBiomeGroupItemStack(final boolean firstUpdate) {
        ItemStack itemStack = guiSettings.getSettingsGuiSettings().getDefaultBiomeOreItemStack();
        itemStack.setType(ore.getMaterial());
        itemStack = MessageUtil.replaceItemStack(getPlugin(), itemStack, getMessagesValues(firstUpdate));
        addItem(oreSlot, itemStack);

        guiSettings.getSettingsGuiSettings().getItemStackValues().forEach(value -> addItem(value.getSlot(), MessageUtil.replaceItemStack(getPlugin(), value.getItemStack(), getMessagesValues(false, value.getValue()))));
    }

    private void handleResetValues(@NotNull final InventoryClickEvent event) {
        if (oreControlValues.getConfigValues().verifyResetAction()) {
            VerifyGui verifyGui = new VerifyGui(getPlugin(), clickEvent -> {
                if (biome != null) {
                    ResetUtil.reset(worldOreConfig, ore, biome, setting);
                } else {
                    ResetUtil.reset(worldOreConfig, ore, setting);
                }

                oreControlValues.getService().saveWorldOreConfig(worldOreConfig);
                openSync(event.getWhoClicked());
                oreControlValues.getOreControlMessages().getGuiResetSuccessMessage().sendMessage(event.getWhoClicked());

                if (biomeGroup == null) {
                    updateItemStack();
                } else {
                    updateBiomeGroupItemStack(false);
                }
            }, clickEvent1 -> openSync(event.getWhoClicked()));

            verifyGui.addDecorations();

            verifyGui.openSync(event.getWhoClicked());
            return;
        }
        if (biome != null) {
            ResetUtil.reset(worldOreConfig, ore, biome, setting);
        } else {
            ResetUtil.reset(worldOreConfig, ore, setting);
        }

        oreControlValues.getService().saveWorldOreConfig(worldOreConfig);
        oreControlValues.getOreControlMessages().getGuiResetSuccessMessage().sendMessage(event.getWhoClicked());
        if (biomeGroup == null) {
            updateItemStack();
        } else {
            updateBiomeGroupItemStack(false);
        }
    }

    private final class SettingConsumer implements Consumer<InventoryClickEvent> {

        private final double value;

        private SettingConsumer(final double value) {
            this.value = value;
        }

        @Override
        public void accept(@NotNull final InventoryClickEvent event) {
            if (event.getClick() != ClickType.LEFT) {
                return;
            }

            final OreControlService service = oreControlValues.getService();
            double current = biome == null ? service.getValue(worldOreConfig, ore, setting) : service.getValue(worldOreConfig, biome, ore, setting);

            double newValue = Double.parseDouble(String.format(Locale.ENGLISH, "%1.2f", current + value));

            if (OreControlUtil.isUnSafe(setting, newValue) || (setting == Setting.VEIN_SIZE && newValue <= 2.001)) {
                if (oreControlValues.getConfigValues().isSafeMode()) {
                    oreControlValues.getOreControlMessages().getNumberNotSafeMessage().sendMessage(event.getWhoClicked(), new MessageValue("value", String.valueOf(newValue)));
                    if ((setting == Setting.VEIN_SIZE && newValue <= 2.001)) {
                        sendSeeAlso(event.getWhoClicked());
                    }
                    return;
                }
                oreControlValues.getOreControlMessages().getNumberNotSafeWarningMessage().sendMessage(event.getWhoClicked(), new MessageValue("value", String.valueOf(newValue)));
                if ((setting == Setting.VEIN_SIZE && newValue <= 2.001)) {
                    sendSeeAlso(event.getWhoClicked());
                }
            }

            if (biome == null) {
                service.setValue(worldOreConfig, ore, setting, newValue);
            } else {
                service.setValue(worldOreConfig, biome, ore, setting, newValue);
            }

            service.saveWorldOreConfig(worldOreConfig);

            updateItemStack();
        }

    }

    private final class SettingBiomeGroupConsumer implements Consumer<InventoryClickEvent> {

        private final double value;

        private SettingBiomeGroupConsumer(double value) {
            this.value = value;
        }

        @Override
        public void accept(@NotNull final InventoryClickEvent event) {
            if (event.getClick() != ClickType.LEFT) {
                return;
            }

            final OreControlService service = oreControlValues.getService();
            double newValue = Double.parseDouble(String.format(Locale.ENGLISH, "%1.2f", current + value));

            if (OreControlUtil.isUnSafe(setting, newValue) || (setting == Setting.VEIN_SIZE && newValue <= 2.001)) {
                if (oreControlValues.getConfigValues().isSafeMode()) {
                    oreControlValues.getOreControlMessages().getNumberNotSafeMessage().sendMessage(event.getWhoClicked(), new MessageValue("value", String.valueOf(newValue)));
                    if ((setting == Setting.VEIN_SIZE && newValue <= 2.001)) {
                        sendSeeAlso(event.getWhoClicked());
                    }
                    return;
                }
                oreControlValues.getOreControlMessages().getNumberNotSafeWarningMessage().sendMessage(event.getWhoClicked(), new MessageValue("value", String.valueOf(newValue)));
                if ((setting == Setting.VEIN_SIZE && newValue <= 2.001)) {
                    sendSeeAlso(event.getWhoClicked());
                }
            }

            current = newValue;

            biomeGroup.getBiomes().stream().filter(biome -> Sets.newHashSet(biome.getOres()).contains(ore)).forEach(biome -> service.setValue(worldOreConfig, biome, ore, setting, current));

            service.saveWorldOreConfig(worldOreConfig);

            updateBiomeGroupItemStack(false);
        }

    }

    private void sendSeeAlso(CommandSender commandSender) {
        BaseComponent[] message = BaseComponentUtil.buildLineWithUrlButton(oreControlValues.getOreControlMessages().getNumberNotSafeSeeAlso().getRawMessage(), "GitHub", "https://github.com/DerFrZocker/Ore-Control/wiki/Vein-Size", oreControlValues.getOreControlMessages());
        commandSender.spigot().sendMessage(message);
    }

}
