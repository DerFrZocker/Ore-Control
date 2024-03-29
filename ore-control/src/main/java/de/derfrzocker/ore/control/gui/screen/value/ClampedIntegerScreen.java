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

import de.derfrzocker.feature.common.value.number.integer.clamped.ClampedIntegerValue;
import de.derfrzocker.feature.common.value.number.integer.clamped.ClampedNormalIntegerValue;
import de.derfrzocker.ore.control.gui.GuiValuesHolder;
import de.derfrzocker.ore.control.gui.ScreenUtil;
import de.derfrzocker.ore.control.gui.Screens;
import de.derfrzocker.spigot.utils.gui.InventoryGui;
import de.derfrzocker.spigot.utils.gui.builders.Builders;
import org.bukkit.entity.Player;

public class ClampedIntegerScreen {

    public static InventoryGui getClampedIntegerScreen(GuiValuesHolder guiValuesHolder) {
        return Builders
                .single()
                .identifier(Screens.VALUE_CLAMPED_INTEGER_SCREEN)
                .languageManager(guiValuesHolder.languageManager())
                .withSetting(guiValuesHolder.settingFunction().apply("design.yml"))
                .withSetting(guiValuesHolder.settingFunction().apply("value/clamped_integer_screen.yml"))
                .addButtonContext(ScreenUtil.getPassthroughButton(guiValuesHolder, "source", ClampedIntegerValue.class, ClampedIntegerValue::getSource))
                .addButtonContext(ScreenUtil.getPassthroughButton(guiValuesHolder, "min-inclusive", ClampedIntegerValue.class, ClampedIntegerValue::getMinInclusive))
                .addButtonContext(ScreenUtil.getPassthroughButton(guiValuesHolder, "max-inclusive", ClampedIntegerValue.class, ClampedIntegerValue::getMaxInclusive))
                .withBackAction((setting, guiInfo) -> guiValuesHolder.guiManager().getPlayerGuiData((Player) guiInfo.getEntity()).setPreviousToEditValue())
                .addButtonContext(ScreenUtil.getBackButton(guiValuesHolder.guiManager()))
                .build();
    }

    public static InventoryGui getClampedNormalIntegerGui(GuiValuesHolder guiValuesHolder) {
        return Builders
                .single()
                .identifier(Screens.VALUE_CLAMPED_NORMAL_INTEGER_SCREEN)
                .languageManager(guiValuesHolder.languageManager())
                .withSetting(guiValuesHolder.settingFunction().apply("design.yml"))
                .withSetting(guiValuesHolder.settingFunction().apply("value/clamped_normal_integer_screen.yml"))
                .addButtonContext(ScreenUtil.getPassthroughButton(guiValuesHolder, "mean", ClampedNormalIntegerValue.class, ClampedNormalIntegerValue::getMean))
                .addButtonContext(ScreenUtil.getPassthroughButton(guiValuesHolder, "deviation", ClampedNormalIntegerValue.class, ClampedNormalIntegerValue::getDeviation))
                .addButtonContext(ScreenUtil.getPassthroughButton(guiValuesHolder, "min-inclusive", ClampedNormalIntegerValue.class, ClampedNormalIntegerValue::getMinInclusive))
                .addButtonContext(ScreenUtil.getPassthroughButton(guiValuesHolder, "max-inclusive", ClampedNormalIntegerValue.class, ClampedNormalIntegerValue::getMaxInclusive))
                .withBackAction((setting, guiInfo) -> guiValuesHolder.guiManager().getPlayerGuiData((Player) guiInfo.getEntity()).setPreviousToEditValue())
                .addButtonContext(ScreenUtil.getBackButton(guiValuesHolder.guiManager()))
                .build();
    }
}
