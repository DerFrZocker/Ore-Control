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
