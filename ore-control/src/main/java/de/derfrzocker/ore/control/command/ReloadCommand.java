package de.derfrzocker.ore.control.command;

import de.derfrzocker.ore.control.OreControl;
import de.derfrzocker.ore.control.OreControlMessages;
import de.derfrzocker.ore.control.Permissions;
import de.derfrzocker.spigot.utils.CommandUtil;
import de.derfrzocker.spigot.utils.ReloadAble;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.List;

public class ReloadCommand implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!Permissions.RELOAD_PERMISSION.hasPermission(sender))
            return false;

        CommandUtil.runAsynchronously(sender, OreControl.getInstance(), () -> {
            OreControlMessages.RELOAD_BEGIN.sendMessage(sender);

            ReloadAble.RELOAD_ABLES.forEach(ReloadAble::reload);

            OreControlMessages.RELOAD_END.sendMessage(sender);
        });

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return new ArrayList<>();
    }
}
