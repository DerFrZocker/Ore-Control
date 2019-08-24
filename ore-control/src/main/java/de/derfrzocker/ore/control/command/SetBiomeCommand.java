package de.derfrzocker.ore.control.command;

import de.derfrzocker.ore.control.OreControl;
import de.derfrzocker.ore.control.Permissions;
import de.derfrzocker.ore.control.api.*;
import de.derfrzocker.ore.control.utils.OreControlUtil;
import de.derfrzocker.spigot.utils.CommandUtil;
import de.derfrzocker.spigot.utils.message.MessageValue;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static de.derfrzocker.ore.control.OreControlMessages.*;

@RequiredArgsConstructor
public class SetBiomeCommand implements TabExecutor { //TODO "merge" set and setbiome command

    @NonNull
    private final Supplier<OreControlService> serviceSupplier;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!Permissions.SET_BIOME_PERMISSION.hasPermission(sender))
            return false;

        if (args.length != 5) {
            SET_BIOME_NOT_ENOUGH_ARGS.sendMessage(sender);
            return true;
        }

        CommandUtil.runAsynchronously(sender, OreControl.getInstance(), () -> {
            final String biomeName = args[0];
            final String oreName = args[1];
            final String settingName = args[2];
            final String configName = args[3];
            String amount = args[4];
            final boolean translated = OreControl.getInstance().getConfigValues().isTranslateTabCompilation();

            Optional<Biome> optionalBiome = OreControlUtil.getBiome(biomeName, translated);

            if (!optionalBiome.isPresent()) {
                SET_BIOME_NOT_FOUND.sendMessage(sender, new MessageValue("biome", biomeName));
                return;
            }

            final Biome biome = optionalBiome.get();

            final Optional<Ore> optionalOre = OreControlUtil.getOre(oreName, translated);

            if (!optionalOre.isPresent()) {
                SET_ORE_NOT_FOUND.sendMessage(sender, new MessageValue("ore", oreName));
                return;
            }

            final Ore ore = optionalOre.get();

            if (Stream.of(biome.getOres()).noneMatch(value -> value == ore)) {
                SET_BIOME_ORE_NOT_VALID.sendMessage(sender, new MessageValue("ore", oreName), new MessageValue("biome", biomeName));
                return;
            }

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

            final OreControlService service = serviceSupplier.get();

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

            int value2 = percents ? (int) (OreControlUtil.getDefault(ore, setting) * (value / 100)) : (int) value;

            if (OreControlUtil.isUnSafe(setting, value2)) {
                if (OreControl.getInstance().getConfigValues().isSafeMode()) {
                    SET_NOT_SAFE.sendMessage(sender, new MessageValue("value", String.valueOf(value2)));
                    return;
                }
                SET_NOT_SAFE_WARNING.sendMessage(sender, new MessageValue("value", String.valueOf(value2)));
            }

            OreControlUtil.setAmount(ore, setting, worldOreConfig, value2, biome);

            service.saveWorldOreConfig(worldOreConfig);
            SET_SUCCESS.sendMessage(sender);
        });

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        final List<String> list = new ArrayList<>();
        final boolean translated = OreControl.getInstance().getConfigValues().isTranslateTabCompilation();

        if (!Permissions.SET_BIOME_PERMISSION.hasPermission(sender))
            return list;

        if (args.length == 2) {
            final String biomeName = args[1].toUpperCase();

            if (translated) {
                OreControlUtil.getTranslatedBiomes().values().stream().filter(value -> value.startsWith(biomeName)).map(String::toLowerCase).forEach(list::add);
                return list;
            }

            Stream.of(Biome.values()).map(Enum::toString).filter(value -> value.contains(biomeName)).map(String::toLowerCase).forEach(list::add);
            return list;
        }

        if (args.length == 3) {
            final Optional<Biome> biome = OreControlUtil.getBiome(args[1], translated);

            if (!biome.isPresent())
                return list;

            final String oreName = args[2].toUpperCase();

            if (translated) {
                OreControlUtil.getTranslatedOres(biome.get().getOres()).values().stream().filter(value -> value.startsWith(oreName)).map(String::toLowerCase).forEach(list::add);
                return list;
            }

            Stream.of(biome.get().getOres()).map(Enum::toString).filter(value -> value.startsWith(oreName)).map(String::toLowerCase).forEach(list::add);
            return list;
        }

        if (args.length == 4) {
            final Optional<Biome> biome = OreControlUtil.getBiome(args[1], translated);

            if (!biome.isPresent())
                return list;

            final Optional<Ore> ore = OreControlUtil.getOre(args[2], translated, biome.get().getOres());

            if (!ore.isPresent())
                return list;

            final String settingName = args[3].toUpperCase();

            if (translated) {
                OreControlUtil.getTranslatedSettings(ore.get().getSettings()).values().stream().filter(value -> value.startsWith(settingName)).map(String::toLowerCase).forEach(list::add);
                return list;
            }

            Stream.of(ore.get().getSettings()).map(Enum::toString).filter(value -> value.startsWith(settingName)).map(String::toLowerCase).forEach(list::add);

            return list;
        }

        if (args.length == 5) {
            final Optional<Biome> biome = OreControlUtil.getBiome(args[1], translated);

            if (!biome.isPresent())
                return list;

            final Optional<Ore> ore = OreControlUtil.getOre(args[2], translated, biome.get().getOres());

            if (!ore.isPresent())
                return list;

            if (!OreControlUtil.getSetting(args[3], translated, ore.get().getSettings()).isPresent())
                return list;

            final String worldName = args[4].toLowerCase();

            Bukkit.getWorlds().stream().map(World::getName).filter(value -> value.toLowerCase().startsWith(worldName)).forEach(list::add);
            serviceSupplier.get().getAllWorldOreConfigs().stream().filter(value -> !list.contains(value.getName())).map(WorldOreConfig::getName).forEach(list::add);

            return list;
        }

        if (args.length == 6) {
            final Optional<Biome> biome = OreControlUtil.getBiome(args[1], translated);

            if (!biome.isPresent())
                return list;

            final Optional<Ore> ore = OreControlUtil.getOre(args[2], translated, biome.get().getOres());

            if (!ore.isPresent())
                return list;

            final Optional<Setting> setting = OreControlUtil.getSetting(args[3], translated, ore.get().getSettings());

            if (!setting.isPresent())
                return list;

            final World world = Bukkit.getWorld(args[4]);

            final Optional<WorldOreConfig> worldOreConfig = serviceSupplier.get().getWorldOreConfig(args[4]);

            if (!worldOreConfig.isPresent() && world == null)
                return list;

            if (!worldOreConfig.isPresent()) {
                list.add("current: " + OreControlUtil.getDefault(ore.get(), setting.get()));
                return list;
            }

            list.add("current: " + OreControlUtil.getAmount(ore.get(), setting.get(), worldOreConfig.get(), biome.get()));

            return list;
        }

        return list;
    }
}
