package de.derfrzocker.ore.control.command.set;

import de.derfrzocker.ore.control.OreControlMessages;
import de.derfrzocker.ore.control.Permissions;
import de.derfrzocker.ore.control.utils.OreControlValues;
import de.derfrzocker.spigot.utils.command.CommandSeparator;
import de.derfrzocker.spigot.utils.command.HelpCommand;
import org.jetbrains.annotations.NotNull;

public class SetCommand extends CommandSeparator {

    public SetCommand(@NotNull final OreControlValues oreControlValues) {
        super(oreControlValues.getJavaPlugin());

        final OreControlMessages messages = oreControlValues.getOreControlMessages();
        final Permissions permissions = oreControlValues.getPermissions();

        registerExecutor(new SetValueCommand(oreControlValues), "value", permissions.getSetValuePermission(), messages.getCommandSetValueUsageMessage(), messages.getCommandSetValueDescriptionMessage());
        registerExecutor(new SetBiomeCommand(oreControlValues), "biome", permissions.getSetBiomePermission(), messages.getCommandSetBiomeUsageMessage(), messages.getCommandSetBiomeDescriptionMessage());

        final HelpCommand helpCommand = new HelpCommand(this, messages);
        registerExecutor(helpCommand, "help", null, null, null);
        registerExecutor(helpCommand, null, null, null, null);
    }

}
