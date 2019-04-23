package de.derfrzocker.ore.control.command;

import de.derfrzocker.ore.control.Permissions;
import de.derfrzocker.spigot.utils.CommandSeparator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.List;

public class OreControlCommand extends CommandSeparator {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> list = new ArrayList<>();

        if (args.length == 1) {
            if ("set".startsWith(args[0].toLowerCase()) && Permissions.SET_PERMISSION.hasPermission(sender))
                list.add("set");
            if ("setbiome".startsWith(args[0].toLowerCase()) && Permissions.SET_BIOME_PERMISSION.hasPermission(sender))
                list.add("setbiome");
            if ("create".startsWith(args[0].toLowerCase()) && Permissions.CREATE_TEMPLATE_PERMISSION.hasPermission(sender))
                list.add("create");
            if ("reload".startsWith(args[0].toLowerCase()) && Permissions.RELOAD_PERMISSION.hasPermission(sender))
                list.add("reload");
            if ("help".startsWith(args[0].toLowerCase()) && Permissions.hasAnyCommandPermission(sender))
                list.add("help");
            return list;
        }

        if (args.length >= 2) {
            TabExecutor executor = getMap().get(args[0].toLowerCase());

            if (executor == null)
                return list;

            return executor.onTabComplete(sender, command, alias, args);
        }

        return list;
    }

}
