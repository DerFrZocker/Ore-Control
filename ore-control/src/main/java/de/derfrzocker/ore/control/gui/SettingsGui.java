/*
 * MIT License
 *
 * Copyright (c) 2019 Marvin (DerFrZocker)
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
 */

package de.derfrzocker.ore.control.gui;

import com.google.common.collect.Sets;
import de.derfrzocker.ore.control.Permissions;
import de.derfrzocker.ore.control.api.*;
import de.derfrzocker.ore.control.gui.copy.CopySettingAction;
import de.derfrzocker.ore.control.gui.settings.BiomeGuiSettings;
import de.derfrzocker.ore.control.gui.settings.SettingsGuiSettings;
import de.derfrzocker.ore.control.utils.OreControlUtil;
import de.derfrzocker.ore.control.utils.OreControlValues;
import de.derfrzocker.ore.control.utils.ResetUtil;
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

import java.util.Locale;
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

    private double current = 0;

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
                new MessageValue("amount", String.valueOf(biome == null ? biomeGroup == null ? oreControlValues.getService().getValue(worldOreConfig, ore, setting) : firstUpdate ? "N/A" : current : oreControlValues.getService().getValue(worldOreConfig, biome, ore, setting)))
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
                    ResetUtil.reset(worldOreConfig, ore, biome, setting);
                else
                    ResetUtil.reset(worldOreConfig, ore, setting);

                oreControlValues.getService().saveWorldOreConfig(worldOreConfig);
                openSync(event.getWhoClicked());
                oreControlValues.getOreControlMessages().getGuiResetSuccessMessage().sendMessage(event.getWhoClicked());
            }, clickEvent1 -> openSync(event.getWhoClicked())).openSync(event.getWhoClicked());
            return;
        }
        if (biome != null)
            ResetUtil.reset(worldOreConfig, ore, biome, setting);
        else
            ResetUtil.reset(worldOreConfig, ore, setting);

        oreControlValues.getService().saveWorldOreConfig(worldOreConfig);
        oreControlValues.getOreControlMessages().getGuiResetSuccessMessage().sendMessage(event.getWhoClicked());
    }

    private final class SettingConsumer implements Consumer<InventoryClickEvent> {

        private final double value;

        private SettingConsumer(final double value) {
            this.value = value;
        }

        @Override
        public void accept(@NotNull final InventoryClickEvent event) {
            final OreControlService service = oreControlValues.getService();
            double current = biome == null ? service.getValue(worldOreConfig, ore, setting) : service.getValue(worldOreConfig, biome, ore, setting);

            double newValue = Double.parseDouble(String.format(Locale.ENGLISH, "%1.2f", current + value));

            if (OreControlUtil.isUnSafe(setting, newValue)) {
                if (oreControlValues.getConfigValues().isSafeMode()) {
                    oreControlValues.getOreControlMessages().getNumberNotSafeMessage().sendMessage(event.getWhoClicked(), new MessageValue("value", String.valueOf(newValue)));
                    return;
                }
                oreControlValues.getOreControlMessages().getNumberNotSafeWarningMessage().sendMessage(event.getWhoClicked(), new MessageValue("value", String.valueOf(newValue)));
            }

            if (biome == null)
                service.setValue(worldOreConfig, ore, setting, newValue);
            else
                service.setValue(worldOreConfig, biome, ore, setting, newValue);

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
            final OreControlService service = oreControlValues.getService();
            double newValue = Double.parseDouble(String.format(Locale.ENGLISH, "%1.2f", current + value));

            if (OreControlUtil.isUnSafe(setting, newValue)) {
                if (oreControlValues.getConfigValues().isSafeMode()) {
                    oreControlValues.getOreControlMessages().getNumberNotSafeMessage().sendMessage(event.getWhoClicked(), new MessageValue("value", String.valueOf(newValue)));
                    return;
                }
                oreControlValues.getOreControlMessages().getNumberNotSafeWarningMessage().sendMessage(event.getWhoClicked(), new MessageValue("value", String.valueOf(newValue)));
            }

            current = newValue;

            biomeGroup.getBiomes().stream().filter(biome -> Sets.newHashSet(biome.getOres()).contains(ore)).forEach(biome -> service.setValue(worldOreConfig, biome, ore, setting, current));

            service.saveWorldOreConfig(worldOreConfig);

            updateBiomeGroupItemStack(false);
        }

    }

}
