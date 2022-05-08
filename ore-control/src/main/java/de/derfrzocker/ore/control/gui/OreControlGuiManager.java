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

package de.derfrzocker.ore.control.gui;

import de.derfrzocker.feature.api.Value;
import de.derfrzocker.feature.api.ValueType;
import de.derfrzocker.feature.common.value.bool.FixedBooleanType;
import de.derfrzocker.feature.common.value.number.FixedFloatType;
import de.derfrzocker.feature.common.value.number.integer.FixedDoubleToIntegerType;
import de.derfrzocker.feature.common.value.number.integer.trapezoid.TrapezoidIntegerType;
import de.derfrzocker.feature.common.value.number.integer.uniform.UniformIntegerType;
import de.derfrzocker.feature.impl.v1_18_R2.value.offset.AboveBottomOffsetIntegerType;
import de.derfrzocker.feature.impl.v1_18_R2.value.offset.BelowTopOffsetIntegerType;
import de.derfrzocker.ore.control.api.OreControlManager;
import de.derfrzocker.ore.control.gui.screen.*;
import de.derfrzocker.ore.control.gui.screen.value.*;
import de.derfrzocker.spigot.utils.Version;
import de.derfrzocker.spigot.utils.gui.InventoryGui;
import de.derfrzocker.spigot.utils.language.LanguageManager;
import de.derfrzocker.spigot.utils.setting.ConfigSetting;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class OreControlGuiManager implements Listener {

    public static final String CONFIG_INFOS_SCREEN = "config_infos_screen";
    public static final String CONFIG_INFO_SCREEN = "config_info_screen";
    public static final String FEATURE_SELECTION_SCREEN = "feature_selection_screen";
    public static final String FEATURE_SETTINGS_SCREEN = "feature_settings_screen";

    private final Map<Player, PlayerGuiData> playerGuiDatas = new ConcurrentHashMap<>();
    private final Map<ValueType<?, ?, ?>, InventoryGui> valueTypeInventoryGuis = new ConcurrentHashMap<>();

    private final Plugin plugin;
    private final OreControlManager oreControlManager;
    private final InventoryGui configInfosScreen;
    private final InventoryGui configInfoScreen;
    private final InventoryGui featureSelectionScreen;
    private final InventoryGui featureSettingsScreen;
    private final InventoryGui biomeScreen;
    private boolean openOther = false;

    public OreControlGuiManager(Plugin plugin, OreControlManager oreControlManager, LanguageManager languageManager, Function<String, ConfigSetting> settingFunction) {
        this.plugin = plugin;
        this.oreControlManager = oreControlManager;
        this.configInfosScreen = ConfigInfosScreen.getGui(plugin, languageManager, this, settingFunction, oreControlManager.getConfigManager());
        this.configInfoScreen = ConfigInfoScreen.getGui(languageManager, this, settingFunction, oreControlManager.getConfigManager());
        this.featureSelectionScreen = FeatureSelectionScreen.getGui(plugin, oreControlManager, languageManager, this, settingFunction);
        this.featureSettingsScreen = FeatureSettingsScreen.getGui(plugin, oreControlManager, languageManager, this, settingFunction);
        this.biomeScreen = BiomeScreen.getGui(plugin, oreControlManager, languageManager, this, settingFunction);

        this.valueTypeInventoryGuis.put(FixedDoubleToIntegerType.INSTANCE, NumberEditScreen.getFixedDoubleToIntegerGui(plugin, oreControlManager, languageManager, this, settingFunction));
        this.valueTypeInventoryGuis.put(FixedFloatType.INSTANCE, NumberEditScreen.getFixedFloatGui(plugin, oreControlManager, languageManager, this, settingFunction));
        this.valueTypeInventoryGuis.put(UniformIntegerType.type(), UniformIntegerScreen.getGui(plugin, oreControlManager, languageManager, this, settingFunction));
        this.valueTypeInventoryGuis.put(TrapezoidIntegerType.type(), TrapezoidIntegerScreen.getGui(plugin, oreControlManager, languageManager, this, settingFunction));
        this.valueTypeInventoryGuis.put(FixedBooleanType.INSTANCE, BooleanScreen.getGui(plugin, oreControlManager, languageManager, this, settingFunction));
        if (Version.v1_18_R2 == Version.getServerVersion(plugin.getServer())) {
            this.valueTypeInventoryGuis.put(AboveBottomOffsetIntegerType.type(), AboveBottomOffsetIntegerScreen.getGui(plugin, oreControlManager, languageManager, this, settingFunction));
            this.valueTypeInventoryGuis.put(BelowTopOffsetIntegerType.type(), BelowTopOffsetIntegerScreen.getGui(plugin, oreControlManager, languageManager, this, settingFunction));
        } else {
            this.valueTypeInventoryGuis.put(de.derfrzocker.feature.impl.v1_18_R1.value.offset.AboveBottomOffsetIntegerType.type(), AboveBottomOffsetIntegerScreen.getGui(plugin, oreControlManager, languageManager, this, settingFunction));
            this.valueTypeInventoryGuis.put(de.derfrzocker.feature.impl.v1_18_R1.value.offset.BelowTopOffsetIntegerType.type(), BelowTopOffsetIntegerScreen.getGui(plugin, oreControlManager, languageManager, this, settingFunction));
        }

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public PlayerGuiData getPlayerGuiData(Player player) {
        return playerGuiDatas.computeIfAbsent(player, player1 -> new PlayerGuiData());
    }

    public void openGui(Player player) {
        playerGuiDatas.remove(player);
        configInfosScreen.openGui(plugin, player, true);
    }

    public void openConfigInfoScreen(Player player) {
        openGui(configInfoScreen, player);
    }

    public void openFeatureSelectionScreen(Player player) {
        openGui(featureSelectionScreen, player);
    }

    public void openFeatureSettingsScreen(Player player) {
        openGui(featureSettingsScreen, player);
    }

    public void openBiomeScreen(Player player) {
        openGui(biomeScreen, player);
    }

    public void openValueScreen(Player player, Value<?, ?, ?> value) {
        InventoryGui inventoryGui = valueTypeInventoryGuis.get(value.getValueType());

        if (inventoryGui == null) {
            plugin.getLogger().info(String.format("No inventory gui found for value type with the key '%s'", value.getValueType().getKey()));
            return;
        }

        openGui(inventoryGui, player);
    }

    private void openGui(InventoryGui inventoryGui, Player player) {
        openOther = true;
        inventoryGui.openGui(plugin, player, true);
        openOther = false;
    }

    @EventHandler
    private void onInventoryClose(InventoryCloseEvent event) {
        if (openOther) {
            return;
        }

        Bukkit.getScheduler().runTask(plugin, () -> {
            InventoryType type = event.getPlayer().getOpenInventory().getType();
            if (type == InventoryType.CRAFTING || type == InventoryType.CREATIVE) {
                playerGuiDatas.remove(event.getPlayer());
                oreControlManager.getConfigManager().saveAndReload();
                oreControlManager.onValueChange();
            }
        });
    }
}
