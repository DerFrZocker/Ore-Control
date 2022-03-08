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

package de.derfrzocker.ore.control.gui.screen.value;

import de.derfrzocker.feature.common.value.number.integer.uniform.UniformIntegerValue;
import de.derfrzocker.ore.control.api.OreControlManager;
import de.derfrzocker.ore.control.gui.OreControlGuiManager;
import de.derfrzocker.ore.control.gui.PlayerGuiData;
import de.derfrzocker.spigot.utils.guin.InventoryGui;
import de.derfrzocker.spigot.utils.guin.builders.ButtonBuilder;
import de.derfrzocker.spigot.utils.guin.builders.ButtonContextBuilder;
import de.derfrzocker.spigot.utils.guin.builders.SingleInventoryGuiBuilder;
import de.derfrzocker.spigot.utils.setting.ConfigSetting;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.function.Function;

public class UniformIntegerScreen {

    public static InventoryGui getGui(Plugin plugin, OreControlManager oreControlManager, OreControlGuiManager guiManager, Function<String, ConfigSetting> settingFunction) {
        return SingleInventoryGuiBuilder
                .builder()
                .identifier("value.uniform_integer_screen")
                .withSetting(settingFunction.apply("design.yml"))
                .withSetting(settingFunction.apply("feature_icons.yml"))
                .withSetting(settingFunction.apply("value/uniform_integer_screen.yml"))
                .addButtonContext(ButtonContextBuilder
                        .builder()
                        .identifier("min-inclusive")
                        .button(ButtonBuilder
                                .builder()
                                .identifier("min-inclusive")
                                .withAction(clickAction -> clickAction.getClickEvent().setCancelled(true))
                                .withAction(clickAction -> {
                                    PlayerGuiData guiData = guiManager.getPlayerGuiData((Player) clickAction.getClickEvent().getWhoClicked());
                                    if (!(guiData.getToEditValue() instanceof UniformIntegerValue value)) {
                                        plugin.getLogger().warning(String.format("Expected a value of type '%s' but got one of type '%s', this is a bug!", UniformIntegerValue.class, guiData.getToEditValue() != null ? guiData.getToEditValue().getClass() : "null"));
                                        return;
                                    }

                                    guiData.setToEditValue(value.getMinInclusive());

                                    guiManager.openValueScreen((Player) clickAction.getClickEvent().getWhoClicked(), value.getMinInclusive());
                                })
                        )
                )
                .addButtonContext(ButtonContextBuilder
                        .builder()
                        .identifier("max-inclusive")
                        .button(ButtonBuilder
                                .builder()
                                .identifier("max-inclusive")
                                .withAction(clickAction -> clickAction.getClickEvent().setCancelled(true))
                                .withAction(clickAction -> {
                                    PlayerGuiData guiData = guiManager.getPlayerGuiData((Player) clickAction.getClickEvent().getWhoClicked());
                                    if (!(guiData.getToEditValue() instanceof UniformIntegerValue value)) {
                                        plugin.getLogger().warning(String.format("Expected a value of type '%s' but got one of type '%s', this is a bug!", UniformIntegerValue.class, guiData.getToEditValue() != null ? guiData.getToEditValue().getClass() : "null"));
                                        return;
                                    }

                                    guiData.setToEditValue(value.getMaxInclusive());

                                    guiManager.openValueScreen((Player) clickAction.getClickEvent().getWhoClicked(), value.getMaxInclusive());
                                })
                        )
                ).build();
    }

}
