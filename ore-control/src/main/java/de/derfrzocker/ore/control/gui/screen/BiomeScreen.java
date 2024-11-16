package de.derfrzocker.ore.control.gui.screen;

import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.OreControlManager;
import de.derfrzocker.ore.control.api.config.ConfigInfo;
import de.derfrzocker.ore.control.api.config.ConfigType;
import de.derfrzocker.ore.control.gui.GuiValuesHolder;
import de.derfrzocker.ore.control.gui.OreControlGuiManager;
import de.derfrzocker.ore.control.gui.PlayerGuiData;
import de.derfrzocker.ore.control.gui.ScreenUtil;
import de.derfrzocker.ore.control.gui.Screens;
import de.derfrzocker.ore.control.gui.info.InfoLink;
import de.derfrzocker.spigot.utils.gui.GuiInfo;
import de.derfrzocker.spigot.utils.gui.InventoryGui;
import de.derfrzocker.spigot.utils.gui.builders.Builders;
import de.derfrzocker.spigot.utils.message.MessageValue;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;

import static de.derfrzocker.ore.control.gui.info.InfoLinkData.of;

public class BiomeScreen {

    public static InventoryGui getGui(GuiValuesHolder guiValuesHolder) {
        return Builders
                .paged()
                .identifier(Screens.BIOME_SCREEN)
                .languageManager(guiValuesHolder.languageManager())
                .withSetting(guiValuesHolder.settingFunction().apply("design.yml"))
                .withSetting(guiValuesHolder.settingFunction().apply("biome_icons.yml"))
                .withSetting(guiValuesHolder.settingFunction().apply("biome_screen.yml"))
                .addDefaultNextButton()
                .addDefaultPreviousButton()
                .pageContent(Builders
                        .pageContent(Biome.class)
                        .data((setting, guiInfo) -> buildList(guiValuesHolder.oreControlManager(), guiValuesHolder.guiManager(), guiInfo))
                        .withMessageValue((setting, guiInfo, biome) -> new MessageValue("biome-key", biome.getKey().getKey()))
                        .withMessageValue((setting, guiInfo, biome) -> new MessageValue("biome-namespace", biome.getKey().getNamespace()))
                        .itemStack((setting, guiInfo, biome) -> ScreenUtil.getIcon(guiValuesHolder, setting, Screens.BIOME_SCREEN, biome))
                        .withAction((clickAction, biome) -> clickAction.getClickEvent().setCancelled(true))
                        .withAction((clickAction, biome) -> guiValuesHolder.guiManager().getPlayerGuiData(clickAction.getPlayer()).setBiome(biome))
                        .withAction((clickAction, biome) -> guiValuesHolder.guiManager().openScreen(Screens.FEATURE_SELECTION_SCREEN, clickAction.getPlayer()))
                )
                .addButtonContext(ScreenUtil.getBackButton(guiValuesHolder.guiManager()))
                .addButtonContext(ScreenUtil.getInfoButton(guiValuesHolder, of(InfoLink.INVENTORY_GUI_SCREENS_EXPLAINED, "Choose-A-Biome", "biome_screen")))
                .build();
    }

    private static List<Biome> buildList(OreControlManager oreControlManager, OreControlGuiManager guiManager, GuiInfo guiInfo) {
        PlayerGuiData playerGuiData = guiManager.getPlayerGuiData((Player) guiInfo.getEntity());
        ConfigInfo configInfo = playerGuiData.getConfigInfo();

        if (configInfo.getConfigType() == ConfigType.WORLD) {
            World world = Bukkit.getWorld(configInfo.getWorldName());
            if (world != null) {
                return new LinkedList<>(oreControlManager.getBiomes(world));
            }
        }

        return new LinkedList<>(oreControlManager.getRegistries().getBiomeRegistry().getValues().values());
    }
}
