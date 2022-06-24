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

package de.derfrzocker.ore.control.gui.screen;

import de.derfrzocker.feature.api.Feature;
import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.OreControlManager;
import de.derfrzocker.ore.control.api.config.ConfigInfo;
import de.derfrzocker.ore.control.api.config.ConfigType;
import de.derfrzocker.ore.control.gui.GuiValuesHolder;
import de.derfrzocker.ore.control.gui.OreControlGuiManager;
import de.derfrzocker.ore.control.gui.PlayerGuiData;
import de.derfrzocker.ore.control.gui.ScreenUtil;
import de.derfrzocker.spigot.utils.gui.GuiInfo;
import de.derfrzocker.spigot.utils.gui.InventoryGui;
import de.derfrzocker.spigot.utils.gui.builders.Builders;
import de.derfrzocker.spigot.utils.message.MessageValue;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class FeatureSelectionScreen {

    private static final String IDENTIFIER = OreControlGuiManager.FEATURE_SELECTION_SCREEN;

    public static InventoryGui getGui(GuiValuesHolder guiValuesHolder) {
        return Builders
                .paged()
                .identifier(IDENTIFIER)
                .languageManager(guiValuesHolder.languageManager())
                .withSetting(guiValuesHolder.settingFunction().apply("design.yml"))
                .withSetting(guiValuesHolder.settingFunction().apply("feature_icons.yml"))
                .withSetting(guiValuesHolder.settingFunction().apply("feature_selection_screen.yml"))
                .addDefaultNextButton()
                .addDefaultPreviousButton()
                .pageContent(Builders
                        .pageContent(Feature.class)
                        .data((setting, guiInfo) -> buildList(guiValuesHolder.oreControlManager(), guiValuesHolder.guiManager(), guiInfo))
                        .withMessageValue((setting, guiInfo, feature) -> new MessageValue("feature-key", feature.getKey().getKey()))
                        .withMessageValue((setting, guiInfo, feature) -> new MessageValue("feature-namespace", feature.getKey().getNamespace()))
                        .itemStack((setting, guiInfo, feature) -> ScreenUtil.getIcon(guiValuesHolder, setting, IDENTIFIER, feature))
                        .withAction((clickAction, feature) -> clickAction.getClickEvent().setCancelled(true))
                        .withAction((clickAction, feature) -> guiValuesHolder.guiManager().getPlayerGuiData(clickAction.getPlayer()).setFeature(feature))
                        .withAction((clickAction, feature) -> guiValuesHolder.guiManager().openFeatureSettingsScreen(clickAction.getPlayer()))
                )
                .withBackAction((setting, guiInfo) -> guiValuesHolder.guiManager().getPlayerGuiData((Player) guiInfo.getEntity()).setBiome(null))
                .addButtonContext(ScreenUtil.getBackButton(guiValuesHolder.guiManager()))
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
                    Set<Feature> features = new LinkedHashSet<>();
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
