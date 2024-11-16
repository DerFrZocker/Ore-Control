package de.derfrzocker.ore.control.gui.screen;

import de.derfrzocker.ore.control.api.config.ConfigInfo;
import de.derfrzocker.ore.control.gui.GuiValuesHolder;
import de.derfrzocker.ore.control.gui.ScreenUtil;
import de.derfrzocker.ore.control.gui.Screens;
import de.derfrzocker.ore.control.gui.info.InfoLink;
import de.derfrzocker.spigot.utils.gui.InventoryGui;
import de.derfrzocker.spigot.utils.gui.builders.Builders;
import de.derfrzocker.spigot.utils.message.MessageValue;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static de.derfrzocker.ore.control.gui.info.InfoLinkData.of;

public class ConfigInfosScreen {

    public static InventoryGui getGui(GuiValuesHolder guiValuesHolder) {
        return Builders
                .paged()
                .identifier(Screens.CONFIG_INFOS_SCREEN)
                .languageManager(guiValuesHolder.languageManager())
                .withSetting(guiValuesHolder.settingFunction().apply("design.yml"))
                .withSetting(guiValuesHolder.settingFunction().apply("config_infos_screen.yml"))
                .addDefaultNextButton()
                .addDefaultPreviousButton()
                .pageContent(Builders
                        .pageContent(ConfigInfo.class)
                        .data((setting, guiInfo) -> {
                            List<ConfigInfo> data = new ArrayList<>(guiValuesHolder.configManager().getConfigInfos());
                            data.sort(null);
                            return data;
                        })
                        .withMessageValue((setting, guiInfo, configInfo) -> new MessageValue("world-name", configInfo.getWorldName()))
                        .itemStack((setting, guiInfo, configInfo) -> setting.get(Screens.CONFIG_INFOS_SCREEN, "default-icons." + configInfo.getConfigType(), new ItemStack(Material.STONE)).clone())
                        .withAction((clickAction, configInfo) -> clickAction.getClickEvent().setCancelled(true))
                        .withAction((clickAction, configInfo) -> guiValuesHolder.guiManager().getPlayerGuiData(clickAction.getPlayer()).setConfigInfo(configInfo))
                        .withAction((clickAction, configInfo) -> guiValuesHolder.guiManager().openScreen(Screens.CONFIG_INFO_SCREEN, clickAction.getPlayer()))
                )
                .addButtonContext(Builders
                        .buttonContext()
                        .identifier("language")
                        .button(Builders
                                .button()
                                .identifier("language")
                                .withAction(clickAction -> clickAction.getClickEvent().setCancelled(true))
                                .withAction(clickAction -> guiValuesHolder.guiManager().openScreen(Screens.LANGUAGE_SCREEN, clickAction.getPlayer()))
                        )
                )
                .addButtonContext(ScreenUtil.getInfoButton(guiValuesHolder, of(InfoLink.INVENTORY_GUI_SCREENS_EXPLAINED, "Choose-A-Config", "config_infos_screen")))
                .build();
    }
}
