package de.derfrzocker.ore.control.gui.screen.value;

import de.derfrzocker.feature.common.value.offset.AboveBottomOffsetIntegerValue;
import de.derfrzocker.feature.common.value.offset.BelowTopOffsetIntegerValue;
import de.derfrzocker.ore.control.gui.GuiValuesHolder;
import de.derfrzocker.ore.control.gui.ScreenUtil;
import de.derfrzocker.ore.control.gui.Screens;
import de.derfrzocker.spigot.utils.gui.InventoryGui;
import de.derfrzocker.spigot.utils.gui.builders.Builders;
import org.bukkit.entity.Player;

public class OffsetIntegerScreens {

    public static InventoryGui getAboveBottomOffsetGui(GuiValuesHolder guiValuesHolder) {
        return Builders
                .single()
                .identifier(Screens.VALUE_ABOVE_BOTTOM_OFFSET_INTEGER_SCREEN)
                .languageManager(guiValuesHolder.languageManager())
                .withSetting(guiValuesHolder.settingFunction().apply("design.yml"))
                .withSetting(guiValuesHolder.settingFunction().apply("value/above_bottom_offset_integer_screen.yml"))
                .addButtonContext(ScreenUtil.getPassthroughButton(guiValuesHolder, "base", AboveBottomOffsetIntegerValue.class, AboveBottomOffsetIntegerValue::getBase))
                .withBackAction((setting, guiInfo) -> guiValuesHolder.guiManager().getPlayerGuiData((Player) guiInfo.getEntity()).setPreviousToEditValue())
                .addButtonContext(ScreenUtil.getBackButton(guiValuesHolder.guiManager()))
                .build();
    }

    public static InventoryGui getBelowTopOffsetGui(GuiValuesHolder guiValuesHolder) {
        return Builders
                .single()
                .identifier(Screens.VALUE_BELOW_TOP_OFFSET_INTEGER_SCREEN)
                .languageManager(guiValuesHolder.languageManager())
                .withSetting(guiValuesHolder.settingFunction().apply("design.yml"))
                .withSetting(guiValuesHolder.settingFunction().apply("value/below_top_offset_integer_screen.yml"))
                .addButtonContext(ScreenUtil.getPassthroughButton(guiValuesHolder, "base", BelowTopOffsetIntegerValue.class, BelowTopOffsetIntegerValue::getBase))
                .withBackAction((setting, guiInfo) -> guiValuesHolder.guiManager().getPlayerGuiData((Player) guiInfo.getEntity()).setPreviousToEditValue())
                .addButtonContext(ScreenUtil.getBackButton(guiValuesHolder.guiManager()))
                .build();
    }
}
