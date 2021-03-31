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

package de.derfrzocker.ore.control.command.set;

import de.derfrzocker.ore.control.OreControlMessages;
import de.derfrzocker.ore.control.Permissions;
import de.derfrzocker.ore.control.utils.OreControlValues;
import de.derfrzocker.spigot.utils.command.CommandSeparator;
import de.derfrzocker.spigot.utils.command.HelpCommand;
import org.jetbrains.annotations.NotNull;

public class SetCommand extends CommandSeparator {

    public SetCommand(@NotNull final OreControlValues oreControlValues) {
        super(oreControlValues.getPlugin());

        final OreControlMessages messages = oreControlValues.getOreControlMessages();
        final Permissions permissions = oreControlValues.getPermissions();

        registerExecutor(new SetValueCommand(oreControlValues), "value", permissions.getSetValuePermission(), messages.getCommandSetValueUsageMessage(), messages.getCommandSetValueDescriptionMessage());
        registerExecutor(new SetBiomeCommand(oreControlValues), "biome", permissions.getSetBiomePermission(), messages.getCommandSetBiomeUsageMessage(), messages.getCommandSetBiomeDescriptionMessage());

        final HelpCommand helpCommand = new HelpCommand(this, messages);
        registerExecutor(helpCommand, "help", null, null, null);
        registerExecutor(helpCommand, null, null, null, null);
    }

}
