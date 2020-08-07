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
import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.Dimension;
import de.derfrzocker.ore.control.api.Ore;
import de.derfrzocker.ore.control.api.WorldOreConfig;
import de.derfrzocker.ore.control.gui.copy.CopyAction;
import de.derfrzocker.ore.control.gui.copy.CopyWorldOreConfigAction;
import de.derfrzocker.ore.control.gui.settings.GuiSettings;
import de.derfrzocker.ore.control.gui.settings.WorldConfigGuiSettings;
import de.derfrzocker.ore.control.utils.OreControlValues;
import de.derfrzocker.ore.control.utils.ResetUtil;
import de.derfrzocker.spigot.utils.gui.BasicGui;
import de.derfrzocker.spigot.utils.gui.VerifyGui;
import de.derfrzocker.spigot.utils.message.MessageUtil;
import de.derfrzocker.spigot.utils.message.MessageValue;
import org.apache.commons.lang.Validate;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.permissions.Permissible;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WorldConfigGui extends BasicGui {

    @NotNull
    private final GuiSettings guiSettings;
    @NotNull
    private final OreControlValues oreControlValues;
    @NotNull
    private final WorldOreConfig worldOreConfig;
    @Nullable
    private final CopyAction copyAction;

    WorldConfigGui(@NotNull final GuiSettings guiSettings, @NotNull final OreControlValues oreControlValues, @NotNull final Permissible permissible, @NotNull final WorldOreConfig worldOreConfig, @Nullable final Dimension dimension) {
        super(oreControlValues.getJavaPlugin(), guiSettings.getWorldConfigGuiSettings());

        Validate.notNull(permissible, "Permissible cannot be null");
        Validate.notNull(worldOreConfig, "WorldOreConfig cannot be null");

        this.guiSettings = guiSettings;
        this.oreControlValues = oreControlValues;
        this.worldOreConfig = worldOreConfig;
        this.copyAction = null;

        final WorldConfigGuiSettings worldConfigGuiSettings = guiSettings.getWorldConfigGuiSettings();
        final JavaPlugin javaPlugin = oreControlValues.getJavaPlugin();
        final Permissions permissions = oreControlValues.getPermissions();

        addDecorations();

        if (permissions.getSetValuePermission().hasPermission(permissible)) {
            addItem(worldConfigGuiSettings.getOreItemStackSlot(), MessageUtil.replaceItemStack(javaPlugin, worldConfigGuiSettings.getOreItemStack()), event -> new OreGui(guiSettings, oreControlValues, event.getWhoClicked(), worldOreConfig, dimension, (Biome) null).openSync(event.getWhoClicked()));
        }

        if (permissions.getSetBiomePermission().hasPermission(permissible)) {
            addItem(worldConfigGuiSettings.getBiomeItemStackSlot(), MessageUtil.replaceItemStack(javaPlugin, worldConfigGuiSettings.getBiomeItemStack()), event -> new BiomeGui(guiSettings, oreControlValues, event.getWhoClicked(), worldOreConfig, dimension).openSync(event.getWhoClicked()));
        }

        if (permissions.getValueResetPermission().hasPermission(permissible)) {
            addItem(worldConfigGuiSettings.getResetValueSlot(), MessageUtil.replaceItemStack(javaPlugin, worldConfigGuiSettings.getResetValueItemStack()), this::handleResetValues);
        }

        if (permissions.getValueCopyPermission().hasPermission(permissible)) {
            addItem(worldConfigGuiSettings.getCopyValueSlot(), MessageUtil.replaceItemStack(javaPlugin, worldConfigGuiSettings.getCopyValueItemStack()), event -> new WorldGui(guiSettings, oreControlValues, new CopyWorldOreConfigAction(oreControlValues, worldOreConfig)).openSync(event.getWhoClicked()));
        }

        if (permissions.getTemplateDeletePermission().hasPermission(permissible) && !worldOreConfig.getName().equals("Default")) {
            addItem(worldConfigGuiSettings.getTemplateDeleteSlot(), MessageUtil.replaceItemStack(javaPlugin, worldConfigGuiSettings.getTemplateDeleteItemStack()), this::handleDeleteTemplate);
        }

        addItem(worldConfigGuiSettings.getBackSlot(), MessageUtil.replaceItemStack(javaPlugin, worldConfigGuiSettings.getBackItemStack()), event -> new WorldGui(guiSettings, oreControlValues, event.getWhoClicked()).openSync(event.getWhoClicked()));
        addItem(worldConfigGuiSettings.getInfoSlot(), MessageUtil.replaceItemStack(javaPlugin, worldConfigGuiSettings.getInfoItemStack(), getMessagesValues()));
    }

    public WorldConfigGui(@NotNull final GuiSettings guiSettings, @NotNull final OreControlValues oreControlValues, @NotNull final Permissible permissible, @NotNull final WorldOreConfig worldOreConfig, @NotNull CopyAction copyAction) {
        super(oreControlValues.getJavaPlugin(), guiSettings.getWorldConfigGuiSettings());

        Validate.notNull(permissible, "Permissible cannot be null");
        Validate.notNull(worldOreConfig, "WorldOreConfig cannot be null");
        Validate.notNull(copyAction, "CopyAction cannot be null");

        this.guiSettings = guiSettings;
        this.oreControlValues = oreControlValues;
        this.worldOreConfig = worldOreConfig;
        this.copyAction = copyAction;

        final WorldConfigGuiSettings worldConfigGuiSettings = guiSettings.getWorldConfigGuiSettings();
        final JavaPlugin javaPlugin = oreControlValues.getJavaPlugin();
        final Permissions permissions = oreControlValues.getPermissions();

        addDecorations();

        if (permissions.getSetValuePermission().hasPermission(permissible)) {
            boolean bool = false;

            for (Ore ore : Ore.values()) {
                if (copyAction.shouldSet(ore)) {
                    bool = true;
                    break;
                }
            }

            if (bool) {
                addItem(worldConfigGuiSettings.getOreItemStackSlot(), MessageUtil.replaceItemStack(javaPlugin, worldConfigGuiSettings.getOreItemStack()), this::handleCopyAction);
            }
        }

        if (permissions.getSetBiomePermission().hasPermission(permissible)) {
            boolean bool = false;

            for (final Biome biome : Biome.values()) {
                if (copyAction.shouldSet(biome)) {
                    bool = true;
                    break;
                }
            }


            if (bool) {
                addItem(worldConfigGuiSettings.getBiomeItemStackSlot(), MessageUtil.replaceItemStack(javaPlugin, worldConfigGuiSettings.getBiomeItemStack()), this::handleCopyActionBiome);
            }
        }

        addItem(worldConfigGuiSettings.getInfoSlot(), MessageUtil.replaceItemStack(javaPlugin, worldConfigGuiSettings.getInfoItemStack(), getMessagesValues()));
    }

    private void handleCopyAction(@NotNull final InventoryClickEvent event) {
        copyAction.setChooseBiome(false);
        copyAction.next(event.getWhoClicked(), this);
    }

    private void handleCopyActionBiome(@NotNull final InventoryClickEvent event) {
        copyAction.setChooseBiome(true);
        copyAction.next(event.getWhoClicked(), this);
    }

    private MessageValue[] getMessagesValues() {
        return new MessageValue[]{new MessageValue("world", worldOreConfig.getName())};
    }

    private void handleResetValues(@NotNull final InventoryClickEvent event) {
        if (oreControlValues.getConfigValues().verifyResetAction()) {
            new VerifyGui(getPlugin(), clickEvent -> {
                ResetUtil.reset(this.worldOreConfig);
                oreControlValues.getService().saveWorldOreConfig(worldOreConfig);
                openSync(event.getWhoClicked());
                oreControlValues.getOreControlMessages().getGuiResetSuccessMessage().sendMessage(event.getWhoClicked());
            }, clickEvent1 -> openSync(event.getWhoClicked())).openSync(event.getWhoClicked());
            return;
        }

        ResetUtil.reset(worldOreConfig);
        oreControlValues.getService().saveWorldOreConfig(worldOreConfig);
        oreControlValues.getOreControlMessages().getGuiResetSuccessMessage().sendMessage(event.getWhoClicked());
    }

    private void handleDeleteTemplate(@NotNull final InventoryClickEvent event) {
        new VerifyGui(getPlugin(), clickEvent -> {
            oreControlValues.getService().removeWorldOreConfig(worldOreConfig);
            closeSync(event.getWhoClicked());
        }, clickEvent1 -> openSync(event.getWhoClicked())).openSync(event.getWhoClicked());
    }

}
