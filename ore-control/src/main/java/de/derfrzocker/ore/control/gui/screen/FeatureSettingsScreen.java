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

package de.derfrzocker.ore.control.gui.screen;

import de.derfrzocker.feature.api.*;
import de.derfrzocker.feature.common.feature.placement.ActivationModifier;
import de.derfrzocker.feature.common.feature.placement.configuration.ActivationConfiguration;
import de.derfrzocker.feature.common.value.bool.BooleanValue;
import de.derfrzocker.feature.common.value.bool.FixedBooleanValue;
import de.derfrzocker.ore.control.api.OreControlManager;
import de.derfrzocker.ore.control.api.config.Config;
import de.derfrzocker.ore.control.gui.OreControlGuiManager;
import de.derfrzocker.ore.control.gui.PlayerGuiData;
import de.derfrzocker.ore.control.gui.SettingWrapper;
import de.derfrzocker.spigot.utils.gui.GuiInfo;
import de.derfrzocker.spigot.utils.gui.InventoryGui;
import de.derfrzocker.spigot.utils.gui.builders.Builders;
import de.derfrzocker.spigot.utils.language.LanguageManager;
import de.derfrzocker.spigot.utils.message.MessageValue;
import de.derfrzocker.spigot.utils.setting.ConfigSetting;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

// TODO clean up
public class FeatureSettingsScreen {

    private static final String IDENTIFIER = OreControlGuiManager.FEATURE_SETTINGS_SCREEN;

