/*
 * MIT License
 *
 * Copyright (c) 2019 - 2022 Marvin (DerFrZocker)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package de.derfrzocker.ore.control.gui.screen.value;

import de.derfrzocker.feature.api.Feature;
import de.derfrzocker.feature.common.value.number.FixedFloatValue;
import de.derfrzocker.feature.common.value.number.integer.FixedDoubleToIntegerValue;
import de.derfrzocker.ore.control.api.OreControlManager;
import de.derfrzocker.ore.control.gui.OreControlGuiManager;
import de.derfrzocker.ore.control.gui.PlayerGuiData;
import de.derfrzocker.spigot.utils.gui.InventoryGui;
import de.derfrzocker.spigot.utils.gui.builders.Builders;
import de.derfrzocker.spigot.utils.gui.builders.SingleInventoryGuiBuilder;
import de.derfrzocker.spigot.utils.language.LanguageManager;
import de.derfrzocker.spigot.utils.message.MessageValue;
import de.derfrzocker.spigot.utils.setting.ConfigSetting;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.NumberConversions;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class NumberEditScreen {

    private static final String DEFAULT_ICON = "default-icon";

    public static InventoryGui getFixedDoubleToIntegerGui(Plugin plugin, OreControlManager oreControlManager, LanguageManager languageManager, OreControlGuiManager guiManager, Function<String, ConfigSetting> settingFunction) {
        return getGui(plugin, oreControlManager, languageManager, guiManager, settingFunction,
                playerGuiData -> ((FixedDoubleToIntegerValue) playerGuiData.getToEditValue()).getValue(),
                (playerGuiData, number) -> {
                    if (!(playerGuiData.getToEditValue() instanceof FixedDoubleToIntegerValue value)) {
                        plugin.getLogger().warning(String.format("Expected a value of type '%s' but got one of type '%s', this is a bug!", FixedDoubleToIntegerValue.class, playerGuiData.getToEditValue() != null ? playerGuiData.getToEditValue().getClass() : "null"));
                        return;
                    }
                    value.setValue(value.getValue() + number.doubleValue());
                })
                .identifier("value.fixed_double_to_integer_screen")
                .withSetting(settingFunction.apply("value/fixed_double_to_integer_screen.yml"))
                .build();
    }

    public static InventoryGui getFixedFloatGui(Plugin plugin, OreControlManager oreControlManager, LanguageManager languageManager, OreControlGuiManager guiManager, Function<String, ConfigSetting> settingFunction) {
        return getGui(plugin, oreControlManager, languageManager, guiManager, settingFunction,
                playerGuiData -> ((FixedFloatValue) playerGuiData.getToEditValue()).getValue(),
                (playerGuiData, number) -> {
                    if (!(playerGuiData.getToEditValue() instanceof FixedFloatValue value)) {
                        plugin.getLogger().warning(String.format("Expected a value of type '%s' but got one of type '%s', this is a bug!", FixedFloatValue.class, playerGuiData.getToEditValue() != null ? playerGuiData.getToEditValue().getClass() : "null"));
                        return;
                    }
                    value.setValue(value.getValue() + number.floatValue());
                })
                .identifier("value.fixed_float_screen")
                .withSetting(settingFunction.apply("value/fixed_float_screen.yml"))
                .build();
    }

    private static SingleInventoryGuiBuilder getGui(Plugin plugin, OreControlManager oreControlManager, LanguageManager languageManager, OreControlGuiManager guiManager, Function<String, ConfigSetting> settingFunction, Function<PlayerGuiData, Number> numberSupplier, BiConsumer<PlayerGuiData, Number> numberConsumer) {
        return Builders
                .single()
                .languageManager(languageManager)
                .withSetting(settingFunction.apply("design.yml"))
                .withSetting(settingFunction.apply("feature_icons.yml"))
                .addListButton(Builders
                        .listButton()
                        .identifier("values")
                        .withMessageValue(((setting, guiInfo, value) -> new MessageValue("value", value)))
                        .withAction((clickAction, value) -> clickAction.getClickEvent().setCancelled(true))
                        .withAction((clickAction, value) -> numberConsumer.accept(guiManager.getPlayerGuiData(clickAction.getPlayer()), NumberConversions.toDouble(value)))
                        .withAction((clickAction, value) -> guiManager.getPlayerGuiData(clickAction.getPlayer()).apply(plugin, oreControlManager))
                        .withAction((clickAction, value) -> clickAction.getInventoryGui().updatedSoft())
                )
                .addButtonContext(Builders
                        .buttonContext()
                        .identifier(DEFAULT_ICON)
                        .button(Builders
                                .button()
                                .identifier(DEFAULT_ICON)
                                .withMessageValue((setting, guiInfo) -> new MessageValue("feature-name", guiManager.getPlayerGuiData((Player) guiInfo.getEntity()).getFeature().getKey()))
                                .withMessageValue((setting, guiInfo) -> new MessageValue("setting-name", guiManager.getPlayerGuiData((Player) guiInfo.getEntity()).getSettingWrapper().getSetting().name()))
                                .withMessageValue((setting, guiInfo) -> new MessageValue("current-value", numberSupplier.apply(guiManager.getPlayerGuiData((Player) guiInfo.getEntity()))))
                                .itemStack((setting, guiInfo) -> {
                                    PlayerGuiData playerGuiData = guiManager.getPlayerGuiData((Player) guiInfo.getEntity());
                                    Feature feature = playerGuiData.getFeature();
                                    String key = "icons." + feature.getKey().getNamespace() + "." + feature.getKey().getKey();
                                    ItemStack icon = setting.get(key + ".item-stack", null);
                                    if (icon == null) {
                                        icon = setting.get("default-icon.item-stack", new ItemStack(Material.STONE)).clone();
                                        String type = setting.get(key + ".type", null);
                                        if (type == null) {
                                            plugin.getLogger().info(String.format("No item stack or type found for feature '%s' using default item stack", feature.getKey()));
                                        } else {
                                            try {
                                                Material material = Material.valueOf(type.toUpperCase());
                                                icon.setType(material);
                                            } catch (IllegalArgumentException e) {
                                                plugin.getLogger().warning(String.format("Material '%s' for feature '%s' not found", type, feature.getKey()));
                                            }
                                        }
                                    } else {
                                        icon = icon.clone();
                                    }
                                    return icon;
                                })
                                .withAction(clickAction -> clickAction.getClickEvent().setCancelled(true))
                        )
                )
                .withBackAction((setting, guiInfo) -> guiManager.getPlayerGuiData((Player) guiInfo.getEntity()).setPreviousToEditValue())
                .addButtonContext(guiManager.getBackButton());
    }
}