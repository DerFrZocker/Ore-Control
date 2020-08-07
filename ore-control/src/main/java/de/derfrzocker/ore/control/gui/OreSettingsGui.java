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

import de.derfrzocker.ore.control.Permissions;
import de.derfrzocker.ore.control.api.*;
import de.derfrzocker.ore.control.gui.copy.CopyAction;
import de.derfrzocker.ore.control.gui.copy.CopyOreAction;
import de.derfrzocker.ore.control.gui.settings.GuiSettings;
import de.derfrzocker.ore.control.gui.settings.OreSettingsGuiSettings;
import de.derfrzocker.ore.control.utils.OreControlValues;
import de.derfrzocker.ore.control.utils.ResetUtil;
import de.derfrzocker.spigot.utils.gui.PageGui;
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

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;

public class OreSettingsGui extends PageGui<Setting> {

    @NotNull
    private final GuiSettings guiSettings;
    @NotNull
    private final OreControlValues oreControlValues;
    @NotNull
    private final WorldOreConfig worldOreConfig;
    @Nullable
    private final Dimension dimension;
    @Nullable
    private final Biome biome;
    @Nullable
    private final BiomeGroupGui.BiomeGroup biomeGroup;
    @NotNull
    private final Ore ore;
    @Nullable
    private final CopyAction copyAction;
    private final int statusSlot;
    private boolean activated;


    OreSettingsGui(@NotNull final GuiSettings guiSettings, @NotNull final OreControlValues oreControlValues, @NotNull final Permissible permissible, @NotNull final WorldOreConfig worldOreConfig, @Nullable final Dimension dimension, @Nullable final Biome biome, @NotNull final Ore ore) {
        super(oreControlValues.getJavaPlugin(), guiSettings.getOreSettingsGuiSettings());

        Validate.notNull(permissible, "Permissible cannot be null");
        Validate.notNull(worldOreConfig, "WorldOreConfig cannot be null");
        Validate.notNull(ore, "Ore cannot be null");

        this.guiSettings = guiSettings;
        this.oreControlValues = oreControlValues;
        this.worldOreConfig = worldOreConfig;
        this.dimension = dimension;
        this.biome = biome;
        this.biomeGroup = null;
        this.ore = ore;
        this.copyAction = null;
        this.statusSlot = guiSettings.getOreSettingsGuiSettings().getStatusSlot();
        this.activated = biome == null ? oreControlValues.getService().isActivated(worldOreConfig, ore) : oreControlValues.getService().isActivated(worldOreConfig, biome, ore);

        addDecorations();

        final OreSettingsGuiSettings oreSettingsGuiSettings = guiSettings.getOreSettingsGuiSettings();
        final JavaPlugin javaPlugin = oreControlValues.getJavaPlugin();
        final Permissions permissions = oreControlValues.getPermissions();
        final Setting[] settings = ore.getSettings();

        init(settings, Setting[]::new, this::getSettingItemStack, (setting, event) -> {
            new SettingsGui(guiSettings, oreControlValues, event.getWhoClicked(), worldOreConfig, dimension, biome, ore, setting).openSync(event.getWhoClicked());
        });

        addItem(oreSettingsGuiSettings.getBackSlot(), MessageUtil.replaceItemStack(javaPlugin, oreSettingsGuiSettings.getBackItemStack()),
                event -> new OreGui(guiSettings, oreControlValues, event.getWhoClicked(), worldOreConfig, dimension, biome).openSync(event.getWhoClicked()));

        addItem(oreSettingsGuiSettings.getInfoSlot(), MessageUtil.replaceItemStack(javaPlugin, biome == null ? oreSettingsGuiSettings.getInfoItemStack() : oreSettingsGuiSettings.getInfoBiomeItemStack(), getMessagesValues()));

        addItem(statusSlot, MessageUtil.replaceItemStack(javaPlugin, activated ? oreSettingsGuiSettings.getDeactivateItemStack() : oreSettingsGuiSettings.getActivateItemStack()), event -> handleStatusUpdate());

        if (permissions.getValueResetPermission().hasPermission(permissible)) {
            addItem(oreSettingsGuiSettings.getResetValueSlot(), MessageUtil.replaceItemStack(javaPlugin, oreSettingsGuiSettings.getResetValueItemStack()), this::handleResetValues);
        }

        if (permissions.getValueCopyPermission().hasPermission(permissible)) {
            addItem(oreSettingsGuiSettings.getCopyValueSlot(), MessageUtil.replaceItemStack(javaPlugin, oreSettingsGuiSettings.getCopyValueItemStack()), event -> new WorldGui(guiSettings, oreControlValues, new CopyOreAction(guiSettings, oreControlValues, worldOreConfig, biome, ore)).openSync(event.getWhoClicked()));
        }
    }

