package de.derfrzocker.ore.control.command;

import de.derfrzocker.ore.control.OreControlMessages;
import de.derfrzocker.ore.control.Permissions;
import de.derfrzocker.ore.control.gui.WorldGui;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GuiCommand implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            OreControlMessages.PLAYER_ONLY_COMMAND.sendMessage(sender);
            return true;
        }

        if (!Permissions.SET_BIOME_PERMISSION.hasPermission(sender) && !Permissions.SET_PERMISSION.hasPermission(sender))
            return false;

        new WorldGui(sender).openSync((Player) sender);

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return new ArrayList<>();
    }
}
