package de.derfrzocker.ore.control.command;

import de.derfrzocker.ore.control.OreControl;
import de.derfrzocker.ore.control.Permissions;
import de.derfrzocker.spigot.utils.CommandUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.List;

import static de.derfrzocker.ore.control.OreControlMessages.*;

public class HelpCommand implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!Permissions.hasAnyCommandPermission(sender))
            return false;

        CommandUtil.runAsynchronously(sender, OreControl.getInstance(), () -> {

            if (args.length == 1) {
                if ("set".equalsIgnoreCase(args[0]) && Permissions.SET_PERMISSION.hasPermission(sender)) {
                    sendSetHelp(sender);
                    return;
                }
                if ("setbiome".equalsIgnoreCase(args[0]) && Permissions.SET_BIOME_PERMISSION.hasPermission(sender)) {
                    sendSetBiomeHelp(sender);
                    return;
                }
                if ("create".equalsIgnoreCase(args[0]) && Permissions.CREATE_TEMPLATE_PERMISSION.hasPermission(sender)) {
                    sendCreateHelp(sender);
                    return;
                }
                if ("reload".equalsIgnoreCase(args[0]) && Permissions.RELOAD_PERMISSION.hasPermission(sender)) {
                    sendReloadHelp(sender);
                    return;
                }
                if ("help".equalsIgnoreCase(args[0])) {
                    sendHelp(sender);
                    return;
                }
            }

            HELP_HEADER.sendMessage(sender);

            boolean seperator = false;

            if (Permissions.SET_PERMISSION.hasPermission(sender)) {
                HELP_SET_COMMAND.sendMessage(sender);
                seperator = true;
            }

            if (Permissions.SET_BIOME_PERMISSION.hasPermission(sender)) {
                if (seperator)
                    HELP_SEPARATOR.sendMessage(sender);
                HELP_SET_BIOME_COMMAND.sendMessage(sender);
                seperator = true;
            }

            if (Permissions.CREATE_TEMPLATE_PERMISSION.hasPermission(sender)) {
                if (seperator)
                    HELP_SEPARATOR.sendMessage(sender);
                HELP_CREATE_COMMAND.sendMessage(sender);
                seperator = true;
            }

            if (Permissions.RELOAD_PERMISSION.hasPermission(sender)) {
                if (seperator)
                    HELP_SEPARATOR.sendMessage(sender);
                HELP_RELOAD_COMMAND.sendMessage(sender);
                seperator = true;
            }

            if (seperator)
                HELP_SEPARATOR.sendMessage(sender);
            HELP_COMMAND.sendMessage(sender);

            HELP_FOOTER.sendMessage(sender);
        });

        return true;
    }

    private void sendSetBiomeHelp(CommandSender sender) {
        HELP_HEADER.sendMessage(sender);
        HELP_SET_BIOME_COMMAND.sendMessage(sender);
        HELP_SET_BIOME_DESCRIPTION.sendMessage(sender);
        HELP_FOOTER.sendMessage(sender);
    }

    private void sendSetHelp(CommandSender sender) {
        HELP_HEADER.sendMessage(sender);
        HELP_SET_COMMAND.sendMessage(sender);
        HELP_SET_DESCRIPTION.sendMessage(sender);
        HELP_FOOTER.sendMessage(sender);
    }

    private void sendCreateHelp(CommandSender sender) {
        HELP_HEADER.sendMessage(sender);
        HELP_CREATE_COMMAND.sendMessage(sender);
        HELP_CREATE_DESCRIPTION.sendMessage(sender);
        HELP_FOOTER.sendMessage(sender);
    }

    private void sendReloadHelp(CommandSender sender) {
        HELP_HEADER.sendMessage(sender);
        HELP_RELOAD_COMMAND.sendMessage(sender);
        HELP_RELOAD_DESCRIPTION.sendMessage(sender);
        HELP_FOOTER.sendMessage(sender);
    }

    private void sendHelp(CommandSender sender) {
        HELP_HEADER.sendMessage(sender);
        HELP_COMMAND.sendMessage(sender);
        HELP_DESCRIPTION.sendMessage(sender);
        HELP_FOOTER.sendMessage(sender);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        final List<String> list = new ArrayList<>();

        if (args.length == 2 && Permissions.hasAnyCommandPermission(sender)) {
            final String subcommand = args[1].toLowerCase();

            if ("set".startsWith(subcommand) && Permissions.SET_PERMISSION.hasPermission(sender))
                list.add("set");
            if ("setbiome".startsWith(subcommand) && Permissions.SET_BIOME_PERMISSION.hasPermission(sender))
                list.add("setbiome");
            if ("create".startsWith(subcommand) && Permissions.CREATE_TEMPLATE_PERMISSION.hasPermission(sender))
                list.add("create");
            if ("reload".startsWith(subcommand) && Permissions.RELOAD_PERMISSION.hasPermission(sender))
                list.add("reload");
            if ("help".startsWith(subcommand))
                list.add("help");

            return list;
        }

        return list;
    }
}
