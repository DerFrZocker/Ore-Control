/*
 * MIT License
 *
 * Copyright (c) 2019 - 2022 Marvin (DerFrZocker)
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

import de.derfrzocker.ore.control.gui.GuiValuesHolder;
import de.derfrzocker.ore.control.gui.ScreenUtil;
import de.derfrzocker.ore.control.gui.Screens;
import de.derfrzocker.spigot.utils.gui.InventoryGui;
import de.derfrzocker.spigot.utils.gui.builders.Builders;

public class ConfigInfoScreen {

    public static InventoryGui getGui(GuiValuesHolder guiValuesHolder) {
        return Builders
                .single()
                .identifier(Screens.CONFIG_INFO_SCREEN)
                .languageManager(guiValuesHolder.languageManager())
                .withSetting(guiValuesHolder.settingFunction().apply("design.yml"))
                .withSetting(guiValuesHolder.settingFunction().apply("config_info_screen.yml"))
                .addButtonContext(Builders
                        .buttonContext()
                        .identifier("whole-world")
                        .button(Builders
                                .button()
                                .identifier("whole-world")
                                .withAction(clickAction -> clickAction.getClickEvent().setCancelled(true))
                                .withAction(clickAction -> guiValuesHolder.guiManager().openScreen(Screens.FEATURE_SELECTION_SCREEN, clickAction.getPlayer()))
                        )
                )
                .addButtonContext(Builders
                        .buttonContext()
                        .identifier("biome")
                        .button(Builders
                                .button()
                                .identifier("biome")
                                .withAction(clickAction -> clickAction.getClickEvent().setCancelled(true))
                                .withAction(clickAction -> guiValuesHolder.guiManager().openScreen(Screens.BIOME_SCREEN, clickAction.getPlayer()))
                        )
                )
                .addButtonContext(Builders
                        .buttonContext()
                        .identifier("extra-values")
                        .button(Builders
                                .button()
                                .identifier("extra-values")
                                .withAction(clickAction -> clickAction.getClickEvent().setCancelled(true))
                                .withAction(clickAction -> guiValuesHolder.guiManager().openScreen(Screens.EXTRA_VALUES_SCREEN, clickAction.getPlayer()))
                        )
                )
                .addButtonContext(ScreenUtil.getBackButton(guiValuesHolder.guiManager()))
                .build();
    }
}
