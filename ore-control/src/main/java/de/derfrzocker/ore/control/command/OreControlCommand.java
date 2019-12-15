package de.derfrzocker.ore.control.command;

import de.derfrzocker.ore.control.OreControlMessages;
import de.derfrzocker.ore.control.Permissions;
import de.derfrzocker.ore.control.command.set.SetCommand;
import de.derfrzocker.ore.control.utils.OreControlValues;
import de.derfrzocker.spigot.utils.command.CommandSeparator;
import de.derfrzocker.spigot.utils.command.HelpCommand;
import org.jetbrains.annotations.NotNull;

public class OreControlCommand extends CommandSeparator {

    public OreControlCommand(@NotNull final OreControlValues oreControlValues) {
        super(oreControlValues.getJavaPlugin());

        final OreControlMessages oreControlMessages = oreControlValues.getOreControlMessages();
        final Permissions permissions = oreControlValues.getPermissions();

        registerExecutor(new SetCommand(oreControlValues), "set", permissions.getSetPermission(), null, null);
        registerExecutor(new CreateCommand(oreControlValues), "create", permissions.getTemplateCreatePermission(), oreControlMessages.getCommandCreateUsageMessage(), oreControlMessages.getCommandCreateDescriptionMessage());
        registerExecutor(new GuiCommand(oreControlValues), "", permissions.getGuiPermission(), null, null);
        registerExecutor(new ReloadCommand(oreControlValues), "reload", permissions.getReloadPermission(), oreControlMessages.getCommandReloadUsageMessage(), oreControlMessages.getCommandReloadDescriptionMessage());

        final HelpCommand helpCommand = new HelpCommand(this, oreControlMessages);
        registerExecutor(helpCommand, "help", null, oreControlMessages.getCommandHelpUsageMessage(), oreControlMessages.getCommandHelpDescriptionMessage());
        registerExecutor(helpCommand, null, null, oreControlMessages.getCommandHelpUsageMessage(), oreControlMessages.getCommandHelpDescriptionMessage());
    }

}
