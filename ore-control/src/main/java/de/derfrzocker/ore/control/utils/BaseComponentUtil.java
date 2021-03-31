/*
 * MIT License
 *
 * Copyright (c) 2019 - 2021 Marvin (DerFrZocker)
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

package de.derfrzocker.ore.control.utils;

import de.derfrzocker.ore.control.OreControlMessages;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.jetbrains.annotations.NotNull;

public class BaseComponentUtil {

    public static BaseComponent[] combineBaseComponent(@NotNull final BaseComponent[] baseComponents, @NotNull final BaseComponent... baseComponents1) {
        final int firstLength = baseComponents.length;
        final int secondLength = baseComponents1.length;
        final BaseComponent[] result = new BaseComponent[firstLength + secondLength];

        System.arraycopy(baseComponents, 0, result, 0, firstLength);
        System.arraycopy(baseComponents1, 0, result, firstLength, secondLength);

        return result;
    }

    public static BaseComponent[] buildUrlButton(String text, String url, OreControlMessages messages) {
        final BaseComponent[] begin = TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', messages.getButtonOpenString().getRawMessage()));
        final BaseComponent[] end = TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', messages.getButtonCloseString().getRawMessage()));

        final BaseComponent[] buttons = TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', text));
        final BaseComponent[] hoverEventMessage = TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', messages.getClickMe().getRawMessage()));
        final HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverEventMessage);
        final ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.OPEN_URL, url);

        for (final BaseComponent button : buttons) {
            button.setHoverEvent(hoverEvent);
            button.setClickEvent(clickEvent);
        }

        final BaseComponent[] first = combineBaseComponent(begin, buttons);

        return combineBaseComponent(first, end);
    }

    public static BaseComponent[] buildLineWithUrlButton(String lineText, String clickText, String url, OreControlMessages messages) {
        final String[] split = lineText.split("%%button%");

        final BaseComponent[] button = buildUrlButton(clickText, url, messages);

        BaseComponent[] result = null;

        for (int i = 0; i < split.length; i++) {
            final BaseComponent[] textPart = TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', split[i]));

            if (result == null) {
                result = textPart;
            } else {
                result = combineBaseComponent(result, textPart);
            }

            if ((i + 1) < split.length) {
                result = combineBaseComponent(result, button);
            }

            if ((i + 1) == split.length && lineText.endsWith("%%button%")) {
                result = combineBaseComponent(result, button);
            }

        }

        return result;
    }

}

