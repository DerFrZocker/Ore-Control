package de.derfrzocker.ore.control.gui.screen.other;

import de.derfrzocker.ore.control.gui.GuiValuesHolder;
import de.derfrzocker.ore.control.gui.PlayerGuiData;
import de.derfrzocker.ore.control.gui.ScreenUtil;
import de.derfrzocker.ore.control.gui.Screens;
import de.derfrzocker.spigot.utils.gui.InventoryGui;
import de.derfrzocker.spigot.utils.gui.builders.Builders;
import de.derfrzocker.spigot.utils.message.MessageValue;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.StreamSupport;

public class BlockTagScreen {
    public static InventoryGui getGui(GuiValuesHolder guiValuesHolder) {
        return Builders
                .paged()
                .identifier(Screens.OTHER_BLOCK_TAG_SCREEN)
                .languageManager(guiValuesHolder.languageManager())
                .withSetting(guiValuesHolder.settingFunction().apply("design.yml"))
                .withSetting(guiValuesHolder.settingFunction().apply("other/block_tag_screen.yml"))
                .pageContent(Builders
                        .pageContent(NamespacedKey.class)
                        .data((setting, guiInfo) -> buildList())
                        .withMessageValue((setting, guiInfo, tag) -> new MessageValue("tag", tag))
                        .itemStack((setting, guiInfo, tag) -> setting.get("other.block_tag_screen", "default-icon.item-stack", new ItemStack(Material.STONE)).clone())
                        .withAction((clickAction, tag) -> clickAction.getClickEvent().setCancelled(true))
                        .withAction((clickAction, tag) -> getConsumer(guiValuesHolder, clickAction.getPlayer()).accept(tag))
                        .withAction(((clickAction, tag) -> guiValuesHolder.guiManager().getPlayerGuiData(clickAction.getPlayer()).apply(guiValuesHolder.plugin(), guiValuesHolder.oreControlManager())))
                        .withAction((clickAction, tag) -> guiValuesHolder.guiManager().getPlayerGuiData(clickAction.getPlayer()).pollFirstInventory().onBack(clickAction.getPlayer()))
                        .withAction((clickAction, tag) -> guiValuesHolder.guiManager().openScreen(guiValuesHolder.guiManager().getPlayerGuiData(clickAction.getPlayer()).pollFirstInventory(), clickAction.getPlayer()))
                )
                .withBackAction((setting, guiInfo) -> guiValuesHolder.guiManager().getPlayerGuiData((Player) guiInfo.getEntity()).removeData("tag-consumer"))
                .addButtonContext(ScreenUtil.getBackButton(guiValuesHolder.guiManager()))
                .build();
    }

    private static List<NamespacedKey> buildList() {
        return StreamSupport.stream(Bukkit.getTags(Tag.REGISTRY_BLOCKS, Material.class).spliterator(), false).map(Keyed::getKey).toList();
    }

    private static Consumer<NamespacedKey> getConsumer(GuiValuesHolder guiValuesHolder, Player player) {
        PlayerGuiData guiData = guiValuesHolder.guiManager().getPlayerGuiData(player);
        return guiData.getData("tag-consumer");
    }
}
