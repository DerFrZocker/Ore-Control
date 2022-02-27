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

import de.derfrzocker.ore.control.api.config.ConfigInfo;
import de.derfrzocker.ore.control.api.config.ConfigManager;
import de.derfrzocker.ore.control.gui.OreControlGuiManager;
import de.derfrzocker.spigot.utils.guin.InventoryGui;
import de.derfrzocker.spigot.utils.guin.builders.PageContentBuilder;
import de.derfrzocker.spigot.utils.guin.builders.PagedInventoryGuiBuilder;
import de.derfrzocker.spigot.utils.setting.ConfigSetting;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.function.Function;

public class ConfigInfosScreen {

    private static final String IDENTIFIER = OreControlGuiManager.CONFIG_INFOS_SCREEN;

    public static InventoryGui getGui(OreControlGuiManager guiManager, Function<String, ConfigSetting> settingFunction, ConfigManager configManager) {
        return PagedInventoryGuiBuilder
                .builder()
                .identifier(IDENTIFIER)
                .withSetting(settingFunction.apply("design.yml"))
                .withSetting(settingFunction.apply("config_infos_screen.yml"))
                .addDefaultNextButton()
                .addDefaultPreviousButton()
                .addConfigDecorations()
                .pageContent(PageContentBuilder
                        .builder(ConfigInfo.class)
                        .data((setting, guiInfo) -> new LinkedList<>(configManager.getConfigInfos()))
                        .itemStack((setting, guiInfo, configInfo) -> {
                            return setting.get(IDENTIFIER, "default-icons." + configInfo.getConfigType(), new ItemStack(Material.STONE)).clone();
                        })
                        .withAction((clickAction, configInfo) -> clickAction.getClickEvent().setCancelled(true))
                        .withAction((clickAction, configInfo) -> guiManager.getPlayerGuiData((Player) clickAction.getClickEvent().getWhoClicked()).setConfigInfo(configInfo))
                        .withAction((clickAction, configInfo) -> guiManager.openConfigInfoScreen((Player) clickAction.getClickEvent().getWhoClicked()))
                )
                .build();
    }

}
