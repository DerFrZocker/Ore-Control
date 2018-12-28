package de.derfrzocker.ore.control.command;

import de.derfrzocker.ore.control.OreControl;
import de.derfrzocker.ore.control.OreControlMessages;
import de.derfrzocker.ore.control.Permissions;
import de.derfrzocker.ore.control.utils.Config;
import de.derfrzocker.ore.control.utils.ReloadAble;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!Permissions.RELOAD_PERMISSION.hasPermission(sender))
            return false;

        Bukkit.getScheduler().runTaskAsynchronously(OreControl.getInstance(), () -> {
            OreControlMessages.RELOAD_BEGIN.sendMessage(sender);

            OreControlMessages.getInstance().setFile(Config.getConfig(OreControl.getInstance(), "messages"));
            OreControl.getInstance().getReloadAbles().forEach(ReloadAble::reload);

            OreControlMessages.RELOAD_END.sendMessage(sender);
        });

        return true;
    }

}
