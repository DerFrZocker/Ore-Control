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
import de.derfrzocker.feature.api.ValueLocation;
import de.derfrzocker.feature.api.ValueType;
import de.derfrzocker.feature.common.value.bool.FixedBooleanType;
import de.derfrzocker.feature.common.value.number.FixedFloatType;
import de.derfrzocker.feature.common.value.number.integer.FixedDoubleToIntegerType;
import de.derfrzocker.feature.common.value.number.integer.biased.BiasedToBottomIntegerType;
import de.derfrzocker.feature.common.value.number.integer.clamped.ClampedIntegerType;
import de.derfrzocker.feature.common.value.number.integer.clamped.ClampedNormalIntegerType;
import de.derfrzocker.feature.common.value.number.integer.trapezoid.TrapezoidIntegerType;
import de.derfrzocker.feature.common.value.number.integer.uniform.UniformIntegerType;
import de.derfrzocker.feature.common.value.number.integer.weighted.WeightedListIntegerType;
import de.derfrzocker.feature.common.value.offset.AboveBottomOffsetIntegerType;
import de.derfrzocker.feature.common.value.offset.BelowTopOffsetIntegerType;
import de.derfrzocker.ore.control.Stats;
import de.derfrzocker.ore.control.api.OreControlManager;
import de.derfrzocker.ore.control.gui.screen.BiomeScreen;
import de.derfrzocker.ore.control.gui.screen.ConfigInfoScreen;
import de.derfrzocker.ore.control.gui.screen.ConfigInfosScreen;
import de.derfrzocker.ore.control.gui.screen.ExtraValuesScreen;
import de.derfrzocker.ore.control.gui.screen.FeatureSelectionScreen;
import de.derfrzocker.ore.control.gui.screen.FeatureSettingsScreen;
import de.derfrzocker.ore.control.gui.screen.GeneratorSettingsScreen;
import de.derfrzocker.ore.control.gui.screen.LanguageScreen;
import de.derfrzocker.ore.control.gui.screen.PlacementModifierSettingsScreen;
import de.derfrzocker.ore.control.gui.screen.extra.BigOreVeinScreen;
import de.derfrzocker.ore.control.gui.screen.value.BiasedToBottomIntegerScreen;
import de.derfrzocker.ore.control.gui.screen.value.BooleanScreen;
import de.derfrzocker.ore.control.gui.screen.value.ClampedIntegerScreen;
import de.derfrzocker.ore.control.gui.screen.value.NumberEditScreen;
import de.derfrzocker.ore.control.gui.screen.value.OffsetIntegerScreens;
import de.derfrzocker.ore.control.gui.screen.value.TrapezoidIntegerScreen;
import de.derfrzocker.ore.control.gui.screen.value.UniformIntegerScreen;
import de.derfrzocker.ore.control.gui.screen.value.WeightedListIntegerScreen;
import de.derfrzocker.ore.control.traverser.BasicStringFormatter;
import de.derfrzocker.ore.control.traverser.ValueTraverser;
import de.derfrzocker.spigot.utils.gui.InventoryGui;
import de.derfrzocker.spigot.utils.language.LanguageManager;
import de.derfrzocker.spigot.utils.setting.ConfigSetting;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

    private final Map<Player, PlayerGuiData> playerGuiData = new ConcurrentHashMap<>();
    private final Map<String, InventoryGui> inventoryGuis = new ConcurrentHashMap<>();
    private final Map<ValueType<?, ?, ?>, InventoryGui> valueTypeInventoryGuis = new ConcurrentHashMap<>();

    private final Plugin plugin;
    private final LanguageManager languageManager;
    private final OreControlManager oreControlManager;
    private boolean openOther = false;

    public OreControlGuiManager(Plugin plugin, OreControlManager oreControlManager, LanguageManager languageManager, Function<String, ConfigSetting> settingFunction, Stats stats) {
        this.plugin = plugin;
        this.oreControlManager = oreControlManager;
        this.languageManager = languageManager;
        GuiValuesHolder guiValuesHolder = new GuiValuesHolder(plugin, oreControlManager, this, oreControlManager.getConfigManager(), languageManager, settingFunction, new ValueTraverser(), stats);

        // TODO move to config file
        for (ValueLocation location : ValueLocation.values()) {
            String prefix = switch (location) {
                case PER_WORLD -> ChatColor.GREEN.toString();
                case PER_BIOME -> ChatColor.DARK_GREEN.toString();
                case GLOBAL_WORLD -> ChatColor.BLUE.toString();
                case GLOBAL_BIOME -> ChatColor.DARK_BLUE.toString();
                case DEFAULT_WORLD -> ChatColor.RED.toString();
                case DEFAULT_BIOME -> ChatColor.DARK_PURPLE.toString();
                case UNKNOWN -> ChatColor.GRAY.toString();
            };
            guiValuesHolder.valueTraverser().registerFormatter(location, new BasicStringFormatter(prefix));
        }

        // Register standard screens
        register(BiomeScreen.getGui(guiValuesHolder));
        register(ConfigInfoScreen.getGui(guiValuesHolder));
        register(ConfigInfosScreen.getGui(guiValuesHolder));
        register(ExtraValuesScreen.getGui(guiValuesHolder));
        register(FeatureSelectionScreen.getGui(guiValuesHolder));
        register(FeatureSettingsScreen.getGui(guiValuesHolder));
        register(GeneratorSettingsScreen.getGui(guiValuesHolder));
        register(LanguageScreen.getGui(guiValuesHolder));
        register(PlacementModifierSettingsScreen.getGui(guiValuesHolder));

        // Register value screens
        register(BiasedToBottomIntegerType.type(), BiasedToBottomIntegerScreen.getGui(guiValuesHolder));
        register(FixedBooleanType.INSTANCE, BooleanScreen.getGui(guiValuesHolder));
        register(ClampedIntegerType.type(), ClampedIntegerScreen.getClampedIntegerScreen(guiValuesHolder));
        register(ClampedNormalIntegerType.type(), ClampedIntegerScreen.getClampedNormalIntegerGui(guiValuesHolder));
        register(FixedDoubleToIntegerType.INSTANCE, NumberEditScreen.getFixedDoubleToIntegerGui(guiValuesHolder));
        register(FixedFloatType.INSTANCE, NumberEditScreen.getFixedFloatGui(guiValuesHolder));
        register(AboveBottomOffsetIntegerType.type(), OffsetIntegerScreens.getAboveBottomOffsetGui(guiValuesHolder));
        register(BelowTopOffsetIntegerType.type(), OffsetIntegerScreens.getBelowTopOffsetGui(guiValuesHolder));
        register(TrapezoidIntegerType.type(), TrapezoidIntegerScreen.getGui(guiValuesHolder));
        register(UniformIntegerType.type(), UniformIntegerScreen.getGui(guiValuesHolder));
        register(WeightedListIntegerType.type(), WeightedListIntegerScreen.getGui(guiValuesHolder));

        // Register extra screens
        register(ExtraValuesScreen.getGui(guiValuesHolder));
        register(BigOreVeinScreen.getGui(guiValuesHolder));

        // Register listeners
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public PlayerGuiData getPlayerGuiData(Player player) {
        return playerGuiData.computeIfAbsent(player, player1 -> new PlayerGuiData());
    }

    public void openGui(Player player) {
        playerGuiData.remove(player);
        if (!languageManager.hasLanguageSet(player)) {
            openScreen(Screens.LANGUAGE_SCREEN, player);
        } else {
            openScreen(Screens.CONFIG_INFOS_SCREEN, player);
        }
    }

    public void openScreen(String identifier, Player player) {
        openScreen(inventoryGuis.get(identifier), player);
    }

    public void openValueScreen(Player player, Value<?, ?, ?> value) {
        InventoryGui inventoryGui = valueTypeInventoryGuis.get(value.getValueType());

        if (inventoryGui == null) {
            plugin.getLogger().info(String.format("No inventory gui found for value type with the key '%s'", value.getValueType().getKey()));
            return;
        }

        openScreen(inventoryGui, player);
    }

    public void openScreen(InventoryGui inventoryGui, Player player) {
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

    private InventoryGui register(InventoryGui inventoryGui) {
        inventoryGuis.put(inventoryGui.getIdentifier(), inventoryGui);
        return inventoryGui;
    }

    private void register(ValueType<?, ?, ?> valueType, InventoryGui inventoryGui) {
        valueTypeInventoryGuis.put(valueType, register(inventoryGui));
    }
}
