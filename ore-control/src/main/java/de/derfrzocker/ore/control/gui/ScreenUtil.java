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

package de.derfrzocker.ore.control.gui;

import de.derfrzocker.feature.api.Value;
import de.derfrzocker.spigot.utils.gui.builders.Builders;
import de.derfrzocker.spigot.utils.gui.builders.ButtonContextBuilder;
import de.derfrzocker.spigot.utils.message.MessageValue;
import de.derfrzocker.spigot.utils.setting.Setting;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Function;

public final class ScreenUtil {

    private ScreenUtil() {
    }

    public static ItemStack getIcon(GuiValuesHolder guiValuesHolder, Setting setting, String identifier, Keyed keyed) {
        String key = "icons." + keyed.getKey().getNamespace() + "." + keyed.getKey().getKey();
        ItemStack icon = setting.get(identifier, key + ".item-stack", null);
        if (icon == null) {
            icon = setting.get(identifier, "default-icon.item-stack", new ItemStack(Material.STONE)).clone();
            String type = setting.get(identifier, key + ".type", null);
            if (type == null) {
                guiValuesHolder.plugin().getLogger().info(String.format("No item stack or type found for '%s' using default item stack", keyed.getKey()));
            } else {
                try {
                    Material material = Material.valueOf(type.toUpperCase());
                    icon.setType(material);
                } catch (IllegalArgumentException e) {
                    guiValuesHolder.plugin().getLogger().warning(String.format("Material '%s' for '%s' not found", type, keyed.getKey()));
                }
            }
        } else {
            icon = icon.clone();
        }
        return icon;
    }

    public static ButtonContextBuilder getBackButton(OreControlGuiManager guiManager) {
        return Builders
                .buttonContext()
                .identifier("back")
                .button(Builders
                        .button()
                        .identifier("back")
                        .withAction(clickAction -> clickAction.getClickEvent().setCancelled(true))
                        .withAction(clickAction -> guiManager.getPlayerGuiData(clickAction.getPlayer()).pollFirstInventory().onBack(clickAction.getPlayer()))
                        .withAction(clickAction -> guiManager.openScreen(guiManager.getPlayerGuiData(clickAction.getPlayer()).pollFirstInventory(), clickAction.getPlayer()))
                );
    }

    public static <T extends Value<?, ?, ?>> ButtonContextBuilder getPassthroughButton(GuiValuesHolder guiValuesHolder, String identifier, Class<T> valueClass, Function<T, Value<?, ?, ?>> toEditFunction) {
        return Builders
                .buttonContext()
                .identifier(identifier)
                .button(Builders
                        .button()
                        .identifier(identifier)
                        .withMessageValue((setting, guiInfo) -> {
                            PlayerGuiData guiData = guiValuesHolder.guiManager().getPlayerGuiData((Player) guiInfo.getEntity());
                            Value<?, ?, ?> currently = guiData.getToEditValue();
                            if (!valueClass.isAssignableFrom(currently.getClass())) {
                                guiValuesHolder.plugin().getLogger().warning(String.format("Expected a value of type '%s' but got one of type '%s', this is a bug!", valueClass, guiData.getToEditValue() != null ? guiData.getToEditValue().getClass() : "null"));
                            }
                            Value<?, ?, ?> toEdit = toEditFunction.apply((T) currently);
                            return new MessageValue("value-settings", guiValuesHolder.valueTraverser().traverse(toEdit, "§r§f%%translation:[value-types." + toEdit.getValueType().getKey().getNamespace() + "." + toEdit.getValueType().getKey().getKey() + ".name]%: "));
                        })
                        .withAction(clickAction -> clickAction.getClickEvent().setCancelled(true))
                        .withAction(clickAction -> {
                            PlayerGuiData guiData = guiValuesHolder.guiManager().getPlayerGuiData(clickAction.getPlayer());
                            Value<?, ?, ?> currently = guiData.getToEditValue();
                            if (!valueClass.isAssignableFrom(currently.getClass())) {
                                guiValuesHolder.plugin().getLogger().warning(String.format("Expected a value of type '%s' but got one of type '%s', this is a bug!", valueClass, guiData.getToEditValue() != null ? guiData.getToEditValue().getClass() : "null"));
                            }
                            Value<?, ?, ?> toEdit = toEditFunction.apply((T) currently);
                            guiData.setToEditValue(toEdit);
                            guiValuesHolder.guiManager().openValueScreen(clickAction.getPlayer(), toEdit);
                        })
                );
    }

    public static <T> ButtonContextBuilder getPassthroughButton(GuiValuesHolder guiValuesHolder, String identifier, String dataKey, Class<T> valueClass, Function<T, Value<?, ?, ?>> toEditFunction) {
        return Builders
                .buttonContext()
                .identifier(identifier)
                .button(Builders
                        .button()
                        .identifier(identifier)
                        .withMessageValue((setting, guiInfo) -> {
                            PlayerGuiData guiData = guiValuesHolder.guiManager().getPlayerGuiData((Player) guiInfo.getEntity());
                            Object currently = guiData.getData(dataKey);
                            if (!valueClass.isAssignableFrom(currently.getClass())) {
                                guiValuesHolder.plugin().getLogger().warning(String.format("Expected a value of type '%s' but got one of type '%s', this is a bug!", valueClass, currently.getClass()));
                            }
                            Value<?, ?, ?> toEdit = toEditFunction.apply((T) currently);
                            return new MessageValue("value-settings", guiValuesHolder.valueTraverser().traverse(toEdit, "§r§f%%translation:[value-types." + toEdit.getValueType().getKey().getNamespace() + "." + toEdit.getValueType().getKey().getKey() + ".name]%: "));
                        })
                        .withAction(clickAction -> clickAction.getClickEvent().setCancelled(true))
                        .withAction(clickAction -> {
                            PlayerGuiData guiData = guiValuesHolder.guiManager().getPlayerGuiData(clickAction.getPlayer());
                            Object currently = guiData.getData(dataKey);
                            if (!valueClass.isAssignableFrom(currently.getClass())) {
                                guiValuesHolder.plugin().getLogger().warning(String.format("Expected a value of type '%s' but got one of type '%s', this is a bug!", valueClass, currently.getClass()));
                            }
                            Value<?, ?, ?> toEdit = toEditFunction.apply((T) currently);
                            guiData.setToEditValue(toEdit);
                            guiValuesHolder.guiManager().openValueScreen(clickAction.getPlayer(), toEdit);
                        })
                );
    }
}
