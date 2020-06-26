/*
 * MIT License
 *
 * Copyright (c) 2019 Marvin (DerFrZocker)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.derfrzocker.ore.control.command.set;

import de.derfrzocker.ore.control.OreControlMessages;
import de.derfrzocker.ore.control.api.*;
import de.derfrzocker.ore.control.utils.OreControlUtil;
import de.derfrzocker.ore.control.utils.OreControlValues;
import de.derfrzocker.spigot.utils.command.CommandUtil;
import de.derfrzocker.spigot.utils.message.MessageValue;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class SetBiomeCommand implements TabExecutor { //TODO "merge" set and setbiome command

    @NotNull
    private final OreControlValues oreControlValues;

    public SetBiomeCommand(@NotNull final OreControlValues oreControlValues) {
        Validate.notNull(oreControlValues, "OreControlValues can't be null");

        this.oreControlValues = oreControlValues;
    }

    @Override
    public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String label, @NotNull final String[] args) {
        if (args.length != 5) {
            oreControlValues.getOreControlMessages().getCommandSetBiomeNotEnoughArgsMessage().sendMessage(sender);
            return true;
        }

        CommandUtil.runAsynchronously(sender, oreControlValues.getJavaPlugin(), () -> {
            final String biomeName = args[0];
            final String oreName = args[1];
            final String settingName = args[2];
            final String configName = args[3];
            String amount = args[4];
            final boolean translated = oreControlValues.getConfigValues().isTranslateTabCompilation();
            final OreControlMessages messages = oreControlValues.getOreControlMessages();

            final Optional<Biome> optionalBiome = OreControlUtil.getBiome(biomeName, translated);

            if (!optionalBiome.isPresent()) {
                messages.getBiomeNotFoundMessage().sendMessage(sender, new MessageValue("biome", biomeName));
                return;
            }

            final Biome biome = optionalBiome.get();

            final Optional<Ore> optionalOre = OreControlUtil.getOre(oreName, translated);

            if (!optionalOre.isPresent()) {
                messages.getOreNotFoundMessage().sendMessage(sender, new MessageValue("ore", oreName));
                return;
            }

            final Ore ore = optionalOre.get();

            if (Stream.of(biome.getOres()).noneMatch(value -> value == ore)) {
                messages.getOreNotValidMessage().sendMessage(sender, new MessageValue("ore", oreName), new MessageValue("biome", biomeName));
                return;
            }

            final Optional<Setting> optionalSetting = OreControlUtil.getSetting(settingName, translated);

            if (!optionalSetting.isPresent()) {
                messages.getSettingNotFoundMessage().sendMessage(sender, new MessageValue("setting", settingName));
                return;
            }

            final Setting setting = optionalSetting.get();

            if (Stream.of(ore.getSettings()).noneMatch(value -> value == setting)) {
                messages.getSettingNotValidMessage().sendMessage(sender, new MessageValue("setting", settingName), new MessageValue("ore", oreName));
                return;
            }

            final OreControlService service = oreControlValues.getService();

            final World world = Bukkit.getWorld(configName);

            final Optional<WorldOreConfig> optionalWorldOreConfig = service.getWorldOreConfig(configName);

            if (!optionalWorldOreConfig.isPresent() && world == null) {
                messages.getWorldConfigNotFoundMessage().sendMessage(sender, new MessageValue("world-config", configName));
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
                value = Double.parseDouble(amount);
            } catch (final NumberFormatException e) {
                messages.getNumberNotValidMessage().sendMessage(sender, new MessageValue("value", amount));
                return;
            }

            final int value2 = percents ? (int) (service.getDefaultValue(biome, ore, setting) * (value / 100)) : (int) value;

            if (OreControlUtil.isUnSafe(setting, value2)) {
                if (oreControlValues.getConfigValues().isSafeMode()) {
                    messages.getNumberNotSafeMessage().sendMessage(sender, new MessageValue("value", String.valueOf(value2)));
                    return;
                }
                messages.getNumberNotSafeWarningMessage().sendMessage(sender, new MessageValue("value", String.valueOf(value2)));
            }

            service.setValue(worldOreConfig, biome, ore, setting, value2);

            service.saveWorldOreConfig(worldOreConfig);
            messages.getCommandSetBiomeSuccessMessage().sendMessage(sender);
        });

        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String alias, @NotNull final String[] args) {
        final List<String> list = new ArrayList<>();
        final boolean translated = oreControlValues.getConfigValues().isTranslateTabCompilation();

        if (args.length == 1) {
            final String biomeName = args[0].toUpperCase();

            if (translated) {
                OreControlUtil.getTranslatedBiomes().values().stream().filter(value -> value.startsWith(biomeName)).map(String::toLowerCase).forEach(list::add);
                return list;
            }

            Stream.of(Biome.values()).map(Enum::toString).filter(value -> value.contains(biomeName)).map(String::toLowerCase).forEach(list::add);
            return list;
        }

        if (args.length == 2) {
            final Optional<Biome> biome = OreControlUtil.getBiome(args[0], translated);

            if (!biome.isPresent())
                return list;

            final String oreName = args[1].toUpperCase();

            if (translated) {
                OreControlUtil.getTranslatedOres(biome.get().getOres()).values().stream().filter(value -> value.startsWith(oreName)).map(String::toLowerCase).forEach(list::add);
                return list;
            }

            Stream.of(biome.get().getOres()).map(Enum::toString).filter(value -> value.startsWith(oreName)).map(String::toLowerCase).forEach(list::add);
            return list;
        }

        if (args.length == 3) {
            final Optional<Biome> biome = OreControlUtil.getBiome(args[0], translated);

            if (!biome.isPresent())
                return list;

            final Optional<Ore> ore = OreControlUtil.getOre(args[1], translated, biome.get().getOres());

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
            final Optional<Biome> biome = OreControlUtil.getBiome(args[0], translated);

            if (!biome.isPresent())
                return list;

            final Optional<Ore> ore = OreControlUtil.getOre(args[1], translated, biome.get().getOres());

            if (!ore.isPresent())
                return list;

            if (!OreControlUtil.getSetting(args[2], translated, ore.get().getSettings()).isPresent())
                return list;

            final String worldName = args[3].toLowerCase();

            Bukkit.getWorlds().stream().map(World::getName).filter(value -> value.toLowerCase().startsWith(worldName)).forEach(list::add);
            oreControlValues.getService().getAllWorldOreConfigs().stream().filter(value -> !list.contains(value.getName())).map(WorldOreConfig::getName).forEach(list::add);

            return list;
        }

        if (args.length == 5) {
            final Optional<Biome> biome = OreControlUtil.getBiome(args[0], translated);

            if (!biome.isPresent())
                return list;

            final Optional<Ore> ore = OreControlUtil.getOre(args[1], translated, biome.get().getOres());

            if (!ore.isPresent())
                return list;

            final Optional<Setting> setting = OreControlUtil.getSetting(args[2], translated, ore.get().getSettings());

            if (!setting.isPresent())
                return list;

            final World world = Bukkit.getWorld(args[3]);

            final OreControlService service = oreControlValues.getService();
            final Optional<WorldOreConfig> worldOreConfig = service.getWorldOreConfig(args[3]);

            if (!worldOreConfig.isPresent() && world == null)
                return list;

            if (!worldOreConfig.isPresent()) {
                list.add("current: " + service.getDefaultValue(biome.get(), ore.get(), setting.get()));
                return list;
            }

            list.add("current: " + service.getValue(worldOreConfig.get(), biome.get(), ore.get(), setting.get()));

            return list;
        }

        return list;
    }

}
