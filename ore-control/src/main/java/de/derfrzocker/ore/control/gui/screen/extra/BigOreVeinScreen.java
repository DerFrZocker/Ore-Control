package de.derfrzocker.ore.control.gui.screen.extra;

import de.derfrzocker.feature.api.ExtraValues;
import de.derfrzocker.ore.control.api.config.ConfigInfo;
import de.derfrzocker.ore.control.gui.GuiValuesHolder;
import de.derfrzocker.ore.control.gui.PlayerGuiData;
import de.derfrzocker.ore.control.gui.ScreenUtil;
import de.derfrzocker.spigot.utils.gui.ClickAction;
import de.derfrzocker.spigot.utils.gui.InventoryGui;
import de.derfrzocker.spigot.utils.gui.builders.Builders;

import java.util.Optional;

public class BigOreVeinScreen {

    public static InventoryGui getGui(GuiValuesHolder guiValuesHolder) {
        return Builders
                .single()
                .identifier("big_ore_vein_screen")
                .languageManager(guiValuesHolder.languageManager())
                .withSetting(guiValuesHolder.settingFunction().apply("design.yml"))
                .withSetting(guiValuesHolder.settingFunction().apply("extra/big_ore_vein_screen.yml"))
                .addButtonContext(ScreenUtil.getBackButton(guiValuesHolder.guiManager()))
                .addButtonContext(Builders
                        .buttonContext()
                        .identifier("activation")
                        .button(Builders
                                .button()
                                .identifier("activation")
                                .withAction(clickAction -> clickAction.getClickEvent().setCancelled(true))
                                .withAction(clickAction -> setValue(guiValuesHolder, clickAction, Optional.of(true)))
                        )
                )
                .addButtonContext(Builders
                        .buttonContext()
                        .identifier("deactivation")
                        .button(Builders
                                .button()
                                .identifier("deactivation")
                                .withAction(clickAction -> clickAction.getClickEvent().setCancelled(true))
                                .withAction(clickAction -> setValue(guiValuesHolder, clickAction, Optional.of(false)))
                        )
                )
                .addButtonContext(Builders
                        .buttonContext()
                        .identifier("reset")
                        .button(Builders
                                .button()
                                .identifier("reset")
                                .withAction(clickAction -> clickAction.getClickEvent().setCancelled(true))
                                .withAction(clickAction -> setValue(guiValuesHolder, clickAction, Optional.empty()))
                        )
                )
                .addButtonContext(ScreenUtil.getBackButton(guiValuesHolder.guiManager()))
                .build();
    }

    private static void setValue(GuiValuesHolder guiValuesHolder, ClickAction clickAction, Optional<Boolean> status) {
        PlayerGuiData playerGuiData = guiValuesHolder.guiManager().getPlayerGuiData(clickAction.getPlayer());
        ConfigInfo configInfo = playerGuiData.getConfigInfo();
        ExtraValues extraValues = guiValuesHolder.oreControlManager().getConfigManager().getOrCreateExtraValues(configInfo);
        extraValues.setGenerateBigOreVeins(status);
    }
}
