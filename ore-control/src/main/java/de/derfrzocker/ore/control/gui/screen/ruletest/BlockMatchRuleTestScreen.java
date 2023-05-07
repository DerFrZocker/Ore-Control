package de.derfrzocker.ore.control.gui.screen.ruletest;

import de.derfrzocker.feature.common.ruletest.BlockMatchRuleTest;
import de.derfrzocker.ore.control.gui.GuiValuesHolder;
import de.derfrzocker.ore.control.gui.PlayerGuiData;
import de.derfrzocker.ore.control.gui.ScreenUtil;
import de.derfrzocker.ore.control.gui.Screens;
import de.derfrzocker.spigot.utils.gui.InventoryGui;
import de.derfrzocker.spigot.utils.gui.builders.Builders;
import org.bukkit.entity.Player;

public class BlockMatchRuleTestScreen {

    public static InventoryGui getGui(GuiValuesHolder guiValuesHolder) {
        return Builders
                .single()
                .identifier(Screens.RULE_TEST_BLOCK_MATCH_SCREEN)
                .languageManager(guiValuesHolder.languageManager())
                .withSetting(guiValuesHolder.settingFunction().apply("design.yml"))
                .withSetting(guiValuesHolder.settingFunction().apply("rule_test/block_match_screen.yml"))
                .addButtonContext(
                        Builders
                                .buttonContext()
                                .identifier("block")
                                .button(Builders
                                        .button()
                                        .identifier("block")
                                        // TODO: 5/5/23 Set item stack 
                                        .withAction(clickAction -> clickAction.getClickEvent().setCancelled(true))
                                        .withAction(clickAction -> {/* TODO: 5/5/23 add method to click block */})
                                )
                )
                .withBackAction((setting, guiInfo) -> guiValuesHolder.guiManager().getPlayerGuiData((Player) guiInfo.getEntity()).removeData("target_rule_test"))
                .addButtonContext(ScreenUtil.getBackButton(guiValuesHolder.guiManager()))
                .build();
    }

    private static BlockMatchRuleTest getRuleTest(GuiValuesHolder guiValuesHolder, Player player) {
        PlayerGuiData guiData = guiValuesHolder.guiManager().getPlayerGuiData(player);
        return guiData.getData("target_rule_test");
    }
}
