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

package de.derfrzocker.ore.control.command;

import de.derfrzocker.ore.control.utils.OreControlValues;
import de.derfrzocker.spigot.utils.command.CommandUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import org.apache.commons.lang.Validate;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class InfoCommand implements TabExecutor {

    private final BaseComponent[] space = new BaseComponent[]{new TextComponent(" ")};

    @NotNull
    private final OreControlValues oreControlValues;

    public InfoCommand(@NotNull final OreControlValues oreControlValues) {
        Validate.notNull(oreControlValues, "OreControlValues cannot be null");

        this.oreControlValues = oreControlValues;
    }

    @Override
    public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String label, @NotNull final String[] args) {
        CommandUtil.runAsynchronously(sender, oreControlValues.getPlugin(), () -> {

            final BaseComponent[] source = buildUrlButton("Source", "https://github.com/DerFrZocker/Ore-Control");
            final BaseComponent[] discord = buildUrlButton("Discord", "http://discord.derfrzocker.de");
            final BaseComponent[] patreon = buildUrlButton("Patreon", "https://www.patreon.com/woollydevelopment");

            final BaseComponent[] spaceSource = combineBaseComponent(space, source);
            final BaseComponent[] spaceSourceSpace = combineBaseComponent(spaceSource, space);
            final BaseComponent[] spaceSourceSpaceDiscord = combineBaseComponent(spaceSourceSpace, discord);
            final BaseComponent[] spaceSourceSpaceDiscordSpace = combineBaseComponent(spaceSourceSpaceDiscord, space);
            final BaseComponent[] spaceSourceSpaceDiscordSpacePatreon = combineBaseComponent(spaceSourceSpaceDiscordSpace, patreon);

            sender.sendMessage("------- " + ChatColor.BLUE + "Ore-Control" + ChatColor.RESET + " -------");
            sender.spigot().sendMessage(spaceSourceSpaceDiscordSpacePatreon);
            sender.sendMessage("    ");
            sender.sendMessage("      Version: " + ChatColor.BLUE + oreControlValues.getPlugin().getDescription().getVersion());
            sender.sendMessage("      Author: " + ChatColor.BLUE + oreControlValues.getPlugin().getDescription().getAuthors().iterator().next());
            sender.sendMessage("    ");
            sender.sendMessage("------- " + ChatColor.BLUE + "Ore-Control" + ChatColor.RESET + " -------");
        });

        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String alias, @NotNull final String[] args) {
        return new ArrayList<>();
    }

    private BaseComponent[] buildUrlButton(String text, String url) {
        final BaseComponent[] begin = new ComponentBuilder("[").color(ChatColor.DARK_RED).create();
        final BaseComponent[] end = new ComponentBuilder("]").color(ChatColor.DARK_RED).create();

        final BaseComponent[] buttons = TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', text));
        final BaseComponent[] hoverEventMessage = new ComponentBuilder("Click me").create();
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
