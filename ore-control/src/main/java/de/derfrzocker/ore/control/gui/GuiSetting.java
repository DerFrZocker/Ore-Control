package de.derfrzocker.ore.control.gui;

import de.derfrzocker.spigot.utils.guin.settings.AbstractSetting;
import de.derfrzocker.spigot.utils.guin.settings.Setting;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class GuiSetting extends AbstractSetting<GuiSetting> {

    public static BiFunction<Setting<?>, GuiSetting, GuiSetting> function() {
        return Setting.createFunction(GuiSetting::new);
    }

    @Nullable
    private final Supplier<ConfigurationSection> sectionSupplier;
    public ConfigurationSection configuration;

    public GuiSetting() {
        this(null);
    }

    public GuiSetting(@Nullable Supplier<ConfigurationSection> sectionSupplier) {
        this.sectionSupplier = sectionSupplier;
        reload();
    }

    public void reload() {
        if (sectionSupplier != null) {
            configuration = sectionSupplier.get();
        }
    }

    @Override
    protected Object get0(String key) {
        return configuration == null ? null : configuration.get(key);
    }

    @Override
    protected Set<String> getKeys0(String key) {
        if (configuration == null) {
            return null;
        }

        ConfigurationSection section = configuration.getConfigurationSection(key);

        if (section == null) {
            return null;
        }

        return section.getKeys(false);
    }

    @Override
    protected GuiSetting createEmptySetting() {
        return new GuiSetting();
    }
}
