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

import de.derfrzocker.feature.api.Value;
import de.derfrzocker.ore.control.gui.GuiValuesHolder;
import de.derfrzocker.ore.control.gui.PlayerGuiData;
import de.derfrzocker.spigot.utils.gui.builders.Builders;
import de.derfrzocker.spigot.utils.gui.builders.ButtonContextBuilder;

import java.util.function.Function;

public final class ValueUtil {

    private ValueUtil() {
    }

    public static <T extends Value<?, ?, ?>> ButtonContextBuilder getPassthroughButton(GuiValuesHolder guiValuesHolder, String identifier, Class<T> valueClass, Function<T, Value<?, ?, ?>> toEditFunction) {
        return Builders
                .buttonContext()
                .identifier(identifier)
                .button(Builders
                        .button()
                        .identifier(identifier)
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
}
