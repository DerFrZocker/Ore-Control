package de.derfrzocker.ore.control.utils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.HashMap;
import java.util.Map;

public abstract class CommandSeparator implements TabExecutor {

    private CommandExecutor fallBack;

    private Map<String, CommandExecutor> map = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0) {
            if (map.containsKey(""))
                if (map.get("").onCommand(sender, command, label, args))
                    return true;
            if (fallBack == null)
                return false;
            return fallBack.onCommand(sender, command, label, args);
        }

        if (map.containsKey(args[0].toLowerCase()))
            if (map.get(args[0].toLowerCase()).onCommand(sender, command, label, buildStrings(args)))
                return true;

        if (fallBack == null)
            return false;

        return fallBack.onCommand(sender, command, label, args);
    }

    public void registerExecuter(CommandExecutor executer, String key) {
        if (executer == null)
            return;

        if (key == null) {
            fallBack = executer;
            return;
        }

        map.put(key.toLowerCase(), executer);
    }

    private String[] buildStrings(String[] args) {
        String[] strings = new String[args.length - 1];

        if (args.length - 1 >= 0) System.arraycopy(args, 1, strings, 0, args.length - 1);

        return strings;
    }

}
