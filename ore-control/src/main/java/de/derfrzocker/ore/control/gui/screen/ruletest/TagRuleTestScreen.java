package de.derfrzocker.ore.control.gui.screen.ruletest;

import de.derfrzocker.feature.common.ruletest.TagMatchRuleTest;
import de.derfrzocker.ore.control.gui.GuiValuesHolder;
import de.derfrzocker.ore.control.gui.PlayerGuiData;
import de.derfrzocker.ore.control.gui.ScreenUtil;
import de.derfrzocker.ore.control.gui.Screens;
import de.derfrzocker.spigot.utils.gui.InventoryGui;
import de.derfrzocker.spigot.utils.gui.builders.Builders;
import de.derfrzocker.spigot.utils.message.MessageValue;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class TagRuleTestScreen {

    public static InventoryGui getGui(GuiValuesHolder guiValuesHolder) {
        return Builders
                .single()
                .identifier(Screens.RULE_TEST_TAG_MATCH_SCREEN)
                .languageManager(guiValuesHolder.languageManager())
                .withSetting(guiValuesHolder.settingFunction().apply("design.yml"))
                .withSetting(guiValuesHolder.settingFunction().apply("rule_test/tag_match_screen.yml"))
                .addButtonContext(
                        Builders
                                .buttonContext()
                                .identifier("tag")
                                .button(Builders
                                        .button()
                                        .identifier("tag")
                                        .withMessageValue((setting, guiInfo) -> new MessageValue("tag", getRuleTest(guiValuesHolder, (Player) guiInfo.getEntity()).getTag()))
                                        .itemStack((setting, guiInfo) -> setting.get("rule_test.tag_match_screen", "tag.item-stack", new ItemStack(Material.STONE)).clone())
                                        .withAction(clickAction -> clickAction.getClickEvent().setCancelled(true))
                                        .withAction(clickAction -> {
                                            PlayerGuiData playerGuiData = guiValuesHolder.guiManager().getPlayerGuiData(clickAction.getPlayer());
                                            TagMatchRuleTest ruleTest = getRuleTest(guiValuesHolder, clickAction.getPlayer());
                                            playerGuiData.addData("tag-consumer", (Consumer<NamespacedKey>) ruleTest::setTag);

                                            guiValuesHolder.guiManager().openScreen(Screens.OTHER_BLOCK_TAG_SCREEN, clickAction.getPlayer());
                                        })
                                )
                )
                .withBackAction((setting, guiInfo) -> guiValuesHolder.guiManager().getPlayerGuiData((Player) guiInfo.getEntity()).removeData("target_rule_test"))
                .addButtonContext(ScreenUtil.getBackButton(guiValuesHolder.guiManager()))
                .build();
    }

    private static TagMatchRuleTest getRuleTest(GuiValuesHolder guiValuesHolder, Player player) {
        PlayerGuiData guiData = guiValuesHolder.guiManager().getPlayerGuiData(player);
        return guiData.getData("target_rule_test");
    }
}