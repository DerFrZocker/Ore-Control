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

import de.derfrzocker.feature.api.Configuration;
import de.derfrzocker.feature.api.Feature;
import de.derfrzocker.feature.api.Setting;
import de.derfrzocker.feature.api.Value;
import de.derfrzocker.feature.api.ValueLocation;
import de.derfrzocker.ore.control.api.config.Config;
import de.derfrzocker.ore.control.gui.GuiValuesHolder;
import de.derfrzocker.ore.control.gui.PlayerGuiData;
import de.derfrzocker.ore.control.gui.ScreenUtil;
import de.derfrzocker.ore.control.gui.Screens;
import de.derfrzocker.ore.control.gui.SettingWrapper;
import de.derfrzocker.ore.control.gui.info.InfoLink;
import de.derfrzocker.spigot.utils.gui.GuiInfo;
import de.derfrzocker.spigot.utils.gui.InventoryGui;
import de.derfrzocker.spigot.utils.gui.builders.Builders;
import de.derfrzocker.spigot.utils.message.MessageValue;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static de.derfrzocker.ore.control.gui.info.InfoLinkData.of;

public class GeneratorSettingsScreen {

    public static InventoryGui getGui(GuiValuesHolder guiValuesHolder) {
        return Builders
                .paged()
                .identifier(Screens.GENERATOR_SETTINGS_SCREEN)
                .languageManager(guiValuesHolder.languageManager())
                .withSetting(guiValuesHolder.settingFunction().apply("design.yml"))
                .withSetting(guiValuesHolder.settingFunction().apply("all_settings_screen.yml"))
                .addDefaultNextButton()
                .addDefaultPreviousButton()
                .pageContent(Builders
                        .pageContent(Setting.class)
                        .data((setting, guiInfo) -> buildList(guiValuesHolder, guiInfo))
                        .withMessageValue((setting, guiInfo, value) -> new MessageValue("setting", value.name()))
                        .withMessageValue((setting, guiInfo, value) -> new MessageValue("value-settings", getValueSettings(guiValuesHolder, guiInfo, value)))
                        .itemStack((setting, guiInfo, value) -> setting.get(Screens.GENERATOR_SETTINGS_SCREEN, "default-icon.item-stack", new ItemStack(Material.STONE)).clone())
                        .withAction((clickAction, setting) -> clickAction.getClickEvent().setCancelled(true))
                        .withAction((clickAction, setting) -> guiValuesHolder.guiManager().getPlayerGuiData(clickAction.getPlayer()).setSettingWrapper(new SettingWrapper(setting, guiValuesHolder.guiManager().getPlayerGuiData(clickAction.getPlayer()).getFeature().generator())))
                        .withAction((clickAction, setting) -> {
                            PlayerGuiData playerGuiData = guiValuesHolder.guiManager().getPlayerGuiData(clickAction.getPlayer());
                            Optional<Config> optionalConfig;
                            if (playerGuiData.getBiome() == null) {
                                optionalConfig = guiValuesHolder.oreControlManager().getConfigManager().getGuiConfig(playerGuiData.getConfigInfo(), playerGuiData.getFeature().getKey());
                            } else {
                                optionalConfig = guiValuesHolder.oreControlManager().getConfigManager().getGuiConfig(playerGuiData.getConfigInfo(), playerGuiData.getBiome(), playerGuiData.getFeature().getKey());
                            }

                            if (optionalConfig.isEmpty()) {
                                guiValuesHolder.plugin().getLogger().warning(String.format("No gui specific config found, it should always be possible to build one with default value, this is a bug!"));
                                return;
                            }

                            Config config = optionalConfig.get();

                            Configuration configuration = config.getFeature();

                            if (configuration == null) {
                                guiValuesHolder.plugin().getLogger().warning(String.format("No suitable configuration found, there should always a default configuration present, this is a bug!"));
                                return;
                            }

                            Value<?, ?, ?> value = configuration.getValue(setting);

                            if (value == null) {
                                guiValuesHolder.plugin().getLogger().warning(String.format("No suitable value found, there should always a default value present, this is a bug!"));
                                return;
                            }

                            ValueLocation valueLocation = value.getValueLocation();
                            value = value.clone();
                            value.setValueLocation(valueLocation);

                            playerGuiData.setOriginalValue(value);
                            playerGuiData.setToEditValue(value);
                            playerGuiData.setApplied(false);
                            guiValuesHolder.guiManager().openValueScreen(clickAction.getPlayer(), value);
                        })
                )
                .addButtonContext(ScreenUtil.getInfoButton(guiValuesHolder, of(InfoLink.GENERATOR_INFO)))
                .addButtonContext(ScreenUtil.getBackButton(guiValuesHolder.guiManager()))
                .build();
    }

    private static Object getValueSettings(GuiValuesHolder guiValuesHolder, GuiInfo guiInfo, Setting setting) {
        PlayerGuiData playerGuiData = guiValuesHolder.guiManager().getPlayerGuiData((Player) guiInfo.getEntity());
        Optional<Config> optionalConfig;
        if (playerGuiData.getBiome() == null) {
            optionalConfig = guiValuesHolder.oreControlManager().getConfigManager().getGuiConfig(playerGuiData.getConfigInfo(), playerGuiData.getFeature().getKey());
        } else {
            optionalConfig = guiValuesHolder.oreControlManager().getConfigManager().getGuiConfig(playerGuiData.getConfigInfo(), playerGuiData.getBiome(), playerGuiData.getFeature().getKey());
        }

        if (optionalConfig.isEmpty()) {
            guiValuesHolder.plugin().getLogger().warning(String.format("No gui specific config found, it should always be possible to build one with default value, this is a bug!"));
            return "UNKNOWN";
        }

        Config config = optionalConfig.get();
        Value<?,?,?> value = config.getFeature().getValue(setting);

        return guiValuesHolder.valueTraverser().traverse(value, "%%translation:[value-types." + value.getValueType().getKey().getNamespace() + "." + value.getValueType().getKey().getKey() + ".name]%");
    }

    private static List<Setting> buildList(GuiValuesHolder guiValuesHolder, GuiInfo guiInfo) {
        PlayerGuiData playerGuiData = guiValuesHolder.guiManager().getPlayerGuiData((Player) guiInfo.getEntity());
        Feature feature = playerGuiData.getFeature();
        List<Setting> settings = new ArrayList<>(playerGuiData.getFeature().generator().getSettings());

        settings.sort(Comparator.comparing(Setting::name));

        return settings;
    }
}
