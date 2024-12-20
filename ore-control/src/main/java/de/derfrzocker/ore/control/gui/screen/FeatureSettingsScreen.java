package de.derfrzocker.ore.control.gui.screen;

import de.derfrzocker.feature.api.Configuration;
import de.derfrzocker.feature.api.ConfigurationAble;
import de.derfrzocker.feature.api.Feature;
import de.derfrzocker.feature.api.FeaturePlacementModifier;
import de.derfrzocker.feature.api.PlacementModifierConfiguration;
import de.derfrzocker.feature.api.Value;
import de.derfrzocker.feature.api.ValueLocation;
import de.derfrzocker.feature.common.feature.placement.ActivationModifier;
import de.derfrzocker.feature.common.feature.placement.configuration.ActivationConfiguration;
import de.derfrzocker.feature.common.value.bool.BooleanValue;
import de.derfrzocker.feature.common.value.bool.FixedBooleanValue;
import de.derfrzocker.ore.control.api.OreControlManager;
import de.derfrzocker.ore.control.api.config.Config;
import de.derfrzocker.ore.control.gui.GuiValuesHolder;
import de.derfrzocker.ore.control.gui.OreControlGuiManager;
import de.derfrzocker.ore.control.gui.PlayerGuiData;
import de.derfrzocker.ore.control.gui.ScreenUtil;
import de.derfrzocker.ore.control.gui.Screens;
import de.derfrzocker.ore.control.gui.SettingWrapper;
import de.derfrzocker.spigot.utils.gui.GuiInfo;
import de.derfrzocker.spigot.utils.gui.InventoryGui;
import de.derfrzocker.spigot.utils.gui.builders.Builders;
import de.derfrzocker.spigot.utils.message.MessageValue;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

// TODO clean up
public class FeatureSettingsScreen {

