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

import de.derfrzocker.feature.api.Feature;
import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.OreControlManager;
import de.derfrzocker.ore.control.api.config.ConfigInfo;
import de.derfrzocker.ore.control.api.config.ConfigType;
import de.derfrzocker.ore.control.gui.GuiSetting;
import de.derfrzocker.ore.control.gui.OreControlGuiManager;
import de.derfrzocker.ore.control.gui.PlayerGuiData;
import de.derfrzocker.spigot.utils.guin.GuiInfo;
import de.derfrzocker.spigot.utils.guin.InventoryGui;
import de.derfrzocker.spigot.utils.guin.builders.PageContentBuilder;
import de.derfrzocker.spigot.utils.guin.builders.PagedInventoryGuiBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class FeatureSelectionScreen {

    private static final String IDENTIFIER = OreControlGuiManager.FEATURE_SELECTION_SCREEN;

    public static InventoryGui getGui(Plugin plugin, OreControlManager oreControlManager, OreControlGuiManager guiManager, Function<String, GuiSetting> settingFunction) {
        return PagedInventoryGuiBuilder
                .builder(GuiSetting.function())
                .identifier(IDENTIFIER)
                .withSetting(settingFunction.apply("design.yml"))
                .withSetting(settingFunction.apply("feature_selection_screen.yml"))
                .addDefaultNextButton()
                .addDefaultPreviousButton()
                .addConfigDecorations()
                .pageContent(PageContentBuilder
                        .builder(GuiSetting.function(), Feature.class)
                        .data((setting, guiInfo) -> buildList(oreControlManager, guiManager, guiInfo))
                        .itemStack((setting, guiInfo, feature) -> {
                            String key = "icons." + feature.getKey().getNamespace() + "." + feature.getKey().getKey();
                            ItemStack icon = setting.get(IDENTIFIER, key + ".item-stack", null);
                            if (icon == null) {
                                icon = setting.get(IDENTIFIER, "default-icon.item-stack", new ItemStack(Material.STONE)).clone();
                                String type = setting.get(IDENTIFIER, key + ".type", null);
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
                            return icon;
                        })
                        .withAction((clickAction, feature) -> clickAction.getClickEvent().setCancelled(true))
                        .withAction((clickAction, feature) -> guiManager.getPlayerGuiData((Player) clickAction.getClickEvent().getWhoClicked()).setFeature(feature))
                        .withAction((clickAction, feature) -> guiManager.openFeatureSettingsScreen((Player) clickAction.getClickEvent().getWhoClicked()))
                )
                .build();
    }

    private static List<Feature> buildList(OreControlManager oreControlManager, OreControlGuiManager guiManager, GuiInfo guiInfo) {
        PlayerGuiData playerGuiData = guiManager.getPlayerGuiData((Player) guiInfo.getEntity());
        ConfigInfo configInfo = playerGuiData.getConfigInfo();

        if (playerGuiData.getBiome() != null) {
            return new LinkedList<>(playerGuiData.getBiome().getFeatures());
        } else {
            if (configInfo.getConfigType() == ConfigType.WORLD) {
                World world = Bukkit.getWorld(configInfo.getWorldName());
                if (world != null) {
                    Set<Feature<?>> features = new LinkedHashSet<>();
                    for (Biome biome : oreControlManager.getBiomes(world)) {
                        features.addAll(biome.getFeatures());
                    }
                    return new LinkedList<>(features);
                }
            }

            return new LinkedList<>(oreControlManager.getRegistries().getFeatureRegistry().getValues().values());
        }
    }

}
