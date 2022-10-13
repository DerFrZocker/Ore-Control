package de.derfrzocker.ore.control.gui.screen;

import de.derfrzocker.ore.control.gui.GuiValuesHolder;
import de.derfrzocker.ore.control.gui.ScreenUtil;
import de.derfrzocker.spigot.utils.gui.InventoryGui;
import de.derfrzocker.spigot.utils.gui.builders.Builders;

public class ExtraValuesScreen {

    public static InventoryGui getGui(GuiValuesHolder guiValuesHolder) {
        return Builders
                .single()
                .identifier("extra_values")
                .languageManager(guiValuesHolder.languageManager())
                .withSetting(guiValuesHolder.settingFunction().apply("design.yml"))
                .withSetting(guiValuesHolder.settingFunction().apply("extra_values_screen.yml"))
                .addButtonContext(Builders
                        .buttonContext()
                        .identifier("big_ore_veins")
                        .button(
                                Builders
                                        .button()
                                        .identifier("big_ore_veins")
                                        .withAction(clickAction -> clickAction.getClickEvent().setCancelled(true))
                                        .withAction(clickAction -> guiValuesHolder.guiManager().openBigOreVeinsExtraValueScreen(clickAction.getPlayer()))
                        )
                )
                .addButtonContext(ScreenUtil.getBackButton(guiValuesHolder.guiManager()))
                .build();
    }
}
