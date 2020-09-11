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

import de.derfrzocker.ore.control.OreControlMessages;
import de.derfrzocker.ore.control.Permissions;
import de.derfrzocker.ore.control.PlayerJoinListener;
import de.derfrzocker.ore.control.WelcomeMessage;
import de.derfrzocker.ore.control.command.set.SetCommand;
import de.derfrzocker.ore.control.gui.settings.GuiSettings;
import de.derfrzocker.ore.control.utils.OreControlValues;
import de.derfrzocker.spigot.utils.command.CommandSeparator;
import de.derfrzocker.spigot.utils.command.HelpCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OreControlCommand extends CommandSeparator {

    public OreControlCommand(@NotNull final OreControlValues oreControlValues, @NotNull final GuiSettings guiSettings, @Nullable final PlayerJoinListener playerJoinListener, @Nullable final WelcomeMessage welcomeMessage) {
        super(oreControlValues.getPlugin());

        final OreControlMessages oreControlMessages = oreControlValues.getOreControlMessages();
        final Permissions permissions = oreControlValues.getPermissions();

        registerExecutor(new SetCommand(oreControlValues), "set", permissions.getSetPermission(), null, null);
        registerExecutor(new CreateCommand(oreControlValues), "create", permissions.getTemplateCreatePermission(), oreControlMessages.getCommandCreateUsageMessage(), oreControlMessages.getCommandCreateDescriptionMessage());
        registerExecutor(new GuiCommand(oreControlValues, guiSettings), "", permissions.getGuiPermission(), null, null);
        registerExecutor(new ReloadCommand(oreControlValues), "reload", permissions.getReloadPermission(), oreControlMessages.getCommandReloadUsageMessage(), oreControlMessages.getCommandReloadDescriptionMessage());

        final HelpCommand helpCommand = new HelpCommand(this, oreControlMessages);
        registerExecutor(helpCommand, "help", null, oreControlMessages.getCommandHelpUsageMessage(), oreControlMessages.getCommandHelpDescriptionMessage());
        registerExecutor(new InfoCommand(oreControlValues), "info", null, oreControlMessages.getCommandInfoUsageMessage(), oreControlMessages.getCommandInfoDescriptionMessage());

        if (playerJoinListener == null || welcomeMessage == null) {
            registerExecutor(helpCommand, null, null, oreControlMessages.getCommandHelpUsageMessage(), oreControlMessages.getCommandHelpDescriptionMessage());
        } else {
            registerExecutor(new WelcomeCommand(helpCommand, oreControlValues, welcomeMessage, playerJoinListener), null, null, oreControlMessages.getCommandHelpUsageMessage(), oreControlMessages.getCommandHelpDescriptionMessage());
        }
    }

}