    public static InventoryGui getGui(Plugin plugin, OreControlManager oreControlManager, LanguageManager languageManager, OreControlGuiManager guiManager, Function<String, ConfigSetting> settingFunction) {
        return Builders
                .paged()
                .identifier(IDENTIFIER)
                .languageManager(languageManager)
                .withSetting(settingFunction.apply("design.yml"))
                .withSetting(settingFunction.apply("feature_settings_screen.yml"))
                .addDefaultNextButton()
                .addDefaultPreviousButton()
                .pageContent(Builders
                        .pageContent(SettingWrapper.class)
                        .data((setting, guiInfo) -> buildList(oreControlManager, guiManager, guiInfo))
                        .withMessageValue((setting, guiInfo, settingWrapper) -> new MessageValue("setting", settingWrapper.getSetting().getName()))
                        .itemStack((setting, guiInfo, settingWrapper) -> setting.get(IDENTIFIER, "default-icon.item-stack", new ItemStack(Material.STONE)).clone())
                        .withAction((clickAction, settingWrapper) -> clickAction.getClickEvent().setCancelled(true))
                        .withAction((clickAction, settingWrapper) -> guiManager.getPlayerGuiData(clickAction.getPlayer()).setSettingWrapper(settingWrapper))
                        .withAction((clickAction, settingWrapper) -> {
                            PlayerGuiData playerGuiData = guiManager.getPlayerGuiData(clickAction.getPlayer());
                            Setting setting = settingWrapper.getSetting();
                            ConfigurationAble settingOwner = settingWrapper.getSettingOwner();

                            if (settingOwner == null) {
                                plugin.getLogger().warning(String.format("No setting owner found, this is a bug!"));
                                return;
                            }

                            Optional<Config> optionalConfig;
                            if (playerGuiData.getBiome() == null) {
                                optionalConfig = oreControlManager.getConfigManager().getGuiConfig(playerGuiData.getConfigInfo(), playerGuiData.getFeature().getKey());
                            } else {
                                optionalConfig = oreControlManager.getConfigManager().getGuiConfig(playerGuiData.getConfigInfo(), playerGuiData.getBiome(), playerGuiData.getFeature().getKey());
                            }

                            if (optionalConfig.isEmpty()) {
                                plugin.getLogger().warning(String.format("No gui specific config found, it should always be possible to build one with default value, this is a bug!"));
                                return;
                            }

                            Config config = optionalConfig.get();

                            Configuration configuration = config.getFeature();

                            if (configuration == null || configuration.getOwner() != settingOwner) {
                                configuration = getPlacementConfiguration(config.getPlacements().values(), settingOwner);
                            }

                            if (configuration == null || configuration.getOwner() != settingOwner) {
                                plugin.getLogger().warning(String.format("No suitable configuration found, there should always a default configuration present, this is a bug!"));
                                return;
                            }

                            Value<?, ?, ?> value = configuration.getValue(setting);

                            if (value == null) {
                                plugin.getLogger().warning(String.format("No suitable value found, there should always a default value present, this is a bug!"));
                                return;
                            }

                            value = value.clone();

                            playerGuiData.setOriginalValue(value);
                            playerGuiData.setToEditValue(value);
                            playerGuiData.setApplied(false);
                            guiManager.openValueScreen(clickAction.getPlayer(), value);
                        })
                )
                .addButtonContext(Builders
                        .buttonContext()
                        .withCondition((setting, guiInfo) -> {
                            PlayerGuiData playerGuiData = guiManager.getPlayerGuiData((Player) guiInfo.getEntity());
                            Feature<?> feature = playerGuiData.getFeature();

                            if (feature == null) {
                                return false;
                            }

                            return oreControlManager.getRegistries().getPlacementModifierRegistry().get(ActivationModifier.KEY).map(modifier -> feature.getPlacementModifiers().contains(modifier)).orElse(false);
                        })
                        .slot((setting, guiInfo) -> setting.get(IDENTIFIER, "placement-modifier-icon.feature.activation.slot",4))
                        .identifier("activation")
                        .button(Builders
                                .button()
                                .identifier("activation")
                                .itemStack((setting, guiInfo) -> {
                                    PlayerGuiData playerGuiData = guiManager.getPlayerGuiData((Player) guiInfo.getEntity());
                                    Optional<FeaturePlacementModifier<?>> placementModifier = oreControlManager.getRegistries().getPlacementModifierRegistry().get(ActivationModifier.KEY);

                                    if (placementModifier.isEmpty()) {
                                        plugin.getLogger().warning(String.format("No activation modifier found, at this point it should be present, this is a bug!"));
                                        return new ItemStack(Material.BARRIER);
                                    }

                                    Optional<Config> optionalConfig;
                                    if (playerGuiData.getBiome() == null) {
                                        optionalConfig = oreControlManager.getConfigManager().getGuiConfig(playerGuiData.getConfigInfo(), playerGuiData.getFeature().getKey());
                                    } else {
                                        optionalConfig = oreControlManager.getConfigManager().getGuiConfig(playerGuiData.getConfigInfo(), playerGuiData.getBiome(), playerGuiData.getFeature().getKey());
                                    }

                                    if (optionalConfig.isEmpty()) {
                                        plugin.getLogger().warning(String.format("No gui specific config found, it should always be possible to build one with default value, this is a bug!"));
                                        return new ItemStack(Material.BARRIER);
                                    }

                                    Config config = optionalConfig.get();
                                    Configuration configuration = config.getPlacements().get(placementModifier.get());

                                    if (configuration == null) {
                                        plugin.getLogger().warning(String.format("No suitable configuration found, there should always a default configuration present, this is a bug!"));
                                        return new ItemStack(Material.BARRIER);
                                    }

                                    if (!(configuration instanceof ActivationConfiguration)) {
                                        plugin.getLogger().warning(String.format("Present configuration is of type '%s', but should be of type '%s', this is a bug!", configuration.getClass(), "ActivationConfiguration"));
                                        return new ItemStack(Material.BARRIER);
                                    }

                                    BooleanValue value = ((ActivationConfiguration) configuration).getActivate();

                                    if (value == null) {
                                        plugin.getLogger().warning(String.format("No suitable value found, there should always a default value present, this is a bug!"));
                                        return new ItemStack(Material.BARRIER);
                                    }

                                    if (!(value instanceof FixedBooleanValue)) {
                                        return setting.get(IDENTIFIER, "placement-modifier-icon.feature.activation.unknown.item-stack", new ItemStack(Material.STONE));
                                    }

                                    if (((FixedBooleanValue) value).getValue()) {
                                        return setting.get(IDENTIFIER, "placement-modifier-icon.feature.activation.true.item-stack", new ItemStack(Material.STONE));
                                    } else {
                                        return setting.get(IDENTIFIER, "placement-modifier-icon.feature.activation.false.item-stack", new ItemStack(Material.STONE));
                                    }
                                })
                                .withAction(clickAction -> clickAction.getClickEvent().setCancelled(true))
                                .withAction(clickAction -> {
                                    PlayerGuiData playerGuiData = guiManager.getPlayerGuiData(clickAction.getPlayer());
                                    Optional<FeaturePlacementModifier<?>> placementModifier = oreControlManager.getRegistries().getPlacementModifierRegistry().get(ActivationModifier.KEY);

                                    if (placementModifier.isEmpty()) {
                                        plugin.getLogger().warning(String.format("No activation modifier found, at this point it should be present, this is a bug!"));
                                        return;
                                    }

                                    Optional<Config> optionalConfig;
                                    if (playerGuiData.getBiome() == null) {
                                        optionalConfig = oreControlManager.getConfigManager().getGuiConfig(playerGuiData.getConfigInfo(), playerGuiData.getFeature().getKey());
                                    } else {
                                        optionalConfig = oreControlManager.getConfigManager().getGuiConfig(playerGuiData.getConfigInfo(), playerGuiData.getBiome(), playerGuiData.getFeature().getKey());
                                    }

                                    if (optionalConfig.isEmpty()) {
                                        plugin.getLogger().warning(String.format("No gui specific config found, it should always be possible to build one with default value, this is a bug!"));
                                        return;
                                    }

                                    Config config = optionalConfig.get();
                                    Configuration configuration = config.getPlacements().get(placementModifier.get());

                                    if (configuration == null) {
                                        plugin.getLogger().warning(String.format("No suitable configuration found, there should always a default configuration present, this is a bug!"));
                                        return;
                                    }

                                    if (!(configuration instanceof ActivationConfiguration)) {
                                        plugin.getLogger().warning(String.format("Present configuration is of type '%s', but should be of type '%s', this is a bug!", configuration.getClass(), "ActivationConfiguration"));
                                        return;
                                    }

                                    Value<?, ?, ?> value = ((ActivationConfiguration) configuration).getActivate();

                                    if (value == null) {
                                        plugin.getLogger().warning(String.format("No suitable value found, there should always a default value present, this is a bug!"));
                                        return;
                                    }

                                    value = value.clone();

                                    playerGuiData.setOriginalValue(value);
                                    playerGuiData.setToEditValue(value);
                                    playerGuiData.setApplied(false);
                                    playerGuiData.setSettingWrapper(new SettingWrapper(ActivationConfiguration.ACTIVATE, placementModifier.get()));
                                    guiManager.openValueScreen(clickAction.getPlayer(), value);
                                })
                        )
                )
                .addButtonContext(Builders
                        .buttonContext()
                        .identifier("back")
                        .button(Builders
                                .button()
                                .identifier("back")
                                .withAction(clickAction -> clickAction.getClickEvent().setCancelled(true))
                                .withAction(clickAction -> guiManager.getPlayerGuiData(clickAction.getPlayer()).setFeature(null))
                                .withAction(clickAction -> guiManager.openFeatureSelectionScreen(clickAction.getPlayer()))
                        )
                )
                .build();
    }

    private static List<SettingWrapper> buildList(OreControlManager oreControlManager, OreControlGuiManager guiManager, GuiInfo guiInfo) {
        PlayerGuiData playerGuiData = guiManager.getPlayerGuiData((Player) guiInfo.getEntity());
        Feature<?> feature = playerGuiData.getFeature();
        List<SettingWrapper> settingWrappers = new LinkedList<>();

        for (Setting setting : feature.getGenerator().getSettings()) {
            settingWrappers.add(new SettingWrapper(setting, feature.getGenerator()));
        }

        for (FeaturePlacementModifier<?> placementModifier : feature.getPlacementModifiers()) {
            if (placementModifier.getKey().equals(ActivationModifier.KEY)) {
                continue;
            }

            for (Setting setting : placementModifier.getSettings()) {
                settingWrappers.add(new SettingWrapper(setting, placementModifier));
            }
        }

        return settingWrappers;
    }

    private static PlacementModifierConfiguration getPlacementConfiguration(Collection<PlacementModifierConfiguration> placementModifierConfigurations, ConfigurationAble owner) {
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