    public static InventoryGui getGui(GuiValuesHolder guiValuesHolder) {
        return Builders
                .paged()
                .identifier(Screens.FEATURE_SETTINGS_SCREEN)
                .languageManager(guiValuesHolder.languageManager())
                .withSetting(guiValuesHolder.settingFunction().apply("design.yml"))
                .withSetting(guiValuesHolder.settingFunction().apply("generator_icons.yml"))
                .withSetting(guiValuesHolder.settingFunction().apply("feature_settings_screen.yml"))
                .addDefaultNextButton()
                .addDefaultPreviousButton()
                .pageContent(Builders
                        .pageContent(FeaturePlacementModifier.class)
                        .data((setting, guiInfo) -> buildList(guiValuesHolder.oreControlManager(), guiValuesHolder.guiManager(), guiInfo))
                        .withMessageValue((setting, guiInfo, placementModifier) -> new MessageValue("placement-modifier-key", placementModifier.getKey().getKey()))
                        .withMessageValue((setting, guiInfo, placementModifier) -> new MessageValue("placement-modifier-namespace", placementModifier.getKey().getNamespace()))
                        .withMessageValue((setting, guiInfo, placementModifier) -> new MessageValue("placement-modifier-settings", getPlacementModifierSetting(guiValuesHolder, guiInfo, placementModifier)))
                        .itemStack((setting, guiInfo, placementModifier) -> setting.get(Screens.FEATURE_SETTINGS_SCREEN, "default-icon.item-stack", new ItemStack(Material.STONE)).clone())
                        .withAction((clickAction, placementModifier) -> clickAction.getClickEvent().setCancelled(true))
                        .withAction((clickAction, placementModifier) -> guiValuesHolder.guiManager().getPlayerGuiData(clickAction.getPlayer()).setPlacementModifier(placementModifier))
                        .withAction(((clickAction, placementModifier) -> guiValuesHolder.guiManager().openScreen(Screens.PLACEMENT_MODIFIER_SETTINGS_SCREEN, clickAction.getPlayer())))
                )
                .addButtonContext(Builders
                        .buttonContext()
                        .identifier("feature-generator")
                        .button(Builders
                                .button()
                                .identifier("feature-generator")
                                .withMessageValue((setting, guiInfo) -> new MessageValue("generator-key", guiValuesHolder.guiManager().getPlayerGuiData((Player) guiInfo.getEntity()).getFeature().generator().getKey().getKey()))
                                .withMessageValue((setting, guiInfo) -> new MessageValue("generator-namespace", guiValuesHolder.guiManager().getPlayerGuiData((Player) guiInfo.getEntity()).getFeature().generator().getKey().getNamespace()))
                                .withMessageValue((setting, guiInfo) -> new MessageValue("generator-settings", getGeneratorSetting(guiValuesHolder, guiInfo, guiValuesHolder.guiManager().getPlayerGuiData((Player) guiInfo.getEntity()).getFeature())))
                                .itemStack((setting, guiInfo) -> ScreenUtil.getIcon(guiValuesHolder, setting, "feature-generator", guiValuesHolder.guiManager().getPlayerGuiData((Player) guiInfo.getEntity()).getFeature().generator()))
                                .withAction(clickAction -> clickAction.getClickEvent().setCancelled(true))
                                .withAction(clickAction -> guiValuesHolder.guiManager().openScreen(Screens.GENERATOR_SETTINGS_SCREEN, clickAction.getPlayer()))
                        )
                )
                .addButtonContext(Builders
                        .buttonContext()
                        .withCondition((setting, guiInfo) -> {
                            PlayerGuiData playerGuiData = guiValuesHolder.guiManager().getPlayerGuiData((Player) guiInfo.getEntity());
                            Feature feature = playerGuiData.getFeature();

                            if (feature == null) {
                                return false;
                            }

                            return guiValuesHolder.oreControlManager().getRegistries().getPlacementModifierRegistry().get(ActivationModifier.KEY).map(modifier -> feature.placementModifiers().contains(modifier)).orElse(false);
                        })
                        .slot((setting, guiInfo) -> setting.get(Screens.FEATURE_SETTINGS_SCREEN, "placement-modifier-icon.feature.activation.slot", 4))
                        .identifier("activation")
                        .button(Builders
                                .button()
                                .identifier("activation")
                                .itemStack((setting, guiInfo) -> {
                                    PlayerGuiData playerGuiData = guiValuesHolder.guiManager().getPlayerGuiData((Player) guiInfo.getEntity());
                                    Optional<FeaturePlacementModifier<?>> placementModifier = guiValuesHolder.oreControlManager().getRegistries().getPlacementModifierRegistry().get(ActivationModifier.KEY);

                                    if (placementModifier.isEmpty()) {
                                        guiValuesHolder.plugin().getLogger().warning(String.format("No activation modifier found, at this point it should be present, this is a bug!"));
                                        return new ItemStack(Material.BARRIER);
                                    }

                                    Optional<Config> optionalConfig;
                                    if (playerGuiData.getBiome() == null) {
                                        optionalConfig = guiValuesHolder.oreControlManager().getConfigManager().getGuiConfig(playerGuiData.getConfigInfo(), playerGuiData.getFeature().getKey());
                                    } else {
                                        optionalConfig = guiValuesHolder.oreControlManager().getConfigManager().getGuiConfig(playerGuiData.getConfigInfo(), playerGuiData.getBiome(), playerGuiData.getFeature().getKey());
                                    }

                                    if (optionalConfig.isEmpty()) {
                                        guiValuesHolder.plugin().getLogger().warning(String.format("No gui specific config found, it should always be possible to build one with default value, this is a bug!"));
                                        return new ItemStack(Material.BARRIER);
                                    }

                                    Config config = optionalConfig.get();
                                    Configuration configuration = config.getPlacements().get(placementModifier.get());

                                    if (configuration == null) {
                                        guiValuesHolder.plugin().getLogger().warning(String.format("No suitable configuration found, there should always a default configuration present, this is a bug!"));
                                        return new ItemStack(Material.BARRIER);
                                    }

                                    if (!(configuration instanceof ActivationConfiguration)) {
                                        guiValuesHolder.plugin().getLogger().warning(String.format("Present configuration is of type '%s', but should be of type '%s', this is a bug!", configuration.getClass(), "ActivationConfiguration"));
                                        return new ItemStack(Material.BARRIER);
                                    }

                                    BooleanValue value = ((ActivationConfiguration) configuration).getActivate();

                                    if (value == null) {
                                        guiValuesHolder.plugin().getLogger().warning(String.format("No suitable value found, there should always a default value present, this is a bug!"));
                                        return new ItemStack(Material.BARRIER);
                                    }

                                    if (!(value instanceof FixedBooleanValue)) {
                                        return setting.get(Screens.FEATURE_SETTINGS_SCREEN, "placement-modifier-icon.feature.activation.unknown.item-stack", new ItemStack(Material.STONE));
                                    }

                                    if (((FixedBooleanValue) value).getValue()) {
                                        return setting.get(Screens.FEATURE_SETTINGS_SCREEN, "placement-modifier-icon.feature.activation.true.item-stack", new ItemStack(Material.STONE));
                                    } else {
                                        return setting.get(Screens.FEATURE_SETTINGS_SCREEN, "placement-modifier-icon.feature.activation.false.item-stack", new ItemStack(Material.STONE));
                                    }
                                })
                                .withAction(clickAction -> clickAction.getClickEvent().setCancelled(true))
                                .withAction(clickAction -> {
                                    PlayerGuiData playerGuiData = guiValuesHolder.guiManager().getPlayerGuiData(clickAction.getPlayer());
                                    Optional<FeaturePlacementModifier<?>> placementModifier = guiValuesHolder.oreControlManager().getRegistries().getPlacementModifierRegistry().get(ActivationModifier.KEY);

                                    if (placementModifier.isEmpty()) {
                                        guiValuesHolder.plugin().getLogger().warning(String.format("No activation modifier found, at this point it should be present, this is a bug!"));
                                        return;
                                    }

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
                                    Configuration configuration = config.getPlacements().get(placementModifier.get());

                                    if (configuration == null) {
                                        guiValuesHolder.plugin().getLogger().warning(String.format("No suitable configuration found, there should always a default configuration present, this is a bug!"));
                                        return;
                                    }

                                    if (!(configuration instanceof ActivationConfiguration)) {
                                        guiValuesHolder.plugin().getLogger().warning(String.format("Present configuration is of type '%s', but should be of type '%s', this is a bug!", configuration.getClass(), "ActivationConfiguration"));
                                        return;
                                    }

                                    Value<?, ?, ?> value = ((ActivationConfiguration) configuration).getActivate();

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
                                    playerGuiData.setSettingWrapper(new SettingWrapper(ActivationConfiguration.ACTIVATE, placementModifier.get()));
                                    guiValuesHolder.guiManager().openValueScreen(clickAction.getPlayer(), value);
                                })
                        )
                )
                .withBackAction((setting, guiInfo) -> guiValuesHolder.guiManager().getPlayerGuiData((Player) guiInfo.getEntity()).setFeature(null))
                .addButtonContext(ScreenUtil.getBackButton(guiValuesHolder.guiManager()))
                .build();
    }