    public OreSettingsGui(@NotNull final GuiSettings guiSettings, @NotNull final OreControlValues oreControlValues, @NotNull final Permissible permissible, @NotNull final WorldOreConfig worldOreConfig, @Nullable final Biome biome, @NotNull final Ore ore, @NotNull final CopyAction copyAction) {
        super(oreControlValues.getJavaPlugin(), guiSettings.getOreSettingsGuiSettings());

        Validate.notNull(permissible, "Permissible cannot be null");
        Validate.notNull(worldOreConfig, "WorldOreConfig cannot be null");
        Validate.notNull(ore, "Ore cannot be null");
        Validate.notNull(copyAction, "CopyAction cannot be null");

        this.guiSettings = guiSettings;
        this.oreControlValues = oreControlValues;
        this.worldOreConfig = worldOreConfig;
        this.dimension = null;
        this.biome = biome;
        this.biomeGroup = null;
        this.ore = ore;
        this.copyAction = copyAction;
        this.statusSlot = -1;

        addDecorations();

        final OreSettingsGuiSettings oreSettingsGuiSettings = guiSettings.getOreSettingsGuiSettings();
        final JavaPlugin javaPlugin = oreControlValues.getJavaPlugin();
        final Set<Setting> settingSet = new LinkedHashSet<>();

        for (final Setting setting : ore.getSettings()) {
            if (copyAction.shouldSet(setting)) {
                settingSet.add(setting);
            }
        }

        final Setting[] settings = settingSet.toArray(new Setting[0]);

        init(settings, Setting[]::new, this::getSettingItemStack, (setting, event) -> {
            copyAction.setSettingTarget(setting);

            copyAction.next(event.getWhoClicked(), OreSettingsGui.this);
        });

        addItem(oreSettingsGuiSettings.getInfoSlot(), MessageUtil.replaceItemStack(javaPlugin, biome == null ? oreSettingsGuiSettings.getInfoItemStack() : oreSettingsGuiSettings.getInfoBiomeItemStack(), getMessagesValues()));
    }

    OreSettingsGui(@NotNull final GuiSettings guiSettings, @NotNull final OreControlValues oreControlValues, @NotNull final Permissible permissible, @NotNull final WorldOreConfig worldOreConfig, @Nullable final Dimension dimension, @NotNull final BiomeGroupGui.BiomeGroup biomeGroup, @NotNull final Ore ore) {
        super(oreControlValues.getJavaPlugin(), guiSettings.getOreSettingsGuiSettings());

        Validate.notNull(permissible, "Permissible cannot be null");
        Validate.notNull(worldOreConfig, "WorldOreConfig cannot be null");
        Validate.notNull(biomeGroup, "BiomeGroup cannot be null");
        Validate.notNull(ore, "Ore cannot be null");

        this.guiSettings = guiSettings;
        this.oreControlValues = oreControlValues;
        this.worldOreConfig = worldOreConfig;
        this.dimension = dimension;
        this.biome = null;
        this.biomeGroup = biomeGroup;
        this.ore = ore;
        this.copyAction = null;
        this.statusSlot = guiSettings.getOreSettingsGuiSettings().getStatusSlot();
        this.activated = true;

        addDecorations();

        final OreSettingsGuiSettings oreSettingsGuiSettings = guiSettings.getOreSettingsGuiSettings();
        final JavaPlugin javaPlugin = oreControlValues.getJavaPlugin();
        final Setting[] settings = ore.getSettings();

        init(settings, Setting[]::new, this::getSettingItemStack, (setting, event) -> {
            new SettingsGui(guiSettings, oreControlValues, event.getWhoClicked(), worldOreConfig, dimension, biomeGroup, ore, setting).openSync(event.getWhoClicked());
        });

        addItem(oreSettingsGuiSettings.getInfoSlot(), MessageUtil.replaceItemStack(javaPlugin, oreSettingsGuiSettings.getInfoBiomeItemStack(), getMessagesValues()));

        addItem(statusSlot, MessageUtil.replaceItemStack(javaPlugin, oreSettingsGuiSettings.getDeactivateItemStack()), event -> handleBiomeGroupStatusUpdate());
        addItem(oreSettingsGuiSettings.getBackSlot(), MessageUtil.replaceItemStack(javaPlugin, oreSettingsGuiSettings.getBackItemStack()),
                event -> new OreGui(guiSettings, oreControlValues, event.getWhoClicked(), worldOreConfig, dimension, biomeGroup).openSync(event.getWhoClicked()));
    }

    private ItemStack getSettingItemStack(@NotNull final Setting setting) {
        Validate.notNull(setting, "Setting cannot be null");

        final OreControlService service = oreControlValues.getService();
        final ItemStack itemStack;

        if (biome == null) {
            itemStack = MessageUtil.replaceItemStack(getPlugin(), this.guiSettings.getOreSettingsGuiSettings().getSettingsItemStack(setting), new MessageValue("amount", String.valueOf(service.getValue(worldOreConfig, ore, setting))));
        } else {
            itemStack = MessageUtil.replaceItemStack(getPlugin(), this.guiSettings.getOreSettingsGuiSettings().getSettingsItemStack(setting), new MessageValue("amount", String.valueOf(service.getValue(worldOreConfig, biome, ore, setting))));
        }

        return itemStack;
    }

