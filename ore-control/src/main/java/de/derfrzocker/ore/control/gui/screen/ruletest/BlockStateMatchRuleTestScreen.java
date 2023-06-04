package de.derfrzocker.ore.control.gui.screen.ruletest;

import de.derfrzocker.feature.common.ruletest.BlockStateMatchRuleTest;
import de.derfrzocker.ore.control.gui.GuiValuesHolder;
import de.derfrzocker.ore.control.gui.PlayerGuiData;
import de.derfrzocker.ore.control.gui.ScreenUtil;
import de.derfrzocker.ore.control.gui.Screens;
import de.derfrzocker.spigot.utils.gui.InventoryGui;
import de.derfrzocker.spigot.utils.gui.builders.Builders;
import de.derfrzocker.spigot.utils.message.MessageValue;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BlockStateMatchRuleTestScreen {

    public static InventoryGui getGui(GuiValuesHolder guiValuesHolder) {
        return Builders
                .single()
                .identifier(Screens.RULE_TEST_BLOCK_STATE_MATCH_SCREEN)
                .languageManager(guiValuesHolder.languageManager())
                .withSetting(guiValuesHolder.settingFunction().apply("design.yml"))
                .withSetting(guiValuesHolder.settingFunction().apply("rule_test/block_state_match_screen.yml"))
                .addButtonContext(
                        Builders
                                .buttonContext()
                                .identifier("block-state")
                                .button(Builders
                                        .button()
                                        .identifier("block-state")
                                        .withMessageValue((setting, guiInfo) -> new MessageValue("block-state", getRuleTest(guiValuesHolder, (Player) guiInfo.getEntity()).getBlockData().getAsString()))
                                        .itemStack((setting, guiInfo) -> {
                                            ItemStack itemStack = setting.get("rule_test.block_state_match_screen", "block-state.item-stack", new ItemStack(Material.STONE)).clone();

                                            itemStack.setType(getRuleTest(guiValuesHolder, (Player) guiInfo.getEntity()).getBlockData().getMaterial());

                                            return itemStack;
                                        })
                                        .withAction(clickAction -> clickAction.getClickEvent().setCancelled(true))
                                        .withAction(clickAction -> {
                                            PlayerGuiData playerGuiData = guiValuesHolder.guiManager().getPlayerGuiData(clickAction.getPlayer());
                                            playerGuiData.setHandleInventoryClosing(false);
                                            guiValuesHolder.plugin().getServer().getScheduler().runTask(guiValuesHolder.plugin(), () -> clickAction.getPlayer().closeInventory());
                                            guiValuesHolder.blockInteractionManager().createBasicBlockDataInteraction(clickAction.getPlayer(), blockData -> {
                                                BlockStateMatchRuleTest ruleTest = getRuleTest(guiValuesHolder, clickAction.getPlayer());
                                                ruleTest.setBlockData(blockData);
                                                playerGuiData.apply(guiValuesHolder.plugin(), guiValuesHolder.oreControlManager());
                                                guiValuesHolder.guiManager().openScreen(playerGuiData.pollFirstInventory(), clickAction.getPlayer());
                                            }, () -> guiValuesHolder.guiManager().openScreen(playerGuiData.pollFirstInventory(), clickAction.getPlayer()));
                                        })
                                )
                )
                .withBackAction((setting, guiInfo) -> guiValuesHolder.guiManager().getPlayerGuiData((Player) guiInfo.getEntity()).removeData("target_rule_test"))
                .addButtonContext(ScreenUtil.getBackButton(guiValuesHolder.guiManager()))
                .build();
    }

    private static BlockStateMatchRuleTest getRuleTest(GuiValuesHolder guiValuesHolder, Player player) {
        PlayerGuiData guiData = guiValuesHolder.guiManager().getPlayerGuiData(player);
        return guiData.getData("target_rule_test");
    }
}
