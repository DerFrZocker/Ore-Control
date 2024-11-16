package de.derfrzocker.ore.control.gui.screen;

import de.derfrzocker.feature.api.Configuration;
import de.derfrzocker.feature.api.Feature;
import de.derfrzocker.feature.api.Setting;
import de.derfrzocker.feature.api.Value;
import de.derfrzocker.feature.api.ValueLocation;
import de.derfrzocker.feature.api.util.traverser.message.TraversKey;
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
        Value<?, ?, ?> value = config.getFeature().getValue(setting);

        return guiValuesHolder.valueTraverser().traverse(value, TraversKey.ofValueType(value.getValueType().getKey()));
    }

    private static List<Setting> buildList(GuiValuesHolder guiValuesHolder, GuiInfo guiInfo) {
        PlayerGuiData playerGuiData = guiValuesHolder.guiManager().getPlayerGuiData((Player) guiInfo.getEntity());
        Feature feature = playerGuiData.getFeature();
        List<Setting> settings = new ArrayList<>(playerGuiData.getFeature().generator().getSettings());

        settings.sort(Comparator.comparing(Setting::name));

        return settings;
    }
}
