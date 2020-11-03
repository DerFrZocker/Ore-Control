/*
 * MIT License
 *
 * Copyright (c) 2019 - 2020 Marvin (DerFrZocker)
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

package de.derfrzocker.ore.control;

import de.derfrzocker.spigot.utils.Language;
import de.derfrzocker.spigot.utils.Pair;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.Validate;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WelcomeMessage {

    private final BaseComponent space = new TextComponent(" ");
    @NotNull
    private final Plugin plugin;
    @NotNull
    private final OreControlMessages messages;

    public WelcomeMessage(@NotNull final Plugin plugin, @NotNull final OreControlMessages messages) {
        Validate.notNull(plugin, "Plugin cannot be null");
        Validate.notNull(messages, "OreControlMessages cannot be null");

        this.plugin = plugin;
        this.messages = messages;
    }

    public void sendDelayMessage(@NotNull final CommandSender sender) {
        sender.getServer().getScheduler().runTaskLater(plugin, () -> sendMessage(sender), 21);
    }

    //TODO add link to a video tutorial
    public void sendMessage(@NotNull final CommandSender sender) {
        final BaseComponent[] welcome = TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', messages.getWelcomeHeader().getRawMessage()));
        final BaseComponent[] language = buildLanguageButtons();
        final BaseComponent[] foundBug = buildLineWithUrlButton(messages.getFoundBug().getRawMessage(), "GitHub", "https://github.com/DerFrZocker/Ore-Control/issues");
        final BaseComponent[] featureRequest = buildLineWithUrlButton(messages.getFeatureRequest().getRawMessage(), "GitHub", "https://github.com/DerFrZocker/Ore-Control/issues");
        final BaseComponent[] support = buildLineWithUrlButton(messages.getSupport().getRawMessage(), "Discord", "http://discord.derfrzocker.de");

        final Map<String, Pair<String, String>> buttonValues = new HashMap<>();
        buttonValues.put("rating", new Pair<>(messages.getRating().getRawMessage(), "https://www.spigotmc.org/resources/63621"));
        buttonValues.put("donation", new Pair<>(messages.getDonation().getRawMessage(), "https://www.paypal.me/DerFrZocker"));
        final BaseComponent[] supportMyWork = buildLineWithMultipleUrlButton(messages.getSupportMyWork().getRawMessage(), buttonValues);

        final BaseComponent[] notShowAgain = buildCommandButton(messages.getNotShowAgain().getRawMessage(), "/orecontrol welcome notShowAgain");

        sender.spigot().sendMessage(welcome);
        sender.spigot().sendMessage(language);
        sender.spigot().sendMessage(foundBug);
        sender.spigot().sendMessage(featureRequest);
        sender.spigot().sendMessage(support);
        sender.spigot().sendMessage(supportMyWork);
        sender.spigot().sendMessage(notShowAgain);
    }

    private BaseComponent[] buildLanguageButtons() {

        BaseComponent[] result = null;

        for (final Language language : Language.values()) {
            final BaseComponent[] button = buildCommandButton(language.getNames()[0], "/orecontrol welcome language " + language.toString());

            if (result == null) {
                result = button;
            } else {
                result = combineBaseComponent(result, space);
                result = combineBaseComponent(result, button);
            }
        }

        return result;
    }

    // %%button:[rating]%
    private BaseComponent[] buildLineWithMultipleUrlButton(String lineText, Map<String, Pair<String, String>> buttonValues) {

        BaseComponent[] result = null;

        final Pattern pattern = Pattern.compile("%%button:(.*?)]%");
        final Matcher matcher = pattern.matcher(lineText);
        int last = 0;

        final StringBuilder stringBuilder = new StringBuilder(lineText);

        while (matcher.find()) {
            String key = stringBuilder.substring(matcher.start() + 10, matcher.end() - 2);

            Pair<String, String> buttonValue = buttonValues.get(key);

            if (buttonValue == null) {
                buttonValue = new Pair<>("not Found", "not Found");
            }

            final BaseComponent[] button = buildUrlButton(buttonValue.getFirst(), buttonValue.getSecond());

            if (matcher.start() == 0) {
                result = button;
            } else {
                String textBefore = stringBuilder.substring(last, matcher.start());

                final BaseComponent[] before = TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', textBefore));

                if (result == null) {
                    result = combineBaseComponent(before, button);
                } else {
                    result = combineBaseComponent(result, before);
                    result = combineBaseComponent(result, button);
                }

            }

            last = matcher.end();
        }

        if (stringBuilder.length() != last) {
            final BaseComponent[] end = TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', stringBuilder.substring(last)));
            if (result == null) {
                result = end;
            } else {
                result = combineBaseComponent(result, end);
            }
        }

        return result;
    }

    private BaseComponent[] buildLineWithUrlButton(String lineText, String clickText, String url) {
        final String[] split = lineText.split("%%button%");

        final BaseComponent[] button = buildUrlButton(clickText, url);

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

    private BaseComponent[] buildCommandButton(String text, String command) {
        final BaseComponent[] begin = TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', messages.getButtonOpenString().getRawMessage()));
        final BaseComponent[] end = TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', messages.getButtonCloseString().getRawMessage()));

        final BaseComponent[] buttons = TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', text));
        final BaseComponent[] hoverEventMessage = TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', messages.getClickMe().getRawMessage()));
        final HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverEventMessage);
        final ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, command);

        for (final BaseComponent button : buttons) {
            button.setHoverEvent(hoverEvent);
            button.setClickEvent(clickEvent);
        }

        final BaseComponent[] first = combineBaseComponent(begin, buttons);

        return combineBaseComponent(first, end);
    }

    private BaseComponent[] buildUrlButton(String text, String url) {
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

    private static BaseComponent[] combineBaseComponent(@NotNull final BaseComponent[] baseComponents, @NotNull final BaseComponent... baseComponents1) {
        final int firstLength = baseComponents.length;
        final int secondLength = baseComponents1.length;
        final BaseComponent[] result = new BaseComponent[firstLength + secondLength];

        System.arraycopy(baseComponents, 0, result, 0, firstLength);
        System.arraycopy(baseComponents1, 0, result, firstLength, secondLength);

        return result;
    }

}
