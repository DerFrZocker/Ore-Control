/*
 * MIT License
 *
 * Copyright (c) 2019 Marvin (DerFrZocker)
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
 */

package de.derfrzocker.ore.control.command;

import de.derfrzocker.ore.control.PlayerJoinListener;
import de.derfrzocker.ore.control.WelcomeMessage;
import de.derfrzocker.ore.control.utils.OreControlValues;
import de.derfrzocker.spigot.utils.Language;
import org.apache.commons.lang.Validate;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WelcomeCommand implements TabExecutor {

    @NotNull
    private final TabExecutor tabExecutor;
    @NotNull
    private final OreControlValues oreControlValues;
    @NotNull
    private final WelcomeMessage welcomeMessage;
    @NotNull
    private final PlayerJoinListener playerJoinListener;

    public WelcomeCommand(@NotNull final TabExecutor tabExecutor, @NotNull final OreControlValues oreControlValues, @NotNull final WelcomeMessage welcomeMessage, @NotNull final PlayerJoinListener playerJoinListener) {
        Validate.notNull(tabExecutor, "TabExecutor cannot be null");
        Validate.notNull(oreControlValues, "OreControlValues cannot be null");
        Validate.notNull(welcomeMessage, "WelcomeMessage cannot be null");
        Validate.notNull(playerJoinListener, "PlayerJoinListener cannot be null");

        this.tabExecutor = tabExecutor;
        this.oreControlValues = oreControlValues;
        this.welcomeMessage = welcomeMessage;
        this.playerJoinListener = playerJoinListener;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 2 || args.length > 3) {
            return tabExecutor.onCommand(sender, command, label, args);
        }

        if (!args[0].equals("welcome")) {
            return tabExecutor.onCommand(sender, command, label, args);
        }

        final String argTwo = args[1];

        if (argTwo.equals("language")) {
            if (args.length != 3) {
                return tabExecutor.onCommand(sender, command, label, args);
            }
            try {

                final Language language = Language.valueOf(args[2]);
                oreControlValues.getConfigValues().SET.setLanguage(language);

                //send 12 empty lines for a better visual appearance
                for (int i = 0; i < 12; i++) {
                    sender.sendMessage(" ");
                }

                welcomeMessage.sendMessage(sender);

                return true;
            } catch (final IllegalArgumentException e) {
                return tabExecutor.onCommand(sender, command, label, args);
            }
        }

        if (argTwo.equals("notShowAgain")) {
            if (args.length != 2) {
                return tabExecutor.onCommand(sender, command, label, args);
            }

            oreControlValues.getConfigValues().SET.setShowWelcomeMessage(false);
            PlayerJoinEvent.getHandlerList().unregister(playerJoinListener);

            oreControlValues.getOreControlMessages().getNotShowAgainSuccess().sendMessage(sender);
            return false;
        }

        return tabExecutor.onCommand(sender, command, label, args);
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return tabExecutor.onTabComplete(sender, command, alias, args);
    }

}
