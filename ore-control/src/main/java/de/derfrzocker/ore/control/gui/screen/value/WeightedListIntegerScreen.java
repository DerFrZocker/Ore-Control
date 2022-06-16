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

import de.derfrzocker.feature.common.value.number.IntegerValue;
import de.derfrzocker.feature.common.value.number.integer.weighted.WeightedListIntegerValue;
import de.derfrzocker.ore.control.gui.GuiValuesHolder;
import de.derfrzocker.ore.control.gui.PlayerGuiData;
import de.derfrzocker.spigot.utils.gui.GuiInfo;
import de.derfrzocker.spigot.utils.gui.InventoryGui;
import de.derfrzocker.spigot.utils.gui.builders.Builders;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class WeightedListIntegerScreen {

    public static InventoryGui getGui(GuiValuesHolder guiValuesHolder) {
        InventoryGui subGui = getSubGui(guiValuesHolder);
        return Builders
                .paged()
                .identifier("value.weighted_list_sub_integer_screen")
                .languageManager(guiValuesHolder.languageManager())
                .withSetting(guiValuesHolder.settingFunction().apply("design.yml"))
                .withSetting(guiValuesHolder.settingFunction().apply("value/weighted_list_integer_screen.yml"))
                .addDefaultNextButton()
                .addDefaultPreviousButton()
                .pageContent(Builders
                        .pageContent(DistributionHolder.class)
                        .data((setting, guiInfo) -> getData(guiValuesHolder, guiInfo))
                        .itemStack((setting, guiInfo, distributionHolder) -> setting.get("value.weighted_list_sub_integer_screen", "default-icon", new ItemStack(Material.STONE)).clone())
                        .withAction((clickAction, distributionHolder) -> clickAction.getClickEvent().setCancelled(true))
                        .withAction((clickAction, distributionHolder) -> guiValuesHolder.guiManager().getPlayerGuiData(clickAction.getPlayer()).addData("weighted_list_integer_data", distributionHolder))
                        .withAction((clickAction, distributionHolder) -> guiValuesHolder.guiManager().openGui(subGui, clickAction.getPlayer()))
                )
                .withBackAction((setting, guiInfo) -> guiValuesHolder.guiManager().getPlayerGuiData((Player) guiInfo.getEntity()).setPreviousToEditValue())
                .addButtonContext(guiValuesHolder.guiManager().getBackButton())
                .build();
    }

    private static InventoryGui getSubGui(GuiValuesHolder guiValuesHolder) {
        return Builders
                .single()
                .identifier("value.weighted_list_sub_integer_screen")
                .languageManager(guiValuesHolder.languageManager())
                .withSetting(guiValuesHolder.settingFunction().apply("design.yml"))
                .withSetting(guiValuesHolder.settingFunction().apply("value/weighted_list_sub_integer_screen.yml"))
                .addButtonContext(ValueUtil.getPassthroughButton(guiValuesHolder, "data", "weighted_list_integer_data", DistributionHolder.class, DistributionHolder::data))
                .addButtonContext(ValueUtil.getPassthroughButton(guiValuesHolder, "weight", "weighted_list_integer_data", DistributionHolder.class, DistributionHolder::weight))
                .withBackAction((setting, guiInfo) -> guiValuesHolder.guiManager().getPlayerGuiData((Player) guiInfo.getEntity()).removeData("weighted_list_integer_data"))
                .addButtonContext(guiValuesHolder.guiManager().getBackButton())
                .build();
    }

    private static List<DistributionHolder> getData(GuiValuesHolder guiValuesHolder, GuiInfo guiInfo) {
        PlayerGuiData playerGuiData = guiValuesHolder.guiManager().getPlayerGuiData((Player) guiInfo.getEntity());
        if (!(playerGuiData.getToEditValue() instanceof WeightedListIntegerValue value)) {
            guiValuesHolder.plugin().getLogger().warning(String.format("Expected a value of type '%s' but got one of type '%s', this is a bug!", WeightedListIntegerValue.class, playerGuiData.getToEditValue() != null ? playerGuiData.getToEditValue().getClass() : "null"));
            return Collections.emptyList();
        }

        List<DistributionHolder> list = new LinkedList<>();
        for (Map.Entry<IntegerValue, IntegerValue> entry : value.getDistribution().entrySet()) {
            list.add(new DistributionHolder(entry.getKey(), entry.getValue()));
        }

        return list;
    }

    private record DistributionHolder(IntegerValue data, IntegerValue weight) {
    }
}