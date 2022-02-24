/*
 * MIT License
 *
 * Copyright (c) 2019 - 2021 Marvin (DerFrZocker)
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
 *
 */

package de.derfrzocker.ore.control;

import de.derfrzocker.ore.control.api.OreControlManager;
import de.derfrzocker.ore.control.api.OreControlRegistries;
import de.derfrzocker.ore.control.api.config.ConfigManager;
import de.derfrzocker.ore.control.api.config.dao.ConfigDao;
import de.derfrzocker.ore.control.api.config.dao.ConfigInfoDao;
import de.derfrzocker.ore.control.gui.GuiSetting;
import de.derfrzocker.ore.control.gui.OreControlGuiManager;
import de.derfrzocker.ore.control.impl.v1_18_R1.NMSReplacer_v1_18_R1;
import org.bstats.bukkit.Metrics;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class OreControl extends JavaPlugin implements Listener {

    OreControlManager oreControlManager;
    OreControlGuiManager guiManager;
    List<GuiSetting> guiSettings = new ArrayList<>();

    @Override
    public void onEnable() {
        OreControlRegistries registries = new OreControlRegistries();
        ConfigDao configDao = new ConfigDao(registries);
        ConfigInfoDao configInfoDao = new ConfigInfoDao(this, new File(getDataFolder(), "data/configs"), new File(getDataFolder(), "data/global"));
        ConfigManager configManager = new ConfigManager(configDao, configInfoDao);
        configManager.reload();
        NMSReplacer_v1_18_R1 nmsReplacer = new NMSReplacer_v1_18_R1(registries, configManager);
        nmsReplacer.register();
        File defaults = new File(getDataFolder(), "data/default");
        nmsReplacer.saveDefaultValues(defaults);

        nmsReplacer.hookIntoBiomes();

        new Metrics(this, 4244);

        oreControlManager = new OreControlManager(registries, configManager, world -> new HashSet<>());
        guiManager = new OreControlGuiManager(this, oreControlManager, name -> {
            GuiSetting guiSetting = new GuiSetting(() -> YamlConfiguration.loadConfiguration(new File(getDataFolder(), "gui/default/" + name)));
            guiSettings.add(guiSetting);
            return guiSetting;
        });
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;

        guiSettings.forEach(GuiSetting::reload);
        guiManager.openGui(player);

        return true;
    }
}
