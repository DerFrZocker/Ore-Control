package de.derfrzocker.ore.control.gui.screen;

import de.derfrzocker.ore.control.gui.GuiValuesHolder;
import de.derfrzocker.ore.control.gui.ScreenUtil;
import de.derfrzocker.ore.control.gui.Screens;
import de.derfrzocker.ore.control.gui.info.InfoLink;
import de.derfrzocker.spigot.utils.gui.InventoryGui;
import de.derfrzocker.spigot.utils.gui.builders.Builders;

import static de.derfrzocker.ore.control.gui.info.InfoLinkData.of;

public class ConfigInfoScreen {

    public static InventoryGui getGui(GuiValuesHolder guiValuesHolder) {
        return Builders
                .single()
                .identifier(Screens.CONFIG_INFO_SCREEN)
                .languageManager(guiValuesHolder.languageManager())
                .withSetting(guiValuesHolder.settingFunction().apply("design.yml"))
                .withSetting(guiValuesHolder.settingFunction().apply("config_info_screen.yml"))
                .addButtonContext(Builders
                        .buttonContext()
                        .identifier("whole-world")
                        .button(Builders
                                .button()
                                .identifier("whole-world")
                                .withAction(clickAction -> clickAction.getClickEvent().setCancelled(true))
                                .withAction(clickAction -> guiValuesHolder.guiManager().openScreen(Screens.FEATURE_SELECTION_SCREEN, clickAction.getPlayer()))
                        )
                )
                .addButtonContext(Builders
                        .buttonContext()
                        .identifier("biome")
                        .button(Builders
                                .button()
                                .identifier("biome")
                                .withAction(clickAction -> clickAction.getClickEvent().setCancelled(true))
                                .withAction(clickAction -> guiValuesHolder.guiManager().openScreen(Screens.BIOME_SCREEN, clickAction.getPlayer()))
                        )
                )
                .addButtonContext(Builders
                        .buttonContext()
                        .identifier("extra-values")
                        .button(Builders
                                .button()
                                .identifier("extra-values")
                                .withAction(clickAction -> clickAction.getClickEvent().setCancelled(true))
                                .withAction(clickAction -> guiValuesHolder.guiManager().openScreen(Screens.EXTRA_VALUES_SCREEN, clickAction.getPlayer()))
                        )
                )
                .addButtonContext(ScreenUtil.getBackButton(guiValuesHolder.guiManager()))
                .addButtonContext(ScreenUtil.getInfoButton(guiValuesHolder, of(InfoLink.INVENTORY_GUI_SCREENS_EXPLAINED, "Choose-A-Type", "config_info_screen")))
                .build();
    }
}
