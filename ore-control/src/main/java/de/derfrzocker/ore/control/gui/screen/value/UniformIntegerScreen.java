package de.derfrzocker.ore.control.gui.screen.value;

import de.derfrzocker.feature.common.value.number.integer.uniform.UniformIntegerValue;
import de.derfrzocker.ore.control.gui.GuiValuesHolder;
import de.derfrzocker.ore.control.gui.ScreenUtil;
import de.derfrzocker.ore.control.gui.Screens;
import de.derfrzocker.spigot.utils.gui.InventoryGui;
import de.derfrzocker.spigot.utils.gui.builders.Builders;
import org.bukkit.entity.Player;

public class UniformIntegerScreen {

    public static InventoryGui getGui(GuiValuesHolder guiValuesHolder) {
        return Builders
                .single()
                .identifier(Screens.VALUE_UNIFORM_INTEGER_SCREEN)
                .languageManager(guiValuesHolder.languageManager())
                .withSetting(guiValuesHolder.settingFunction().apply("design.yml"))
                .withSetting(guiValuesHolder.settingFunction().apply("value/uniform_integer_screen.yml"))
                .addButtonContext(ScreenUtil.getPassthroughButton(guiValuesHolder, "min-inclusive", UniformIntegerValue.class, UniformIntegerValue::getMinInclusive))
                .addButtonContext(ScreenUtil.getPassthroughButton(guiValuesHolder, "max-inclusive", UniformIntegerValue.class, UniformIntegerValue::getMaxInclusive))
                .withBackAction((setting, guiInfo) -> guiValuesHolder.guiManager().getPlayerGuiData((Player) guiInfo.getEntity()).setPreviousToEditValue())
                .addButtonContext(ScreenUtil.getBackButton(guiValuesHolder.guiManager()))
                .build();
    }
}