    public static String getPlacementModifierSetting(GuiValuesHolder guiValuesHolder, GuiInfo guiInfo, FeaturePlacementModifier<?> placementModifier) {
        PlayerGuiData playerGuiData = guiValuesHolder.guiManager().getPlayerGuiData((Player) guiInfo.getEntity());
        StringBuilder stringBuilder = new StringBuilder();

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

        stringBuilder.append("§r§f");
        stringBuilder.append("%%translation:[placement-modifiers.");
        stringBuilder.append(placementModifier.getKey().getNamespace());
        stringBuilder.append(".");
        stringBuilder.append(placementModifier.getKey().getKey());
        stringBuilder.append(".name]%:");
        stringBuilder.append("%%new-line%");
        stringBuilder.append(guiValuesHolder.valueTraverser().traverse(config.getPlacements().get(placementModifier)));

        return stringBuilder.toString();
    }

    public static String getGeneratorSetting(GuiValuesHolder guiValuesHolder, GuiInfo guiInfo, Feature feature) {
        PlayerGuiData playerGuiData = guiValuesHolder.guiManager().getPlayerGuiData((Player) guiInfo.getEntity());
        Optional<Config> optionalConfig;
        if (playerGuiData.getBiome() == null) {
            optionalConfig = guiValuesHolder.oreControlManager().getConfigManager().getGuiConfig(playerGuiData.getConfigInfo(), feature.getKey());
        } else {
            optionalConfig = guiValuesHolder.oreControlManager().getConfigManager().getGuiConfig(playerGuiData.getConfigInfo(), playerGuiData.getBiome(), feature.getKey());
        }

        if (optionalConfig.isEmpty()) {
            guiValuesHolder.plugin().getLogger().warning(String.format("No gui specific config found, it should always be possible to build one with default value, this is a bug!"));
            return "UNKNOWN";
        }

        Config config = optionalConfig.get();
        Configuration configuration = config.getFeature();

        return "§r§f%%translation:[feature-generators." + feature.generator().getKey().getNamespace() + "." + feature.generator().getKey().getKey() + ".name]%:%%new-line%" + guiValuesHolder.valueTraverser().traverse(configuration);
    }

    private static List<FeaturePlacementModifier> buildList(OreControlManager oreControlManager, OreControlGuiManager guiManager, GuiInfo guiInfo) {
        PlayerGuiData playerGuiData = guiManager.getPlayerGuiData((Player) guiInfo.getEntity());
        Feature feature = playerGuiData.getFeature();
        List<FeaturePlacementModifier> placementModifiers = new LinkedList<>();

        for (FeaturePlacementModifier<?> placementModifier : feature.placementModifiers()) {
            if (placementModifier.getKey().equals(ActivationModifier.KEY)) {
                continue;
            }

            placementModifiers.add(placementModifier);
        }

        return placementModifiers;
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
