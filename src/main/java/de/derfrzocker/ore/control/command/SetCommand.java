package de.derfrzocker.ore.control.command;

import de.derfrzocker.ore.control.OreControl;
import de.derfrzocker.ore.control.Permissions;
import de.derfrzocker.ore.control.api.Ore;
import de.derfrzocker.ore.control.api.OreControlService;
import de.derfrzocker.ore.control.api.WorldOreConfig;
import de.derfrzocker.ore.control.utils.MessageValue;
import de.derfrzocker.ore.control.utils.OreControlUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import static de.derfrzocker.ore.control.OreControlMessages.*;

public class SetCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!Permissions.SET_PERMISSION.hasPermission(sender))
            return false;

        if (args.length != 4) {
            SET_NOT_ENOUGH_ARGS.sendMessage(sender);
            return true;
        }

        Bukkit.getScheduler().runTaskAsynchronously(OreControl.getInstance(), () -> {
            String ore_name = args[0];
            String type = args[1];
            String world_name = args[2];
            String amount = args[3];

            World world = Bukkit.getWorld(world_name);

            if (world == null) {
                SET_WORLD_NOT_FOUND.sendMessage(sender, new MessageValue("world_name", world_name));
                return;
            }

            Ore ore;

            try {
                ore = Ore.valueOf(ore_name.toUpperCase());
            } catch (IllegalArgumentException e) {
                SET_ORE_NOT_FOUND.sendMessage(sender, new MessageValue("ore", ore_name));
                return;
            }

            OreControlService service = OreControl.getService();

            WorldOreConfig worldOreConfig = service.getWorldOreConfig(world).orElseGet(() -> service.createWorldOreConfig(world));

            double value;
            boolean percents = false;

            if (amount.endsWith("%")) {
                amount = amount.replace("%", "");
                percents = true;
            }

            try {
                value = Double.valueOf(amount);
            } catch (NumberFormatException e) {
                SET_NO_NUMBER.sendMessage(sender, new MessageValue("value", amount));
                return;
            }

            int value2 = percents ? (int) (OreControlUtil.getDefault(ore, type) * (value / 100)) : (int) value;

            if (!OreControlUtil.isSave(ore, type, value2)) {
                if (OreControl.getInstance().getConfigValues().isSaveMode()) {
                    SET_NOT_SAVE.sendMessage(sender, new MessageValue("value", String.valueOf(value2)));
                    return;
                }
                SET_NOT_SAVE_WARNING.sendMessage(sender, new MessageValue("value", String.valueOf(value2)));
            }

            try {
                OreControlUtil.setAmount(ore, type, worldOreConfig, value2);
            } catch (IllegalArgumentException e) {
                SET_TYPE_NOT_FOUND.sendMessage(sender, new MessageValue("type", type));
                return;
            }

            service.saveWorldOreConfig(worldOreConfig);
            SET_SUCCESS.sendMessage(sender);
        });

        return true;
    }

}
