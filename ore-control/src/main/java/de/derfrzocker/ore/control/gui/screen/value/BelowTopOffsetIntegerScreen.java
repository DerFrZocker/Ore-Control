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

package de.derfrzocker.ore.control.gui.screen.value;

import de.derfrzocker.feature.impl.v1_18_R2.value.offset.BelowTopOffsetIntegerValue;
import de.derfrzocker.ore.control.api.OreControlManager;
import de.derfrzocker.ore.control.gui.OreControlGuiManager;
import de.derfrzocker.ore.control.gui.PlayerGuiData;
import de.derfrzocker.spigot.utils.gui.InventoryGui;
import de.derfrzocker.spigot.utils.gui.builders.Builders;
import de.derfrzocker.spigot.utils.language.LanguageManager;
import de.derfrzocker.spigot.utils.setting.ConfigSetting;
import org.bukkit.plugin.Plugin;

import java.util.function.Function;

public class BelowTopOffsetIntegerScreen {

    public static InventoryGui getGui(Plugin plugin, OreControlManager oreControlManager, LanguageManager languageManager, OreControlGuiManager guiManager, Function<String, ConfigSetting> settingFunction) {
        return Builders
                .single()
                .identifier("value.below_top_offset_integer_screen")
                .languageManager(languageManager)
                .withSetting(settingFunction.apply("design.yml"))
                .withSetting(settingFunction.apply("value/below_top_offset_integer_screen.yml"))
                .addButtonContext(Builders
                        .buttonContext()
                        .identifier("base")
                        .button(Builders
                                .button()
                                .identifier("base")
                                .withAction(clickAction -> clickAction.getClickEvent().setCancelled(true))
                                .withAction(clickAction -> {
                                    PlayerGuiData guiData = guiManager.getPlayerGuiData(clickAction.getPlayer());
                                    if ((guiData.getToEditValue() instanceof BelowTopOffsetIntegerValue value)) {
                                        guiData.setToEditValue(value.getBase());

                                        guiManager.openValueScreen(clickAction.getPlayer(), value.getBase());
                                        return;
                                    }

                                    if ((guiData.getToEditValue() instanceof de.derfrzocker.feature.impl.v1_18_R1.value.offset.BelowTopOffsetIntegerValue value)) {
                                        guiData.setToEditValue(value.getBase());

                                        guiManager.openValueScreen(clickAction.getPlayer(), value.getBase());
                                        return;
                                    }

                                    plugin.getLogger().warning(String.format("Expected a value of type '%s' or of type '%s' but got one of type '%s', this is a bug!", BelowTopOffsetIntegerValue.class, de.derfrzocker.feature.impl.v1_18_R1.value.offset.BelowTopOffsetIntegerValue.class, guiData.getToEditValue() != null ? guiData.getToEditValue().getClass() : "null"));
                                })
                        )
                )
                .addButtonContext(Builders
                        .buttonContext()
                        .identifier("back")
                        .button(Builders
                                .button()
                                .identifier("back")
                                .withAction(clickAction -> clickAction.getClickEvent().setCancelled(true))
                                .withAction(clickAction -> {
                                            PlayerGuiData data = guiManager.getPlayerGuiData(clickAction.getPlayer());
                                            data.setPreviousToEditValue();

                                            if (data.getToEditValue() == null) {
                                                data.setOriginalValue(null);
                                                guiManager.openFeatureSettingsScreen(clickAction.getPlayer());
                                            } else {
                                                guiManager.openValueScreen(clickAction.getPlayer(), data.getToEditValue());
                                            }
                                        }
                                )
                        )
                )
                .build();
    }
}
