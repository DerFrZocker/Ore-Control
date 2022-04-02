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

package de.derfrzocker.ore.control.gui.screen;

import de.derfrzocker.ore.control.api.config.ConfigManager;
import de.derfrzocker.ore.control.gui.OreControlGuiManager;
import de.derfrzocker.spigot.utils.gui.InventoryGui;
import de.derfrzocker.spigot.utils.gui.builders.Builders;
import de.derfrzocker.spigot.utils.language.LanguageManager;
import de.derfrzocker.spigot.utils.setting.ConfigSetting;

import java.util.function.Function;

public class ConfigInfoScreen {

    private static final String IDENTIFIER = OreControlGuiManager.CONFIG_INFO_SCREEN;
    private static final String WHOLE_WORLD = "whole-world";

    public static InventoryGui getGui(LanguageManager languageManager, OreControlGuiManager guiManager, Function<String, ConfigSetting> settingFunction, ConfigManager configManager) {
        return Builders
                .single()
                .identifier(IDENTIFIER)
                .languageManager(languageManager)
                .withSetting(settingFunction.apply("design.yml"))
                .withSetting(settingFunction.apply("config_info_screen.yml"))
                .addButtonContext(Builders
                        .buttonContext()
                        .identifier(WHOLE_WORLD)
                        .button(Builders
                                .button()
                                .identifier(WHOLE_WORLD)
                                .withAction(clickAction -> clickAction.getClickEvent().setCancelled(true))
                                .withAction(clickAction -> guiManager.openFeatureSelectionScreen(clickAction.getPlayer()))
                        )
                )
                .addButtonContext(Builders
                        .buttonContext()
                        .identifier("back")
                        .button(Builders
                                .button()
                                .identifier("back")
                                .withAction(clickAction -> clickAction.getClickEvent().setCancelled(true))
                                .withAction(clickAction -> guiManager.openGui(clickAction.getPlayer()))
                        )
                )
                .build();
    }
}
