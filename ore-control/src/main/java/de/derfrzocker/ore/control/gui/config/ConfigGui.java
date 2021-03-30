/*
 * MIT License
 *
 * Copyright (c) 2019 - 2020 Marvin (DerFrZocker)
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

package de.derfrzocker.ore.control.gui.config;

import de.derfrzocker.ore.control.gui.WorldGui;
import de.derfrzocker.ore.control.gui.settings.BooleanGuiSetting;
import de.derfrzocker.ore.control.gui.settings.ConfigGuiSettings;
import de.derfrzocker.ore.control.gui.settings.GuiSettings;
import de.derfrzocker.ore.control.utils.OreControlValues;
import de.derfrzocker.spigot.utils.ReloadAble;
import de.derfrzocker.spigot.utils.gui.BasicGui;
import de.derfrzocker.spigot.utils.gui.VerifyGui;
import de.derfrzocker.spigot.utils.message.MessageUtil;
import de.derfrzocker.spigot.utils.message.MessageValue;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class ConfigGui extends BasicGui {

    @NotNull
    private final GuiSettings guiSettings;
    @NotNull
    private final OreControlValues oreControlValues;

    public ConfigGui(@NotNull GuiSettings guiSettings, @NotNull final OreControlValues oreControlValues) {
        super(oreControlValues.getPlugin(), guiSettings.getConfigGuiSettings());

        this.guiSettings = guiSettings;
        this.oreControlValues = oreControlValues;

        final ConfigGuiSettings configGuiSettings = guiSettings.getConfigGuiSettings();
        final Plugin plugin = oreControlValues.getPlugin();

        addDecorations();

        addItem(configGuiSettings.getReloadSlot(), MessageUtil.replaceItemStack(plugin, configGuiSettings.getReloadItemStack()), event -> {
            ReloadAble.RELOAD_ABLES.forEach(ReloadAble::reload);
            new ConfigGui(guiSettings, oreControlValues).openSync(event.getWhoClicked());
        });

        addItem(configGuiSettings.getBackSlot(), MessageUtil.replaceItemStack(plugin, configGuiSettings.getBackItemStack()), event -> new WorldGui(guiSettings, oreControlValues, event.getWhoClicked()).openSync(event.getWhoClicked()));

        addItem(configGuiSettings.getLanguageSlot(), MessageUtil.replaceItemStack(plugin, configGuiSettings.getLanguageItemStack(),
                new MessageValue("amount", oreControlValues.getConfigValues().getLanguage().getNames()[0]),
                new MessageValue("default", oreControlValues.getConfigValues().DEFAULT.defaultLanguage().getNames()[0])
        ), event -> new LanguageGui(guiSettings, oreControlValues).openSync(event.getWhoClicked()));

        addItem(configGuiSettings.getsafeModeSlot(), MessageUtil.replaceItemStack(plugin, configGuiSettings.getsafeModeItemStack(),
                new MessageValue("amount", oreControlValues.getConfigValues().isSafeMode()),
                new MessageValue("default", oreControlValues.getConfigValues().DEFAULT.defaultSafeMode())
        ), this::handleSafeMode);

        addItem(configGuiSettings.getTranslateTabCompilationSlot(), MessageUtil.replaceItemStack(plugin, configGuiSettings.getTranslateTabCompilationItemStack(),
                new MessageValue("amount", oreControlValues.getConfigValues().isTranslateTabCompilation()),
                new MessageValue("default", oreControlValues.getConfigValues().DEFAULT.defaultTranslateTabCompilation())
        ), this::handleTranslateTabCompilation);

        addItem(configGuiSettings.getVerifyCopyActionSlot(), MessageUtil.replaceItemStack(plugin, configGuiSettings.getVerifyCopyActionItemStack(),
                new MessageValue("amount", oreControlValues.getConfigValues().verifyCopyAction()),
                new MessageValue("default", oreControlValues.getConfigValues().DEFAULT.defaultVerifyCopyAction())
        ), this::handleVerifyCopyAction);

        addItem(configGuiSettings.getVerifyResetActionSlot(), MessageUtil.replaceItemStack(plugin, configGuiSettings.getVerifyResetActionItemStack(),
                new MessageValue("amount", oreControlValues.getConfigValues().verifyResetAction()),
                new MessageValue("default", oreControlValues.getConfigValues().DEFAULT.defaultVerifyResetAction())
        ), this::handleVerifyResetAction);
    }

    private void handleSafeMode(@NotNull final InventoryClickEvent event) {
        final BooleanGuiSetting booleanGuiSetting = guiSettings.getBooleanGuiSetting();

        final VerifyGui verifyGui = new VerifyGui(getPlugin(), event1 -> {
            oreControlValues.getConfigValues().SET.setSafeMode(true);
            new ConfigGui(guiSettings, oreControlValues).openSync(event.getWhoClicked());
        }, event1 -> {
            oreControlValues.getConfigValues().SET.setSafeMode(false);
            new ConfigGui(guiSettings, oreControlValues).openSync(event.getWhoClicked());
        }, booleanGuiSetting);

        verifyGui.addDecorations();

        verifyGui.addItem(booleanGuiSetting.getBackSlot(), MessageUtil.replaceItemStack(getPlugin(), booleanGuiSetting.getBackItemStack()), click -> new ConfigGui(guiSettings, oreControlValues).openSync(click.getWhoClicked()));

        verifyGui.addItem(booleanGuiSetting.getInfoSlot(), MessageUtil.replaceItemStack(getPlugin(), booleanGuiSetting.getInfoItemStack(),
                new MessageValue("amount", oreControlValues.getConfigValues().isSafeMode()),
                new MessageValue("default", oreControlValues.getConfigValues().DEFAULT.defaultSafeMode())));

        verifyGui.openSync(event.getWhoClicked());
    }

    private void handleTranslateTabCompilation(@NotNull final InventoryClickEvent event) {
        final BooleanGuiSetting booleanGuiSetting = guiSettings.getBooleanGuiSetting();

        final VerifyGui verifyGui = new VerifyGui(getPlugin(), event1 -> {
            oreControlValues.getConfigValues().SET.setTranslateTabCompilation(true);
            new ConfigGui(guiSettings, oreControlValues).openSync(event.getWhoClicked());
        }, event1 -> {
            oreControlValues.getConfigValues().SET.setTranslateTabCompilation(false);
            new ConfigGui(guiSettings, oreControlValues).openSync(event.getWhoClicked());
        }, booleanGuiSetting);

        verifyGui.addDecorations();

        verifyGui.addItem(booleanGuiSetting.getBackSlot(), MessageUtil.replaceItemStack(getPlugin(), booleanGuiSetting.getBackItemStack()), click -> new ConfigGui(guiSettings, oreControlValues).openSync(click.getWhoClicked()));

        verifyGui.addItem(booleanGuiSetting.getInfoSlot(), MessageUtil.replaceItemStack(getPlugin(), booleanGuiSetting.getInfoItemStack(),
                new MessageValue("amount", oreControlValues.getConfigValues().isTranslateTabCompilation()),
                new MessageValue("default", oreControlValues.getConfigValues().DEFAULT.defaultTranslateTabCompilation())));

        verifyGui.openSync(event.getWhoClicked());
    }

    private void handleVerifyCopyAction(@NotNull final InventoryClickEvent event) {
        final BooleanGuiSetting booleanGuiSetting = guiSettings.getBooleanGuiSetting();

        final VerifyGui verifyGui = new VerifyGui(getPlugin(), event1 -> {
            oreControlValues.getConfigValues().SET.setVerifyCopyAction(true);
            new ConfigGui(guiSettings, oreControlValues).openSync(event.getWhoClicked());
        }, event1 -> {
            oreControlValues.getConfigValues().SET.setVerifyCopyAction(false);
            new ConfigGui(guiSettings, oreControlValues).openSync(event.getWhoClicked());
        }, booleanGuiSetting);

        verifyGui.addDecorations();

        verifyGui.addItem(booleanGuiSetting.getBackSlot(), MessageUtil.replaceItemStack(getPlugin(), booleanGuiSetting.getBackItemStack()), click -> new ConfigGui(guiSettings, oreControlValues).openSync(click.getWhoClicked()));

        verifyGui.addItem(booleanGuiSetting.getInfoSlot(), MessageUtil.replaceItemStack(getPlugin(), booleanGuiSetting.getInfoItemStack(),
                new MessageValue("amount", oreControlValues.getConfigValues().verifyCopyAction()),
                new MessageValue("default", oreControlValues.getConfigValues().DEFAULT.defaultVerifyCopyAction())));

        verifyGui.openSync(event.getWhoClicked());
    }

    private void handleVerifyResetAction(@NotNull final InventoryClickEvent event) {
        final BooleanGuiSetting booleanGuiSetting = guiSettings.getBooleanGuiSetting();

        final VerifyGui verifyGui = new VerifyGui(getPlugin(), event1 -> {
            oreControlValues.getConfigValues().SET.setVerifyResetAction(true);
            new ConfigGui(guiSettings, oreControlValues).openSync(event.getWhoClicked());
        }, event1 -> {
            oreControlValues.getConfigValues().SET.setVerifyResetAction(false);
            new ConfigGui(guiSettings, oreControlValues).openSync(event.getWhoClicked());
        }, booleanGuiSetting);

        verifyGui.addDecorations();

        verifyGui.addItem(booleanGuiSetting.getBackSlot(), MessageUtil.replaceItemStack(getPlugin(), booleanGuiSetting.getBackItemStack()), click -> new ConfigGui(guiSettings, oreControlValues).openSync(click.getWhoClicked()));

        verifyGui.addItem(booleanGuiSetting.getInfoSlot(), MessageUtil.replaceItemStack(getPlugin(), booleanGuiSetting.getInfoItemStack(),
                new MessageValue("amount", oreControlValues.getConfigValues().verifyResetAction()),
                new MessageValue("default", oreControlValues.getConfigValues().DEFAULT.defaultVerifyResetAction())));

        verifyGui.openSync(event.getWhoClicked());
    }

}
