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

import de.derfrzocker.feature.api.Configuration;
import de.derfrzocker.feature.api.ConfigurationAble;
import de.derfrzocker.feature.api.Feature;
import de.derfrzocker.feature.api.FeaturePlacementModifier;
import de.derfrzocker.feature.api.PlacementModifierConfiguration;
import de.derfrzocker.feature.api.Setting;
import de.derfrzocker.feature.api.Value;
import de.derfrzocker.ore.control.api.OreControlManager;
import de.derfrzocker.ore.control.api.config.Config;
import de.derfrzocker.ore.control.gui.OreControlGuiManager;
import de.derfrzocker.ore.control.gui.PlayerGuiData;
import de.derfrzocker.ore.control.gui.SettingWrapper;
import de.derfrzocker.spigot.utils.guin.GuiInfo;
import de.derfrzocker.spigot.utils.guin.InventoryGui;
import de.derfrzocker.spigot.utils.guin.builders.PageContentBuilder;
import de.derfrzocker.spigot.utils.guin.builders.PagedInventoryGuiBuilder;
import de.derfrzocker.spigot.utils.message.MessageUtil;
import de.derfrzocker.spigot.utils.setting.ConfigSetting;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class FeatureSettingsScreen {

    private static final String IDENTIFIER = OreControlGuiManager.FEATURE_SETTINGS_SCREEN;

    public static InventoryGui getGui(Plugin plugin, OreControlManager oreControlManager, OreControlGuiManager guiManager, Function<String, ConfigSetting> settingFunction) {
        return PagedInventoryGuiBuilder
                .builder()
                .identifier(IDENTIFIER)
                .withSetting(settingFunction.apply("design.yml"))
                .withSetting(settingFunction.apply("feature_settings_screen.yml"))
                .addDefaultNextButton()
                .addDefaultPreviousButton()
                .addConfigDecorations()
                .pageContent(PageContentBuilder
                        .builder(SettingWrapper.class)
                        .data((setting, guiInfo) -> buildList(oreControlManager, guiManager, guiInfo))
                        .itemStack((setting, guiInfo, settingWrapper) -> {
                            return MessageUtil.replaceItemStack(plugin, setting.get(IDENTIFIER, "default-icon.item-stack", new ItemStack(Material.STONE)).clone());
                        })
                        .withAction((clickAction, settingWrapper) -> clickAction.getClickEvent().setCancelled(true))
                        .withAction((clickAction, settingWrapper) -> guiManager.getPlayerGuiData((Player) clickAction.getClickEvent().getWhoClicked()).setSettingWrapper(settingWrapper))
                        .withAction((clickAction, settingWrapper) -> {
                            PlayerGuiData playerGuiData = guiManager.getPlayerGuiData((Player) clickAction.getClickEvent().getWhoClicked());
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
                                configuration = getPlacementConfiguration(config.getPlacements(), settingOwner);
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
                            guiManager.openValueScreen((Player) clickAction.getClickEvent().getWhoClicked(), value);
                        })
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
            for (Setting setting : placementModifier.getSettings()) {
                settingWrappers.add(new SettingWrapper(setting, placementModifier));
            }
        }

        return settingWrappers;
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
