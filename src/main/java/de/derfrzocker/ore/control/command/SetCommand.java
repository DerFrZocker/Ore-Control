package de.derfrzocker.ore.control.command;

import de.derfrzocker.ore.control.OreControl;
import de.derfrzocker.ore.control.Permissions;
import de.derfrzocker.ore.control.api.Ore;
import de.derfrzocker.ore.control.api.OreControlService;
import de.derfrzocker.ore.control.api.Setting;
import de.derfrzocker.ore.control.api.WorldOreConfig;
import de.derfrzocker.ore.control.utils.CommandUtil;
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

public class SetCommand implements TabExecutor { //TODO "merge" set and setbiome command

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!Permissions.SET_PERMISSION.hasPermission(sender))
            return false;

        if (args.length != 4) {
            SET_NOT_ENOUGH_ARGS.sendMessage(sender);
            return true;
        }

        CommandUtil.runAsynchronously(sender, OreControl.getInstance(), () -> {
            final String oreName = args[0];
            final String settingName = args[1];
            final String configName = args[2];
            String amount = args[3];
            final boolean translated = OreControl.getInstance().getConfigValues().isTranslateTabCompilation();

            final Optional<Ore> optionalOre = OreControlUtil.getOre(oreName, translated);

            if (!optionalOre.isPresent()) {
                SET_ORE_NOT_FOUND.sendMessage(sender, new MessageValue("ore", oreName));
                return;
            }

            final Ore ore = optionalOre.get();

            final Optional<Setting> optionalSetting = OreControlUtil.getSetting(settingName, translated);

            if (!optionalSetting.isPresent()) {
                SET_SETTING_NOT_FOUND.sendMessage(sender, new MessageValue("setting", settingName));
                return;
            }

            final Setting setting = optionalSetting.get();

            if (Stream.of(ore.getSettings()).noneMatch(value -> value == setting)) {
                SET_SETTING_NOT_VALID.sendMessage(sender, new MessageValue("setting", settingName), new MessageValue("ore", oreName));
                return;
            }

            final OreControlService service = OreControl.getService();

            final World world = Bukkit.getWorld(configName);

            final Optional<WorldOreConfig> optionalWorldOreConfig = service.getWorldOreConfig(configName);

            if (!optionalWorldOreConfig.isPresent() && world == null) {
                SET_CONFIG_NOT_FOUND.sendMessage(sender, new MessageValue("config_name", configName));
                return;
            }

            final WorldOreConfig worldOreConfig = optionalWorldOreConfig.orElseGet(() -> service.createWorldOreConfig(world));

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

            final int value2 = percents ? (int) (OreControlUtil.getDefault(ore, setting) * (value / 100)) : (int) value;

            if (OreControlUtil.isUnSafe(setting, value2)) {
                if (OreControl.getInstance().getConfigValues().isSafeMode()) {
                    SET_NOT_SAFE.sendMessage(sender, new MessageValue("value", String.valueOf(value2)));
                    return;
                }
                SET_NOT_SAFE_WARNING.sendMessage(sender, new MessageValue("value", String.valueOf(value2)));
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
        final boolean translated = OreControl.getInstance().getConfigValues().isTranslateTabCompilation();

        if (!Permissions.SET_PERMISSION.hasPermission(sender))
            return list;

        if (args.length == 2) {
            final String oreName = args[1].toUpperCase();

            if (translated) {
                OreControlUtil.getTranslatedOres().values().stream().filter(value -> value.startsWith(oreName)).map(String::toLowerCase).forEach(list::add);
                return list;
            }

            Stream.of(Ore.values()).map(Enum::toString).filter(value -> value.startsWith(oreName)).map(String::toLowerCase).forEach(list::add);
            return list;
        }

        if (args.length == 3) {
            final Optional<Ore> ore = OreControlUtil.getOre(args[1], translated);

            if (!ore.isPresent())
                return list;

            final String settingName = args[2].toUpperCase();

            if (translated) {
                OreControlUtil.getTranslatedSettings(ore.get().getSettings()).values().stream().filter(value -> value.startsWith(settingName)).map(String::toLowerCase).forEach(list::add);
                return list;
            }

            Stream.of(ore.get().getSettings()).map(Enum::toString).filter(value -> value.startsWith(settingName)).map(String::toLowerCase).forEach(list::add);

            return list;
        }

        if (args.length == 4) {
            final Optional<Ore> ore = OreControlUtil.getOre(args[1], translated);

            if (!ore.isPresent())
                return list;

            if (!OreControlUtil.getSetting(args[2], translated, ore.get().getSettings()).isPresent())
                return list;

            final String configName = args[3].toLowerCase();

            Bukkit.getWorlds().stream().map(World::getName).filter(value -> value.toLowerCase().startsWith(configName)).forEach(list::add);
            OreControl.getService().getAllWorldOreConfigs().stream().filter(value -> !list.contains(value.getName())).map(WorldOreConfig::getName).forEach(list::add);

            return list;
        }

        if (args.length == 5) {
            final Optional<Ore> ore = OreControlUtil.getOre(args[1], translated);

            if (!ore.isPresent())
                return list;

            final Optional<Setting> setting = OreControlUtil.getSetting(args[2], translated, ore.get().getSettings());

            if (!setting.isPresent())
                return list;

            final World world = Bukkit.getWorld(args[3]);

            final Optional<WorldOreConfig> worldOreConfig = OreControl.getService().getWorldOreConfig(args[3]);

            if (!worldOreConfig.isPresent() && world == null)
                return list;

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