    private MessageValue[] getMessagesValues() {
        return new MessageValue[]{new MessageValue("world", worldOreConfig.getName()),
                new MessageValue("biome", biome == null ? biomeGroup == null ? "" : biomeGroup.getName() : biome.toString()),
                new MessageValue("ore", ore.toString())};
    }

    private void handleStatusUpdate() {
        final OreControlService service = oreControlValues.getService();

        activated = !activated;

        if (biome == null) {
            service.setActivated(worldOreConfig, ore, activated);
        } else {
            service.setActivated(worldOreConfig, biome, ore, activated);
        }

        addItem(statusSlot, MessageUtil.replaceItemStack(getPlugin(), activated ? this.guiSettings.getOreSettingsGuiSettings().getDeactivateItemStack() : this.guiSettings.getOreSettingsGuiSettings().getActivateItemStack()));

        service.saveWorldOreConfig(worldOreConfig);
    }

    private void handleBiomeGroupStatusUpdate() {
        final OreControlService service = oreControlValues.getService();

        activated = !activated;

        biomeGroup.getBiomes().forEach(biome -> service.setActivated(worldOreConfig, biome, ore, activated));

        addItem(statusSlot, MessageUtil.replaceItemStack(getPlugin(), activated ? this.guiSettings.getOreSettingsGuiSettings().getDeactivateItemStack() : this.guiSettings.getOreSettingsGuiSettings().getActivateItemStack()));

        service.saveWorldOreConfig(worldOreConfig);
    }

    private void handleResetValues(@NotNull final InventoryClickEvent event) {
        Validate.notNull(event, "InventoryClickEvent cannot be null");

        final OreControlService service = oreControlValues.getService();

        if (oreControlValues.getConfigValues().verifyResetAction()) {
            new VerifyGui(getPlugin(), clickEvent -> {
                if (biome != null) {
                    ResetUtil.reset(worldOreConfig, ore, biome);
                } else {
                    ResetUtil.reset(worldOreConfig, ore);
                }

                service.saveWorldOreConfig(worldOreConfig);
                activated = biome == null ? service.isActivated(worldOreConfig, ore) : service.isActivated(worldOreConfig, biome, ore);
                addItem(statusSlot, MessageUtil.replaceItemStack(getPlugin(), activated ? this.guiSettings.getOreSettingsGuiSettings().getDeactivateItemStack() : this.guiSettings.getOreSettingsGuiSettings().getActivateItemStack()));
                openSync(event.getWhoClicked());
                oreControlValues.getOreControlMessages().getGuiResetSuccessMessage().sendMessage(event.getWhoClicked());
            }, clickEvent1 -> openSync(event.getWhoClicked())).openSync(event.getWhoClicked());
            return;
        }
        if (biome != null) {
            ResetUtil.reset(worldOreConfig, ore, biome);
        } else {
            ResetUtil.reset(worldOreConfig, ore);
        }

        service.saveWorldOreConfig(worldOreConfig);
        activated = biome == null ? service.isActivated(worldOreConfig, ore) : service.isActivated(worldOreConfig, biome, ore);
        addItem(statusSlot, MessageUtil.replaceItemStack(getPlugin(), activated ? this.guiSettings.getOreSettingsGuiSettings().getDeactivateItemStack() : this.guiSettings.getOreSettingsGuiSettings().getActivateItemStack()));
        oreControlValues.getOreControlMessages().getGuiResetSuccessMessage().sendMessage(event.getWhoClicked());
    }

    private final class SettingConsumer implements Consumer<InventoryClickEvent> {

        @NotNull
        private final Setting setting;

        private SettingConsumer(@NotNull final Setting setting) {
            this.setting = setting;
        }

        @Override
        public void accept(@NotNull final InventoryClickEvent event) {
            Validate.notNull(event, "InventoryClickEvent cannot be null");

            new SettingsGui(guiSettings, oreControlValues, event.getWhoClicked(), worldOreConfig, dimension, biome, ore, setting).openSync(event.getWhoClicked());
        }
    }

    private final class SettingCopyConsumer implements Consumer<InventoryClickEvent> {

        @NotNull
        private final Setting setting;

        private SettingCopyConsumer(@NotNull final Setting setting) {
            this.setting = setting;
        }

        @Override
        public void accept(@NotNull final InventoryClickEvent event) {
            copyAction.setSettingTarget(setting);

            copyAction.next(event.getWhoClicked(), OreSettingsGui.this);
        }

    }

    private final class SettingBiomeGroupConsumer implements Consumer<InventoryClickEvent> {

        @NotNull
        private final Setting setting;

        private SettingBiomeGroupConsumer(@NotNull final Setting setting) {
            this.setting = setting;
        }

        @Override
        public void accept(@NotNull final InventoryClickEvent event) {
            new SettingsGui(guiSettings, oreControlValues, event.getWhoClicked(), worldOreConfig, dimension, biomeGroup, ore, setting).openSync(event.getWhoClicked());
        }

    }

}
