package de.derfrzocker.ore.control.command;

import de.derfrzocker.ore.control.OreControlMessages;
import de.derfrzocker.ore.control.Permissions;
import de.derfrzocker.ore.control.api.OreControlService;
import de.derfrzocker.ore.control.gui.WorldGui;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class GuiCommand implements TabExecutor {

    @NonNull
    private final Supplier<OreControlService> serviceSupplier;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            OreControlMessages.PLAYER_ONLY_COMMAND.sendMessage(sender);
            return true;
        }

        if (!Permissions.OPEN_GUI_PERMISSION.hasPermission(sender))
            return false;

        new WorldGui(sender, serviceSupplier).openSync((Player) sender);

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return new ArrayList<>();
    }
}
