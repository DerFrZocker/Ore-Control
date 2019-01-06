package de.derfrzocker.ore.control.command;

import de.derfrzocker.ore.control.OreControl;
import de.derfrzocker.ore.control.Permissions;
import org.bukkit.Bukkit;
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

        Bukkit.getScheduler().runTaskAsynchronously(OreControl.getInstance(), () -> {

            if (args.length == 1) {
                if ("set".equalsIgnoreCase(args[0])) {
                    sendSetHelp(sender);
                    return;
                }
                if ("setbiome".equalsIgnoreCase(args[0])) {
                    sendSetBiomeHelp(sender);
                    return;
                }
                if ("reload".equalsIgnoreCase(args[0])) {
                    sendReloadHelp(sender);
                    return;
                }
                if ("help".equalsIgnoreCase(args[0])) {
                    sendHelp(sender);
                    return;
                }
            }

            HELP_HEADER.sendMessage(sender);

            boolean b = false;

            if (Permissions.SET_PERMISSION.hasPermission(sender)) {
                HELP_SET_COMMAND.sendMessage(sender);
                b = true;
            }

            if (Permissions.SET_BIOME_PERMISSION.hasPermission(sender)) {
                if (b)
                    HELP_SEPARATOR.sendMessage(sender);
                HELP_SET_BIOME_COMMAND.sendMessage(sender);
                b = true;
            }

            if (Permissions.RELOAD_PERMISSION.hasPermission(sender)) {
                if (b)
                    HELP_SEPARATOR.sendMessage(sender);
                HELP_RELOAD_COMMAND.sendMessage(sender);
                b = true;
            }

            if (b)
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
            if ("reload".startsWith(subcommand) && Permissions.RELOAD_PERMISSION.hasPermission(sender))
                list.add("reload");
            if ("help".startsWith(subcommand))
                list.add("help");

            return list;
        }

        return list;
    }
}
