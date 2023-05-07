package de.derfrzocker.ore.control.gui.screen.other;

import de.derfrzocker.feature.common.value.target.TargetBlockState;
import de.derfrzocker.ore.control.gui.GuiValuesHolder;
import de.derfrzocker.ore.control.gui.PlayerGuiData;
import de.derfrzocker.ore.control.gui.ScreenUtil;
import de.derfrzocker.ore.control.gui.Screens;
import de.derfrzocker.spigot.utils.gui.InventoryGui;
import de.derfrzocker.spigot.utils.gui.builders.Builders;
import org.bukkit.entity.Player;

public class TargetBlockStateScreen {

    public static InventoryGui getGui(GuiValuesHolder guiValuesHolder) {
        return Builders
                .single()
                .identifier(Screens.OTHER_TARGET_BLOCK_STATE_SCREEN)
                .languageManager(guiValuesHolder.languageManager())
                .withSetting(guiValuesHolder.settingFunction().apply("design.yml"))
                .withSetting(guiValuesHolder.settingFunction().apply("other/target_block_state_screen.yml"))
                .addButtonContext(
                        Builders
                                .buttonContext()
                                .identifier("target")
                                .button(Builders
                                        .button()
                                        .identifier("target")
                                        .withAction(clickAction -> clickAction.getClickEvent().setCancelled(true))
                                        .withAction(clickAction -> guiValuesHolder.guiManager().openRuleTestScreen(clickAction.getPlayer(),
                                                getTargetBlockState(guiValuesHolder, clickAction.getPlayer()).getTarget()))
                                )
                )
                .addButtonContext(
                        Builders
                                .buttonContext()
                                .identifier("state")
                                .button(Builders
                                        .button()
                                        .identifier("state")
                                        // TODO: 5/5/23 Set item stack 
                                        .withAction(clickAction -> clickAction.getClickEvent().setCancelled(true))
                                        .withAction(clickAction -> {/* TODO: 5/5/23 add method to click block */})
                                )
                )
                .withBackAction((setting, guiInfo) -> guiValuesHolder.guiManager().getPlayerGuiData((Player) guiInfo.getEntity()).removeData("target_block_state"))
                .addButtonContext(ScreenUtil.getBackButton(guiValuesHolder.guiManager()))
                .build();
    }

    private static TargetBlockState getTargetBlockState(GuiValuesHolder guiValuesHolder, Player player) {
        PlayerGuiData guiData = guiValuesHolder.guiManager().getPlayerGuiData(player);
        return guiData.getData("target_block_state");
    }
}
