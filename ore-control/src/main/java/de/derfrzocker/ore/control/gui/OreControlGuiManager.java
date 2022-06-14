/*
 * MIT License
 *
 * Copyright (c) 2019 - 2022 Marvin (DerFrZocker)
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
import de.derfrzocker.feature.common.value.offset.AboveBottomOffsetIntegerType;
import de.derfrzocker.feature.common.value.offset.BelowTopOffsetIntegerType;
import de.derfrzocker.ore.control.api.OreControlManager;
import de.derfrzocker.ore.control.gui.screen.BiomeScreen;
import de.derfrzocker.ore.control.gui.screen.ConfigInfoScreen;
import de.derfrzocker.ore.control.gui.screen.ConfigInfosScreen;
import de.derfrzocker.ore.control.gui.screen.FeatureSelectionScreen;
import de.derfrzocker.ore.control.gui.screen.FeatureSettingsScreen;
import de.derfrzocker.ore.control.gui.screen.value.AboveBottomOffsetIntegerScreen;
import de.derfrzocker.ore.control.gui.screen.value.BelowTopOffsetIntegerScreen;
import de.derfrzocker.ore.control.gui.screen.value.BooleanScreen;
import de.derfrzocker.ore.control.gui.screen.value.NumberEditScreen;
import de.derfrzocker.ore.control.gui.screen.value.TrapezoidIntegerScreen;
import de.derfrzocker.ore.control.gui.screen.value.UniformIntegerScreen;
import de.derfrzocker.spigot.utils.gui.InventoryGui;
import de.derfrzocker.spigot.utils.gui.builders.Builders;
import de.derfrzocker.spigot.utils.gui.builders.ButtonContextBuilder;
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

    private final Map<Player, PlayerGuiData> playerGuiData = new ConcurrentHashMap<>();
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
        GuiValuesHolder guiValuesHolder = new GuiValuesHolder(plugin, oreControlManager, this, oreControlManager.getConfigManager(), languageManager, settingFunction);
        this.configInfosScreen = ConfigInfosScreen.getGui(guiValuesHolder);
        this.configInfoScreen = ConfigInfoScreen.getGui(guiValuesHolder);
        this.featureSelectionScreen = FeatureSelectionScreen.getGui(guiValuesHolder);
        this.featureSettingsScreen = FeatureSettingsScreen.getGui(guiValuesHolder);
        this.biomeScreen = BiomeScreen.getGui(guiValuesHolder);

        this.valueTypeInventoryGuis.put(FixedDoubleToIntegerType.INSTANCE, NumberEditScreen.getFixedDoubleToIntegerGui(guiValuesHolder));
        this.valueTypeInventoryGuis.put(FixedFloatType.INSTANCE, NumberEditScreen.getFixedFloatGui(guiValuesHolder));
        this.valueTypeInventoryGuis.put(UniformIntegerType.type(), UniformIntegerScreen.getGui(guiValuesHolder));
        this.valueTypeInventoryGuis.put(TrapezoidIntegerType.type(), TrapezoidIntegerScreen.getGui(guiValuesHolder));
        this.valueTypeInventoryGuis.put(FixedBooleanType.INSTANCE, BooleanScreen.getGui(guiValuesHolder));
        this.valueTypeInventoryGuis.put(AboveBottomOffsetIntegerType.type(), AboveBottomOffsetIntegerScreen.getGui(guiValuesHolder));
        this.valueTypeInventoryGuis.put(BelowTopOffsetIntegerType.type(), BelowTopOffsetIntegerScreen.getGui(guiValuesHolder));

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public PlayerGuiData getPlayerGuiData(Player player) {
        return playerGuiData.computeIfAbsent(player, player1 -> new PlayerGuiData());
    }

    public void openGui(Player player) {
        playerGuiData.remove(player);
        openGui(configInfosScreen, player);
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
        PlayerGuiData data = getPlayerGuiData(player);
        inventoryGui.openGui(plugin, player, true);
        data.addGui(inventoryGui);
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
                playerGuiData.remove(event.getPlayer());
                oreControlManager.getConfigManager().saveAndReload();
                oreControlManager.onValueChange();
            }
        });
    }

    public ButtonContextBuilder getBackButton() {
        return Builders
                .buttonContext()
                .identifier("back")
                .button(Builders
                        .button()
                        .identifier("back")
                        .withAction(clickAction -> clickAction.getClickEvent().setCancelled(true))
                        .withAction(clickAction -> getPlayerGuiData(clickAction.getPlayer()).pollFirstInventory().onBack(clickAction.getPlayer()))
                        .withAction(clickAction -> openGui(getPlayerGuiData(clickAction.getPlayer()).pollFirstInventory(), clickAction.getPlayer()))
                );
    }
}
