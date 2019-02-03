package de.derfrzocker.ore.control.command;

import de.derfrzocker.ore.control.OreControl;
import de.derfrzocker.ore.control.Permissions;
import de.derfrzocker.ore.control.api.Ore;
import de.derfrzocker.ore.control.api.OreControlService;
import de.derfrzocker.ore.control.api.Setting;
import de.derfrzocker.ore.control.api.WorldOreConfig;
import de.derfrzocker.ore.control.utils.MessageValue;
import de.derfrzocker.ore.control.utils.OreControlUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static de.derfrzocker.ore.control.OreControlMessages.*;

public class SetCommand implements TabExecutor {

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
            String setting_name = args[1];
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

            if (!OreControlUtil.isSave(setting, value2)) {
                if (OreControl.getInstance().getConfigValues().isSaveMode()) {
                    SET_NOT_SAVE.sendMessage(sender, new MessageValue("value", String.valueOf(value2)));
                    return;
                }
                SET_NOT_SAVE_WARNING.sendMessage(sender, new MessageValue("value", String.valueOf(value2)));
            }

            OreControlUtil.setAmount(ore, setting, worldOreConfig, value2);

            service.saveWorldOreConfig(worldOreConfig);
            SET_SUCCESS.sendMessage(sender);
        });

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        final List<String> list = new ArrayList<>();

        if (!Permissions.SET_PERMISSION.hasPermission(sender))
            return list;

        if (args.length == 2) {
            final String ore_name = args[1].toUpperCase();
            Stream.of(Ore.values()).map(Enum::toString).filter(value -> value.startsWith(ore_name)).map(String::toLowerCase).forEach(list::add);
            return list;
        }

        if (args.length == 3) {
            Optional<Ore> ore = Stream.of(Ore.values()).filter(value -> value.toString().equalsIgnoreCase(args[1])).findAny();

            if (!ore.isPresent())
                return list;

            final String setting_name = args[2].toUpperCase();

            Stream.of(ore.get().getSettings()).map(Enum::toString).filter(value -> value.startsWith(setting_name)).map(String::toLowerCase).forEach(list::add);

            return list;
        }

        if (args.length == 4) {
            Optional<Ore> ore = Stream.of(Ore.values()).filter(value -> value.toString().equalsIgnoreCase(args[1])).findAny();

            if (!ore.isPresent())
                return list;

            if (Stream.of(ore.get().getSettings()).map(Enum::toString).noneMatch(value -> value.equalsIgnoreCase(args[2])))
                return list;

            final String world_name = args[3].toLowerCase();

            Bukkit.getWorlds().stream().map(World::getName).filter(value -> value.toLowerCase().startsWith(world_name)).forEach(list::add);

            return list;
        }

        if (args.length == 5) {

            Optional<Ore> ore = Stream.of(Ore.values()).filter(value -> value.toString().equalsIgnoreCase(args[1])).findAny();

            if (!ore.isPresent())
                return list;

            Optional<Setting> setting = Stream.of(ore.get().getSettings()).filter(value -> value.toString().equalsIgnoreCase(args[2])).findAny();

            if (!setting.isPresent())
                return list;

            Optional<World> world = Bukkit.getWorlds().stream().filter(value -> value.getName().equalsIgnoreCase(args[3])).findAny();

            if (!world.isPresent())
                return list;

            Optional<WorldOreConfig> worldOreConfig = OreControl.getService().getWorldOreConfig(world.get());

            if (!worldOreConfig.isPresent()) {
                list.add("current: " + OreControlUtil.getDefault(ore.get(), setting.get()));
                return list;
            }

            list.add("current: " + OreControlUtil.getAmount(ore.get(), setting.get(), worldOreConfig.get()));

            return list;
        }

        return list;
    }
}
