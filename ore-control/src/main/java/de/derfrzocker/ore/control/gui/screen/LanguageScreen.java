package de.derfrzocker.ore.control.gui.screen;

import de.derfrzocker.ore.control.gui.GuiValuesHolder;
import de.derfrzocker.spigot.utils.gui.InventoryGui;
import de.derfrzocker.spigot.utils.gui.builders.Builders;
import de.derfrzocker.spigot.utils.language.Language;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class LanguageScreen {

    public static InventoryGui getGui(GuiValuesHolder guiValuesHolder) {
        return Builders
                .paged()
                .identifier("language_screen")
                .languageManager(guiValuesHolder.languageManager())
                .withSetting(guiValuesHolder.settingFunction().apply("design.yml"))
                .withSetting(guiValuesHolder.settingFunction().apply("language_screen.yml"))
                .addDefaultNextButton()
                .addDefaultPreviousButton()
                .pageContent(Builders
                        .pageContent(Language.class)
                        .data(((setting, guiInfo) -> guiValuesHolder.languageManager().getAvailableLanguages()))
                        .itemStack((setting, guiInfo, language) -> language.getLogo())
                        .withAction((clickAction, language) -> clickAction.getClickEvent().setCancelled(true))
                        .withAction((clickAction, language) -> guiValuesHolder.languageManager().setLanguage(clickAction.getPlayer(), language))
                        .withAction((clickAction, language) -> clickAction.getInventoryGui().openGui(guiValuesHolder.plugin(), clickAction.getPlayer(), true))
                )
                .addButtonContext(Builders
                        .buttonContext()
                        .identifier("finished")
                        .withCondition((setting, guiInfo) -> guiValuesHolder.languageManager().hasLanguageSet((Player) guiInfo.getEntity()))
                        .button(Builders
                                .button()
                                .identifier("finished")
                                .withAction(clickAction -> clickAction.getClickEvent().setCancelled(true))
                                .withAction(clickAction -> guiValuesHolder.guiManager().openGui(clickAction.getPlayer()))
                        )
                )
                .build();
    }

}
