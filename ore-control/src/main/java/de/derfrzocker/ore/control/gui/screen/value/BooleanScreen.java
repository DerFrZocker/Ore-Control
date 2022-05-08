package de.derfrzocker.ore.control.gui.screen.value;

import de.derfrzocker.feature.common.value.bool.FixedBooleanValue;
import de.derfrzocker.ore.control.api.OreControlManager;
import de.derfrzocker.ore.control.gui.OreControlGuiManager;
import de.derfrzocker.ore.control.gui.PlayerGuiData;
import de.derfrzocker.spigot.utils.gui.InventoryGui;
import de.derfrzocker.spigot.utils.gui.builders.Builders;
import de.derfrzocker.spigot.utils.language.LanguageManager;
import de.derfrzocker.spigot.utils.setting.ConfigSetting;
import org.bukkit.plugin.Plugin;

import java.util.function.Function;

public class BooleanScreen {

    public static InventoryGui getGui(Plugin plugin, OreControlManager oreControlManager, LanguageManager languageManager, OreControlGuiManager guiManager, Function<String, ConfigSetting> settingFunction) {
        return Builders
                .single()
                .identifier("value.boolean_screen")
                .languageManager(languageManager)
                .withSetting(settingFunction.apply("design.yml"))
                .withSetting(settingFunction.apply("value/boolean_screen.yml"))
                .addButtonContext(Builders
                        .buttonContext()
                        .identifier("true")
                        .button(Builders
                                .button()
                                .identifier("true")
                                .withAction(clickAction -> clickAction.getClickEvent().setCancelled(true))
                                .withAction(clickAction -> {
                                    PlayerGuiData guiData = guiManager.getPlayerGuiData(clickAction.getPlayer());
                                    if (!(guiData.getToEditValue() instanceof FixedBooleanValue value)) {
                                        plugin.getLogger().warning(String.format("Expected a value of type '%s' but got one of type '%s', this is a bug!", FixedBooleanValue.class, guiData.getToEditValue() != null ? guiData.getToEditValue().getClass() : "null"));
                                        return;
                                    }

                                    value.setValue(true);
                                })
                                .withAction(clickAction -> guiManager.getPlayerGuiData(clickAction.getPlayer()).apply(plugin, oreControlManager))
                        )
                )
                .addButtonContext(Builders
                        .buttonContext()
                        .identifier("false")
                        .button(Builders
                                .button()
                                .identifier("false")
                                .withAction(clickAction -> clickAction.getClickEvent().setCancelled(true))
                                .withAction(clickAction -> {
                                    PlayerGuiData guiData = guiManager.getPlayerGuiData(clickAction.getPlayer());
                                    if (!(guiData.getToEditValue() instanceof FixedBooleanValue value)) {
                                        plugin.getLogger().warning(String.format("Expected a value of type '%s' but got one of type '%s', this is a bug!", FixedBooleanValue.class, guiData.getToEditValue() != null ? guiData.getToEditValue().getClass() : "null"));
                                        return;
                                    }

                                    value.setValue(false);
                                })
                                .withAction(clickAction -> guiManager.getPlayerGuiData(clickAction.getPlayer()).apply(plugin, oreControlManager))
                        )
                )
                .addButtonContext(Builders
                        .buttonContext()
                        .identifier("back")
                        .button(Builders
                                .button()
                                .identifier("back")
                                .withAction(clickAction -> clickAction.getClickEvent().setCancelled(true))
                                .withAction(clickAction -> {
                                            PlayerGuiData data = guiManager.getPlayerGuiData(clickAction.getPlayer());
                                            data.setPreviousToEditValue();

                                            if (data.getToEditValue() == null) {
                                                data.setOriginalValue(null);
                                                guiManager.openFeatureSettingsScreen(clickAction.getPlayer());
                                            } else {
                                                guiManager.openValueScreen(clickAction.getPlayer(), data.getToEditValue());
                                            }
                                        }
                                )
                        )
                )
                .build();
    }
}
