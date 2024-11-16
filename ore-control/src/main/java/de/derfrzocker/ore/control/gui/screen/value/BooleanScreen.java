package de.derfrzocker.ore.control.gui.screen.value;

import de.derfrzocker.feature.common.value.bool.FixedBooleanValue;
import de.derfrzocker.ore.control.gui.GuiValuesHolder;
import de.derfrzocker.ore.control.gui.PlayerGuiData;
import de.derfrzocker.ore.control.gui.ScreenUtil;
import de.derfrzocker.ore.control.gui.Screens;
import de.derfrzocker.spigot.utils.gui.InventoryGui;
import de.derfrzocker.spigot.utils.gui.builders.Builders;
import de.derfrzocker.spigot.utils.gui.builders.ButtonContextBuilder;
import org.bukkit.entity.Player;

import java.util.Locale;

public class BooleanScreen {

    public static InventoryGui getGui(GuiValuesHolder guiValuesHolder) {
        return Builders
                .single()
                .identifier(Screens.VALUE_BOOLEAN_SCREEN)
                .languageManager(guiValuesHolder.languageManager())
                .withSetting(guiValuesHolder.settingFunction().apply("design.yml"))
                .withSetting(guiValuesHolder.settingFunction().apply("value/boolean_screen.yml"))
                .addButtonContext(getButton(guiValuesHolder, true))
                .addButtonContext(getButton(guiValuesHolder, false))
                .withBackAction((setting, guiInfo) -> guiValuesHolder.guiManager().getPlayerGuiData((Player) guiInfo.getEntity()).setPreviousToEditValue())
                .addButtonContext(ScreenUtil.getBackButton(guiValuesHolder.guiManager()))
                .build();
    }

    private static ButtonContextBuilder getButton(GuiValuesHolder guiValuesHolder, boolean newBoolValue) {
        return Builders
                .buttonContext()
                .identifier(String.valueOf(newBoolValue).toLowerCase(Locale.ROOT))
                .button(Builders
                        .button()
                        .identifier(String.valueOf(newBoolValue).toLowerCase(Locale.ROOT))
                        .withAction(clickAction -> clickAction.getClickEvent().setCancelled(true))
                        .withAction(clickAction -> {
                            PlayerGuiData guiData = guiValuesHolder.guiManager().getPlayerGuiData(clickAction.getPlayer());
                            if (!(guiData.getToEditValue() instanceof FixedBooleanValue value)) {
                                guiValuesHolder.plugin().getLogger().warning(String.format("Expected a value of type '%s' but got one of type '%s', this is a bug!", FixedBooleanValue.class, guiData.getToEditValue() != null ? guiData.getToEditValue().getClass() : "null"));
                                return;
                            }

                            value.setValue(newBoolValue);
                        })
                        .withAction(clickAction -> guiValuesHolder.guiManager().getPlayerGuiData(clickAction.getPlayer()).apply(guiValuesHolder.plugin(), guiValuesHolder.oreControlManager()))
                );
    }
}
