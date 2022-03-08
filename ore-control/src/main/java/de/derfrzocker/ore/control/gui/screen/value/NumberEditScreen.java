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

package de.derfrzocker.ore.control.gui.screen.value;

import de.derfrzocker.feature.api.Configuration;
import de.derfrzocker.feature.api.ConfigurationAble;
import de.derfrzocker.feature.api.Feature;
import de.derfrzocker.feature.api.FeatureGenerator;
import de.derfrzocker.feature.api.FeatureGeneratorConfiguration;
import de.derfrzocker.feature.api.FeaturePlacementModifier;
import de.derfrzocker.feature.api.PlacementModifierConfiguration;
import de.derfrzocker.feature.common.value.number.FixedFloatValue;
import de.derfrzocker.feature.common.value.number.integer.FixedDoubleToIntegerValue;
import de.derfrzocker.ore.control.api.OreControlManager;
import de.derfrzocker.ore.control.api.config.Config;
import de.derfrzocker.ore.control.gui.OreControlGuiManager;
import de.derfrzocker.ore.control.gui.PlayerGuiData;
import de.derfrzocker.ore.control.gui.SettingWrapper;
import de.derfrzocker.spigot.utils.guin.ClickAction;
import de.derfrzocker.spigot.utils.guin.InventoryGui;
import de.derfrzocker.spigot.utils.guin.builders.ButtonBuilder;
import de.derfrzocker.spigot.utils.guin.builders.ButtonContextBuilder;
import de.derfrzocker.spigot.utils.guin.builders.ListButtonBuilder;
import de.derfrzocker.spigot.utils.guin.builders.SingleInventoryGuiBuilder;
import de.derfrzocker.spigot.utils.message.MessageUtil;
import de.derfrzocker.spigot.utils.message.MessageValue;
import de.derfrzocker.spigot.utils.setting.ConfigSetting;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.NumberConversions;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class NumberEditScreen {

    private static final String DEFAULT_ICON = "default-icon";

    public static InventoryGui getFixedDoubleToIntegerGui(Plugin plugin, OreControlManager oreControlManager, OreControlGuiManager guiManager, Function<String, ConfigSetting> settingFunction) {
        return getGui(plugin, oreControlManager, guiManager, settingFunction,
                playerGuiData -> ((FixedDoubleToIntegerValue) playerGuiData.getToEditValue()).getValue(),
                (playerGuiData, number) -> {
                    if (!(playerGuiData.getToEditValue() instanceof FixedDoubleToIntegerValue value)) {
                        plugin.getLogger().warning(String.format("Expected a value of type '%s' but got one of type '%s', this is a bug!", FixedDoubleToIntegerValue.class, playerGuiData.getToEditValue() != null ? playerGuiData.getToEditValue().getClass() : "null"));
                        return;
                    }
                    value.setValue(value.getValue() + number.doubleValue());
                })
                .identifier("value.fixed_double_to_integer_screen")
                .withSetting(settingFunction.apply("value/fixed_double_to_integer_screen.yml"))
                .build();
    }

    public static InventoryGui getFixedFloatGui(Plugin plugin, OreControlManager oreControlManager, OreControlGuiManager guiManager, Function<String, ConfigSetting> settingFunction) {
        return getGui(plugin, oreControlManager, guiManager, settingFunction,
                playerGuiData -> ((FixedFloatValue) playerGuiData.getToEditValue()).getValue(),
                (playerGuiData, number) -> {
                    if (!(playerGuiData.getToEditValue() instanceof FixedFloatValue value)) {
                        plugin.getLogger().warning(String.format("Expected a value of type '%s' but got one of type '%s', this is a bug!", FixedFloatValue.class, playerGuiData.getToEditValue() != null ? playerGuiData.getToEditValue().getClass() : "null"));
                        return;
                    }
                    value.setValue(value.getValue() + number.floatValue());
                })
                .identifier("value.fixed_float_screen")
                .withSetting(settingFunction.apply("value/fixed_float_screen.yml"))
                .build();
    }

    private static SingleInventoryGuiBuilder getGui(Plugin plugin, OreControlManager oreControlManager, OreControlGuiManager guiManager, Function<String, ConfigSetting> settingFunction, Function<PlayerGuiData, Number> numberSupplier, BiConsumer<PlayerGuiData, Number> numberConsumer) {
        return SingleInventoryGuiBuilder
                .builder()
                .withSetting(settingFunction.apply("design.yml"))
                .withSetting(settingFunction.apply("feature_icons.yml"))
                .addListButton(ListButtonBuilder
                        .builder()
                        .identifier("values")
                        .withAction((clickAction, value) -> clickAction.getClickEvent().setCancelled(true))
                        .withAction((clickAction, value) -> numberConsumer.accept(guiManager.getPlayerGuiData((Player) clickAction.getClickEvent().getWhoClicked()), NumberConversions.toDouble(value)))
                        .withAction((clickAction, value) -> add(plugin, oreControlManager, guiManager, clickAction))
                        .withAction((clickAction, value) -> clickAction.getInventoryGui().updatedSoft())
                )
                .addButtonContext(ButtonContextBuilder
                        .builder()
                        .identifier(DEFAULT_ICON)
                        .button(ButtonBuilder
                                .builder()
                                .identifier(DEFAULT_ICON)
                                .itemStack((setting, guiInfo) -> {
                                    PlayerGuiData playerGuiData = guiManager.getPlayerGuiData((Player) guiInfo.getEntity());
                                    Feature<?> feature = playerGuiData.getFeature();
                                    String key = "icons." + feature.getKey().getNamespace() + "." + feature.getKey().getKey();
                                    ItemStack icon = setting.get(key + ".item-stack", null);
                                    if (icon == null) {
                                        icon = setting.get("default-icon.item-stack", new ItemStack(Material.STONE)).clone();
                                        String type = setting.get(key + ".type", null);
                                        if (type == null) {
                                            plugin.getLogger().info(String.format("No item stack or type found for feature '%s' using default item stack", feature.getKey()));
                                        } else {
                                            try {
                                                Material material = Material.valueOf(type.toUpperCase());
                                                icon.setType(material);
                                            } catch (IllegalArgumentException e) {
                                                plugin.getLogger().warning(String.format("Material '%s' for feature '%s' not found", type, feature.getKey()));
                                            }
                                        }
                                    } else {
                                        icon = icon.clone();
                                    }
                                    return MessageUtil.replaceItemStack(plugin, icon,
                                            new MessageValue("feature-name", feature.getKey()),
                                            new MessageValue("setting-name", playerGuiData.getSettingWrapper().getSetting().getName()),
                                            new MessageValue("current-value", numberSupplier.apply(playerGuiData))
                                    );
                                })
                                .withAction(clickAction -> clickAction.getClickEvent().setCancelled(true))
                        )
                );
    }

    private static void add(Plugin plugin, OreControlManager oreControlManager, OreControlGuiManager guiManager, ClickAction clickAction) {
        PlayerGuiData playerGuiData = guiManager.getPlayerGuiData((Player) clickAction.getClickEvent().getWhoClicked());

        if (!playerGuiData.isApplied()) {
            Config config;
            if (playerGuiData.getBiome() == null) {
                config = oreControlManager.getConfigManager().getOrCreateConfig(playerGuiData.getConfigInfo(), playerGuiData.getFeature().getKey());
            } else {
                config = oreControlManager.getConfigManager().getOrCreateConfig(playerGuiData.getConfigInfo(), playerGuiData.getBiome(), playerGuiData.getFeature().getKey());
            }

            SettingWrapper settingWrapper = playerGuiData.getSettingWrapper();
            Configuration configuration;
            if (settingWrapper.getSettingOwner() instanceof FeatureGenerator) {
                if (config.getFeature() == null) {
                    config.setFeature((FeatureGeneratorConfiguration) settingWrapper.getSettingOwner().createEmptyConfiguration());
                } else if (config.getFeature().getOwner() != settingWrapper.getSettingOwner()) {
                    plugin.getLogger().warning(String.format("Expected a setting owner of type '%s' but got one of type '%s', this is a bug!", settingWrapper.getSettingOwner().getClass(), config.getFeature().getOwner()));
                    return;
                }

                configuration = config.getFeature();
            } else if (settingWrapper.getSettingOwner() instanceof FeaturePlacementModifier) {
                configuration = getPlacementConfiguration(config.getPlacements(), settingWrapper.getSettingOwner());

                if (configuration == null) {
                    configuration = settingWrapper.getSettingOwner().createEmptyConfiguration();
                    config.setPlacement((PlacementModifierConfiguration) configuration);
                }
            } else {
                plugin.getLogger().warning(String.format("Expected a setting owner of type '%s' or '%s' but got '%s', this is a bug!", FeatureGenerator.class, FeaturePlacementModifier.class, settingWrapper.getSettingOwner().getClass()));
                return;
            }

            configuration.setValue(settingWrapper.getSetting(), playerGuiData.getOriginalValue());
            playerGuiData.setApplied(true);
        }
    }

    private static PlacementModifierConfiguration getPlacementConfiguration(List<PlacementModifierConfiguration> placementModifierConfigurations, ConfigurationAble owner) {
        if (placementModifierConfigurations == null || placementModifierConfigurations.isEmpty()) {
            return null;
        }

        for (PlacementModifierConfiguration configuration : placementModifierConfigurations) {
            if (configuration.getOwner() == owner) {
                return configuration;
            }
        }

        return null;
    }
}