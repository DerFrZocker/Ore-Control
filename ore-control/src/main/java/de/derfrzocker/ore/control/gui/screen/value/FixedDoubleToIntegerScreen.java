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
import de.derfrzocker.feature.api.FeatureGenerator;
import de.derfrzocker.feature.api.FeatureGeneratorConfiguration;
import de.derfrzocker.feature.api.FeaturePlacementModifier;
import de.derfrzocker.feature.api.PlacementModifierConfiguration;
import de.derfrzocker.feature.common.value.number.integer.FixedDoubleToIntegerValue;
import de.derfrzocker.ore.control.api.OreControlManager;
import de.derfrzocker.ore.control.api.config.Config;
import de.derfrzocker.ore.control.gui.GuiSetting;
import de.derfrzocker.ore.control.gui.OreControlGuiManager;
import de.derfrzocker.ore.control.gui.PlayerGuiData;
import de.derfrzocker.ore.control.gui.SettingWrapper;
import de.derfrzocker.spigot.utils.guin.ClickAction;
import de.derfrzocker.spigot.utils.guin.InventoryGui;
import de.derfrzocker.spigot.utils.guin.builders.ButtonBuilder;
import de.derfrzocker.spigot.utils.guin.builders.ButtonContextBuilder;
import de.derfrzocker.spigot.utils.guin.builders.SingleInventoryGuiBuilder;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.function.Function;

public class FixedDoubleToIntegerScreen {

    private static final String IDENTIFIER = "value.fixed_double_to_integer_screen";
    private static final String ADD_1 = "values.0"; // Add 1
    private static final String ADD_0_1 = "values.1"; // Add 0.1
    private static final String ADD__1 = "values.2"; // Add -1
    private static final String ADD__0_1 = "values.3"; // Add -0.1

    public static InventoryGui getGui(Plugin plugin, OreControlManager oreControlManager, OreControlGuiManager guiManager, Function<String, GuiSetting> settingFunction) {
        return SingleInventoryGuiBuilder
                .builder(GuiSetting.function())
                .identifier(IDENTIFIER)
                .withSetting(settingFunction.apply("design.yml"))
                .withSetting(settingFunction.apply("value/fixed_double_to_integer_screen.yml"))
                .addConfigDecorations()
                .addButtonContext(ButtonContextBuilder
                        .builder(GuiSetting.function())
                        .identifier(ADD_1)
                        .button(ButtonBuilder
                                .builder(GuiSetting.function())
                                .identifier(ADD_1)
                                .withAction(clickAction -> clickAction.getClickEvent().setCancelled(true))
                                .withAction(clickAction -> add(plugin, oreControlManager, guiManager, clickAction, 1))
                        )
                )
                .addButtonContext(ButtonContextBuilder
                        .builder(GuiSetting.function())
                        .identifier(ADD_0_1)
                        .button(ButtonBuilder
                                .builder(GuiSetting.function())
                                .identifier(ADD_0_1)
                                .withAction(clickAction -> clickAction.getClickEvent().setCancelled(true))
                                .withAction(clickAction -> add(plugin, oreControlManager, guiManager, clickAction, 0.1))
                        )
                )
                .addButtonContext(ButtonContextBuilder
                        .builder(GuiSetting.function())
                        .identifier(ADD__1)
                        .button(ButtonBuilder
                                .builder(GuiSetting.function())
                                .identifier(ADD__1)
                                .withAction(clickAction -> clickAction.getClickEvent().setCancelled(true))
                                .withAction(clickAction -> add(plugin, oreControlManager, guiManager, clickAction, -1))
                        )
                )
                .addButtonContext(ButtonContextBuilder
                        .builder(GuiSetting.function())
                        .identifier(ADD__0_1)
                        .button(ButtonBuilder
                                .builder(GuiSetting.function())
                                .identifier(ADD__0_1)
                                .withAction(clickAction -> clickAction.getClickEvent().setCancelled(true))
                                .withAction(clickAction -> add(plugin, oreControlManager, guiManager, clickAction, -0.1))
                        )
                )
                .build();
    }

    private static void add(Plugin plugin, OreControlManager oreControlManager, OreControlGuiManager guiManager, ClickAction clickAction, double amount) {
        PlayerGuiData playerGuiData = guiManager.getPlayerGuiData((Player) clickAction.getClickEvent().getWhoClicked());

        if (!(playerGuiData.getToEditValue() instanceof FixedDoubleToIntegerValue)) {
            plugin.getLogger().warning(String.format("Expected a value of type '%s' but got one of type '%s', this is a bug!", FixedDoubleToIntegerValue.class, playerGuiData.getToEditValue() != null ? playerGuiData.getToEditValue().getClass() : "null"));
            return;
        }

        FixedDoubleToIntegerValue value = (FixedDoubleToIntegerValue) playerGuiData.getToEditValue();
        value.setValue(value.getValue() + amount);

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