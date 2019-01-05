package de.derfrzocker.ore.control.command;

import de.derfrzocker.ore.control.OreControl;
import de.derfrzocker.ore.control.Permissions;
import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.Ore;
import de.derfrzocker.ore.control.api.Setting;
import de.derfrzocker.ore.control.api.WorldOreConfig;
import de.derfrzocker.ore.control.utils.CommandSeparator;
import de.derfrzocker.ore.control.utils.OreControlUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class OreGeneratorCommand extends CommandSeparator {

    @Override // TODO move TapComplete to subcommands
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> list = new ArrayList<>();

        if (args.length == 1) {
            if ("set".startsWith(args[0].toLowerCase()) && Permissions.SET_PERMISSION.hasPermission(sender))
                list.add("set");
            if ("setbiome".startsWith(args[0].toLowerCase()) && Permissions.SET_BIOME_PERMISSION.hasPermission(sender))
                list.add("setbiome");
            if ("reload".startsWith(args[0].toLowerCase()) && Permissions.RELOAD_PERMISSION.hasPermission(sender))
                list.add("reload");
            if ("help".startsWith(args[0].toLowerCase()) && Permissions.hasAnyCommandPermission(sender))
                list.add("help");
            return list;
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("set") && Permissions.SET_PERMISSION.hasPermission(sender)) {
                for (Ore ore : Ore.values())
                    if (ore.name().startsWith(args[1].toUpperCase()))
                        list.add(ore.name().toLowerCase());
                return list;
            }
            if (args[0].equalsIgnoreCase("setbiome") && Permissions.SET_BIOME_PERMISSION.hasPermission(sender)) {
                for (Biome biome : Biome.values())
                    if (biome.name().startsWith(args[1].toUpperCase()))
                        list.add(biome.name().toLowerCase());
                return list;
            }
            if (args[0].equalsIgnoreCase("help") && Permissions.hasAnyCommandPermission(sender)) {
                if ("set".startsWith(args[1].toLowerCase()) && Permissions.SET_PERMISSION.hasPermission(sender))
                    list.add("set");
                if ("setbiome".startsWith(args[1].toLowerCase()) && Permissions.SET_BIOME_PERMISSION.hasPermission(sender))
                    list.add("setbiome");
                if ("reload".startsWith(args[1].toLowerCase()) && Permissions.RELOAD_PERMISSION.hasPermission(sender))
                    list.add("reload");
                if ("help".startsWith(args[1].toLowerCase()))
                    list.add("help");
                return list;
            }
        }

        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("set") && Permissions.SET_PERMISSION.hasPermission(sender)) {
                Optional<Ore> ore = Stream.of(Ore.values()).filter(value -> value.name().equalsIgnoreCase(args[1])).findAny();

                if (!ore.isPresent())
                    return list;

                final String arg = args[2].toLowerCase();

                Stream.of(ore.get().getSettings()).map(value -> value.toString().toLowerCase()).filter(value -> value.startsWith(arg)).forEach(list::add);

                return list;
            }
            if (args[0].equalsIgnoreCase("setbiome") && Permissions.SET_BIOME_PERMISSION.hasPermission(sender)) {
                Optional<Biome> biome = Stream.of(Biome.values()).filter(value -> value.name().equalsIgnoreCase(args[1])).findAny();

                if (!biome.isPresent())
                    return list;

                final String arg = args[2].toLowerCase();

                Stream.of(Ore.values()).map(value -> value.toString().toLowerCase()).filter(value -> value.startsWith(arg)).forEach(list::add);

                return list;
            }
        }

        if (args.length == 4) {
            if (args[0].equalsIgnoreCase("set") && Permissions.SET_PERMISSION.hasPermission(sender)) {
                Optional<Ore> ore = Stream.of(Ore.values()).filter(value -> value.name().equalsIgnoreCase(args[1])).findAny();

                if (!ore.isPresent())
                    return list;

                {
                    final String arg = args[2].toLowerCase();

                    Optional<String> setting = Stream.of(ore.get().getSettings()).map(Enum::toString).filter(value -> value.equalsIgnoreCase(arg)).findAny();

                    if (!setting.isPresent())
                        return list;
                }

                final String arg = args[3].toLowerCase();

                Bukkit.getWorlds().stream().filter(value -> value.getName().toLowerCase().startsWith(arg)).map(World::getName).forEach(list::add);

                return list;
            }
            if (args[0].equalsIgnoreCase("setbiome") && Permissions.SET_BIOME_PERMISSION.hasPermission(sender)) {
                Optional<Ore> ore = Stream.of(Ore.values()).filter(value -> value.name().equalsIgnoreCase(args[2])).findAny();

                if (!ore.isPresent())
                    return list;

                final String arg = args[3].toLowerCase();

                Stream.of(ore.get().getSettings()).map(value -> value.toString().toLowerCase()).filter(value -> value.startsWith(arg)).forEach(list::add);

                return list;
            }
        }

        if (args.length == 5) {
            if (args[0].equalsIgnoreCase("set") && Permissions.SET_PERMISSION.hasPermission(sender)) {
                Optional<Ore> ore = Stream.of(Ore.values()).filter(value -> value.name().equalsIgnoreCase(args[1])).findAny();

                if (!ore.isPresent())
                    return list;

                Optional<Setting> setting;

                {
                    final String arg = args[2].toLowerCase();

                    setting = Stream.of(ore.get().getSettings()).filter(value -> value.toString().equalsIgnoreCase(arg)).findAny();

                    if (!setting.isPresent())
                        return list;
                }

                final String arg = args[3].toLowerCase();

                Optional<World> world = Bukkit.getWorlds().stream().filter(value -> value.getName().toLowerCase().startsWith(arg)).findAny();

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
            if (args[0].equalsIgnoreCase("setbiome") && Permissions.SET_BIOME_PERMISSION.hasPermission(sender)) {
                Optional<Ore> ore = Stream.of(Ore.values()).filter(value -> value.name().equalsIgnoreCase(args[2])).findAny();

                if (!ore.isPresent())
                    return list;

                {
                    final String arg = args[3].toLowerCase();

                    Optional<String> setting = Stream.of(ore.get().getSettings()).map(Enum::toString).filter(value -> value.equalsIgnoreCase(arg)).findAny();

                    if (!setting.isPresent())
                        return list;
                }

                final String arg = args[4].toLowerCase();

                Bukkit.getWorlds().stream().filter(value -> value.getName().toLowerCase().startsWith(arg)).map(World::getName).forEach(list::add);

                return list;
            }
        }
        if (args.length == 6) {
            if (args[0].equalsIgnoreCase("setbiome") && Permissions.SET_BIOME_PERMISSION.hasPermission(sender)) {
                Optional<Biome> biome = Stream.of(Biome.values()).filter(value -> value.name().equalsIgnoreCase(args[1])).findAny();

                if (!biome.isPresent())
                    return list;

                Optional<Ore> ore = Stream.of(Ore.values()).filter(value -> value.name().equalsIgnoreCase(args[2])).findAny();

                if (!ore.isPresent())
                    return list;

                Optional<Setting> setting;

                {
                    final String arg = args[3].toLowerCase();

                    setting = Stream.of(ore.get().getSettings()).filter(value -> value.toString().equalsIgnoreCase(arg)).findAny();

                    if (!setting.isPresent())
                        return list;
                }

                final String arg = args[4].toLowerCase();

                Optional<World> world = Bukkit.getWorlds().stream().filter(value -> value.getName().toLowerCase().startsWith(arg)).findAny();

                if (!world.isPresent())
                    return list;

                Optional<WorldOreConfig> worldOreConfig = OreControl.getService().getWorldOreConfig(world.get());

                if (!worldOreConfig.isPresent()) {
                    list.add("current: " + OreControlUtil.getDefault(ore.get(), setting.get()));
                    return list;
                }

                list.add("current: " + OreControlUtil.getAmount(ore.get(), setting.get(), worldOreConfig.get(), biome.get()));

                return list;
            }
        }


        return list;
    }

}
