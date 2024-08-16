package de.derfrzocker.ore.control.gui.screen.value;

import de.derfrzocker.feature.api.util.traverser.message.TraversKey;
import de.derfrzocker.feature.common.value.target.FixedTargetListValue;
import de.derfrzocker.feature.common.value.target.TargetBlockState;
import de.derfrzocker.ore.control.gui.GuiValuesHolder;
import de.derfrzocker.ore.control.gui.PlayerGuiData;
import de.derfrzocker.ore.control.gui.ScreenUtil;
import de.derfrzocker.ore.control.gui.Screens;
import de.derfrzocker.spigot.utils.gui.GuiInfo;
import de.derfrzocker.spigot.utils.gui.InventoryGui;
import de.derfrzocker.spigot.utils.gui.builders.Builders;
import de.derfrzocker.spigot.utils.message.MessageValue;
import de.derfrzocker.spigot.utils.setting.Setting;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class FixedTargetListScreen {

    public static InventoryGui getGui(GuiValuesHolder guiValuesHolder) {
        return Builders
                .paged()
                .identifier(Screens.VALUE_FIXED_TARGET_LIST_SCREEN)
                .languageManager(guiValuesHolder.languageManager())
                .withSetting(guiValuesHolder.settingFunction().apply("design.yml"))
                .withSetting(guiValuesHolder.settingFunction().apply("value/fixed_target_list_screen.yml"))
                .addDefaultNextButton()
                .addDefaultPreviousButton()
                .pageContent(Builders
                        .pageContent(TargetBlockState.class)
                        .data((setting, guiInfo) -> getData(guiValuesHolder, guiInfo))
                        .withMessageValue((setting, guiInfo, targetBlockState) -> {
                            String entry = guiValuesHolder.valueTraverser().traverse(targetBlockState, TraversKey.ofValueSetting("entry"));
                            return new MessageValue("value-settings", entry);
                        })
                        .itemStack(FixedTargetListScreen::getItemStack)
                        .withAction((clickAction, targetBlockState) -> clickAction.getClickEvent().setCancelled(true))
                        .withAction((clickAction, targetBlockState) -> guiValuesHolder.guiManager().getPlayerGuiData(clickAction.getPlayer()).addData("target_block_state", targetBlockState))
                        .withAction((clickAction, targetBlockState) -> guiValuesHolder.guiManager().openScreen(Screens.OTHER_TARGET_BLOCK_STATE_SCREEN, clickAction.getPlayer()))
                )
                .withBackAction((setting, guiInfo) -> guiValuesHolder.guiManager().getPlayerGuiData((Player) guiInfo.getEntity()).setPreviousToEditValue())
                .addButtonContext(ScreenUtil.getBackButton(guiValuesHolder.guiManager()))
                .build();
    }

    private static List<TargetBlockState> getData(GuiValuesHolder guiValuesHolder, GuiInfo guiInfo) {
        PlayerGuiData playerGuiData = guiValuesHolder.guiManager().getPlayerGuiData((Player) guiInfo.getEntity());
        if (!(playerGuiData.getToEditValue() instanceof FixedTargetListValue value)) {
            guiValuesHolder.plugin().getLogger().warning(String.format("Expected a value of type '%s' but got one of type '%s', this is a bug!", FixedTargetListValue.class, playerGuiData.getToEditValue() != null ? playerGuiData.getToEditValue().getClass() : "null"));
            return Collections.emptyList();
        }

        // #31: Check for null
        return value.getValue() == null ? Collections.emptyList() : value.getValue();
    }

    private static ItemStack getItemStack(Setting setting, GuiInfo guiInfo, TargetBlockState targetBlockState) {
        ItemStack itemStack = setting.get("value.fixed_target_list_screen", "default-icon", new ItemStack(Material.STONE)).clone();

        itemStack.setType(targetBlockState.getState().getMaterial());

        return itemStack;
    }
}
