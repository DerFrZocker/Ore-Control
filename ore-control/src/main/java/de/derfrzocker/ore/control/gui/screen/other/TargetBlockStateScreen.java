package de.derfrzocker.ore.control.gui.screen.other;

import de.derfrzocker.feature.common.value.target.TargetBlockState;
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
                                        .withMessageValue((setting, guiInfo) -> new MessageValue("rule-test-key", getTargetBlockState(guiValuesHolder, (Player) guiInfo.getEntity()).getTarget().getType().getKey().getKey()))
                                        .withMessageValue((setting, guiInfo) -> new MessageValue("rule-test-namespace", getTargetBlockState(guiValuesHolder, (Player) guiInfo.getEntity()).getTarget().getType().getKey().getNamespace()))
                                        .withAction(clickAction -> clickAction.getClickEvent().setCancelled(true))
                                        .withAction(clickAction -> guiValuesHolder.guiManager().getPlayerGuiData(clickAction.getPlayer())
                                                .addData("target_rule_test", getTargetBlockState(guiValuesHolder, clickAction.getPlayer()).getTarget()))
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
                                        .withMessageValue((setting, guiInfo) -> new MessageValue("state", getTargetBlockState(guiValuesHolder, (Player) guiInfo.getEntity()).getState().getAsString()))
                                        .itemStack((setting, guiInfo) -> {
                                            ItemStack itemStack = setting.get("other.target_block_state_screen", "state.item-stack", new ItemStack(Material.STONE)).clone();

                                            itemStack.setType(getTargetBlockState(guiValuesHolder, (Player) guiInfo.getEntity()).getState().getMaterial());

                                            return itemStack;
                                        })
                                        .withAction(clickAction -> clickAction.getClickEvent().setCancelled(true))
                                        .withAction(clickAction -> {
                                            PlayerGuiData playerGuiData = guiValuesHolder.guiManager().getPlayerGuiData(clickAction.getPlayer());
                                            playerGuiData.setHandleInventoryClosing(false);
                                            guiValuesHolder.plugin().getServer().getScheduler().runTask(guiValuesHolder.plugin(), () -> clickAction.getPlayer().closeInventory());
                                            guiValuesHolder.blockInteractionManager().createBasicBlockDataInteraction(clickAction.getPlayer(), blockData -> {
                                                TargetBlockState targetBlockState = getTargetBlockState(guiValuesHolder, clickAction.getPlayer());
                                                targetBlockState.setState(blockData);
                                                playerGuiData.apply(guiValuesHolder.plugin(), guiValuesHolder.oreControlManager());
                                                guiValuesHolder.guiManager().openScreen(playerGuiData.pollFirstInventory(), clickAction.getPlayer());
                                            }, () -> guiValuesHolder.guiManager().openScreen(playerGuiData.pollFirstInventory(), clickAction.getPlayer()));
                                        })
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
