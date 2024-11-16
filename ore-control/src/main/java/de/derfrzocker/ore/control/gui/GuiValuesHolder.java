package de.derfrzocker.ore.control.gui;

import de.derfrzocker.ore.control.Stats;
import de.derfrzocker.ore.control.api.OreControlManager;
import de.derfrzocker.ore.control.api.config.ConfigManager;
import de.derfrzocker.ore.control.interactions.BlockInteractionManager;
import de.derfrzocker.ore.control.traverser.ValueTraverser;
import de.derfrzocker.spigot.utils.language.LanguageManager;
import de.derfrzocker.spigot.utils.setting.ConfigSetting;
import org.bukkit.plugin.Plugin;

import java.util.function.Function;

public record GuiValuesHolder(Plugin plugin, OreControlManager oreControlManager, OreControlGuiManager guiManager,
                              ConfigManager configManager, LanguageManager languageManager,
                              Function<String, ConfigSetting> settingFunction, ValueTraverser valueTraverser,
                              Stats stats, BlockInteractionManager blockInteractionManager) {
}
