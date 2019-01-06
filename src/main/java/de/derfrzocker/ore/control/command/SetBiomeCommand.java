package de.derfrzocker.ore.control.command;

import de.derfrzocker.ore.control.OreControl;
import de.derfrzocker.ore.control.Permissions;
import de.derfrzocker.ore.control.api.*;
import de.derfrzocker.ore.control.utils.MessageValue;
import de.derfrzocker.ore.control.utils.OreControlUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.stream.Stream;

import static de.derfrzocker.ore.control.OreControlMessages.*;

public class SetBiomeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!Permissions.SET_BIOME_PERMISSION.hasPermission(sender))
            return false;

        if (args.length != 5) {
            SET_BIOME_NOT_ENOUGH_ARGS.sendMessage(sender);
            return true;
        }

        Bukkit.getScheduler().runTaskAsynchronously(OreControl.getInstance(), () -> {
            String biome_name = args[0];
            String ore_name = args[1];
            String setting_name = args[2];
            String world_name = args[3];
            String amount = args[4];

            Biome biome;

            try {
                biome = Biome.valueOf(biome_name.toUpperCase());
            } catch (IllegalArgumentException e) {
                SET_BIOME_NOT_FOUND.sendMessage(sender, new MessageValue("biome", biome_name));
                return;
            }

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

            Setting setting;

            try {
                setting = Setting.valueOf(setting_name.toUpperCase());
            } catch (IllegalArgumentException e) {
                SET_SETTING_NOT_FOUND.sendMessage(sender, new MessageValue("setting", setting_name));
                return;
            }

            if (Stream.of(ore.getSettings()).noneMatch(value -> value == setting)) {
                SET_SETTING_NOT_VALID.sendMessage(sender, new MessageValue("setting", setting_name), new MessageValue("ore", ore_name));
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

            int value2 = percents ? (int) (OreControlUtil.getDefault(ore, setting) * (value / 100)) : (int) value;

            if (!OreControlUtil.isSave(ore, setting, value2)) {
                if (OreControl.getInstance().getConfigValues().isSaveMode()) {
                    SET_NOT_SAVE.sendMessage(sender, new MessageValue("value", String.valueOf(value2)));
                    return;
                }
                SET_NOT_SAVE_WARNING.sendMessage(sender, new MessageValue("value", String.valueOf(value2)));
            }

            OreControlUtil.setAmount(ore, setting, worldOreConfig, value2, biome);

            service.saveWorldOreConfig(worldOreConfig);
            SET_SUCCESS.sendMessage(sender);
        });

        return true;
    }

}
