package de.derfrzocker.ore.control.gui;

import de.derfrzocker.feature.api.RuleTest;
import de.derfrzocker.feature.api.RuleTestType;
import de.derfrzocker.feature.api.Value;
import de.derfrzocker.feature.api.ValueLocation;
import de.derfrzocker.feature.api.ValueType;
import de.derfrzocker.feature.common.ruletest.BlockMatchRuleTestType;
import de.derfrzocker.feature.common.ruletest.BlockStateMatchRuleTestType;
import de.derfrzocker.feature.common.ruletest.TagMatchRuleTest;
import de.derfrzocker.feature.common.ruletest.TagMatchRuleTestType;
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
import de.derfrzocker.feature.common.value.target.FixedTargetListType;
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
import de.derfrzocker.ore.control.gui.screen.other.BlockTagScreen;
import de.derfrzocker.ore.control.gui.screen.other.TargetBlockStateScreen;
import de.derfrzocker.ore.control.gui.screen.ruletest.BlockMatchRuleTestScreen;
import de.derfrzocker.ore.control.gui.screen.ruletest.BlockStateMatchRuleTestScreen;
import de.derfrzocker.ore.control.gui.screen.ruletest.TagRuleTestScreen;
import de.derfrzocker.ore.control.gui.screen.value.BiasedToBottomIntegerScreen;
import de.derfrzocker.ore.control.gui.screen.value.BooleanScreen;
import de.derfrzocker.ore.control.gui.screen.value.ClampedIntegerScreen;
import de.derfrzocker.ore.control.gui.screen.value.FixedTargetListScreen;
import de.derfrzocker.ore.control.gui.screen.value.NumberEditScreen;
import de.derfrzocker.ore.control.gui.screen.value.OffsetIntegerScreens;
import de.derfrzocker.ore.control.gui.screen.value.TrapezoidIntegerScreen;
import de.derfrzocker.ore.control.gui.screen.value.UniformIntegerScreen;
import de.derfrzocker.ore.control.gui.screen.value.WeightedListIntegerScreen;
import de.derfrzocker.ore.control.interactions.BlockInteractionManager;
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
    private final Map<RuleTestType, InventoryGui> ruleTestTypeInventoryGuis = new ConcurrentHashMap<>();

    private final Plugin plugin;
    private final LanguageManager languageManager;
    private final OreControlManager oreControlManager;

    public OreControlGuiManager(Plugin plugin, OreControlManager oreControlManager, LanguageManager languageManager, Function<String, ConfigSetting> settingFunction, Stats stats, BlockInteractionManager interactionManager) {
        this.plugin = plugin;
        this.oreControlManager = oreControlManager;
        this.languageManager = languageManager;
        GuiValuesHolder guiValuesHolder = new GuiValuesHolder(plugin, oreControlManager, this, oreControlManager.getConfigManager(), languageManager, settingFunction, new ValueTraverser(), stats, interactionManager);

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
        register(FixedTargetListType.type(), FixedTargetListScreen.getGui(guiValuesHolder));

        // Register rule test screens
        register(BlockMatchRuleTestType.INSTANCE, BlockMatchRuleTestScreen.getGui(guiValuesHolder));
        register(BlockStateMatchRuleTestType.INSTANCE, BlockStateMatchRuleTestScreen.getGui(guiValuesHolder));
        register(TagMatchRuleTestType.INSTANCE, TagRuleTestScreen.getGui(guiValuesHolder));

        // Register extra screens
        register(ExtraValuesScreen.getGui(guiValuesHolder));
        register(BigOreVeinScreen.getGui(guiValuesHolder));

        // Register other screens
        register(TargetBlockStateScreen.getGui(guiValuesHolder));
        register(BlockTagScreen.getGui(guiValuesHolder));

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

    public void openRuleTestScreen(Player player, RuleTest ruleTest) {
        InventoryGui inventoryGui = ruleTestTypeInventoryGuis.get(ruleTest.getType());

        if (inventoryGui == null) {
            plugin.getLogger().info(String.format("No inventory gui found for rule test type with the key '%s'", ruleTest.getType().getKey()));
            return;
        }

        openScreen(inventoryGui, player);
    }

    public void openScreen(InventoryGui inventoryGui, Player player) {
        PlayerGuiData data = getPlayerGuiData(player);
        data.setHandleInventoryClosing(false);
        inventoryGui.openGui(plugin, player, true);
        data.addGui(inventoryGui);
        data.setHandleInventoryClosing(true);
    }

    @EventHandler
    private void onInventoryClose(InventoryCloseEvent event) {
        PlayerGuiData data = getPlayerGuiData((Player) event.getPlayer());
        if (data == null) {
            return;
        }

        if (!data.isHandleInventoryClosing()) {
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

    private void register(RuleTestType ruleTestType, InventoryGui inventoryGui) {
        ruleTestTypeInventoryGuis.put(ruleTestType, register(inventoryGui));
    }
}
