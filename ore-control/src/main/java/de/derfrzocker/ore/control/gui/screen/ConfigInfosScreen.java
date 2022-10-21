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

import de.derfrzocker.ore.control.api.config.ConfigInfo;
import de.derfrzocker.ore.control.gui.GuiValuesHolder;
import de.derfrzocker.ore.control.gui.Screens;
import de.derfrzocker.spigot.utils.gui.InventoryGui;
import de.derfrzocker.spigot.utils.gui.builders.Builders;
import de.derfrzocker.spigot.utils.message.MessageValue;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ConfigInfosScreen {

    public static InventoryGui getGui(GuiValuesHolder guiValuesHolder) {
        return Builders
                .paged()
                .identifier(Screens.CONFIG_INFOS_SCREEN)
                .languageManager(guiValuesHolder.languageManager())
                .withSetting(guiValuesHolder.settingFunction().apply("design.yml"))
                .withSetting(guiValuesHolder.settingFunction().apply("config_infos_screen.yml"))
                .addDefaultNextButton()
                .addDefaultPreviousButton()
                .pageContent(Builders
                        .pageContent(ConfigInfo.class)
                        .data((setting, guiInfo) -> {
                            List<ConfigInfo> data = new ArrayList<>(guiValuesHolder.configManager().getConfigInfos());
                            data.sort(null);
                            return data;
                        })
                        .withMessageValue((setting, guiInfo, configInfo) -> new MessageValue("world-name", configInfo.getWorldName()))
                        .itemStack((setting, guiInfo, configInfo) -> setting.get(Screens.CONFIG_INFOS_SCREEN, "default-icons." + configInfo.getConfigType(), new ItemStack(Material.STONE)).clone())
                        .withAction((clickAction, configInfo) -> clickAction.getClickEvent().setCancelled(true))
                        .withAction((clickAction, configInfo) -> guiValuesHolder.guiManager().getPlayerGuiData(clickAction.getPlayer()).setConfigInfo(configInfo))
                        .withAction((clickAction, configInfo) -> guiValuesHolder.guiManager().openScreen(Screens.CONFIG_INFO_SCREEN, clickAction.getPlayer()))
                )
                .addButtonContext(Builders
                        .buttonContext()
                        .identifier("language")
                        .button(Builders
                                .button()
                                .identifier("language")
                                .withAction(clickAction -> clickAction.getClickEvent().setCancelled(true))
                                .withAction(clickAction -> guiValuesHolder.guiManager().openScreen(Screens.LANGUAGE_SCREEN, clickAction.getPlayer()))
                        )
                )
                .build();
    }
}
