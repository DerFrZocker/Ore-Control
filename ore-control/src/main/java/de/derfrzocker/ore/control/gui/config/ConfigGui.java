package de.derfrzocker.ore.control.gui.config;

import de.derfrzocker.ore.control.gui.settings.BooleanGuiSetting;
import de.derfrzocker.ore.control.gui.settings.ConfigGuiSettings;
import de.derfrzocker.ore.control.utils.OreControlValues;
import de.derfrzocker.spigot.utils.gui.BasicGui;
import de.derfrzocker.spigot.utils.gui.VerifyGui;
import de.derfrzocker.spigot.utils.message.MessageUtil;
import de.derfrzocker.spigot.utils.message.MessageValue;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class ConfigGui extends BasicGui {

    private static BooleanGuiSetting booleanGuiSetting;
    private static ConfigGuiSettings configGuiSettings;

    @NotNull
    private final OreControlValues oreControlValues;

    public ConfigGui(@NotNull final OreControlValues oreControlValues) {
        super(oreControlValues.getJavaPlugin(), checkSettings(oreControlValues.getJavaPlugin()));

        checkSettings(oreControlValues.getJavaPlugin());

        this.oreControlValues = oreControlValues;

        final JavaPlugin javaPlugin = oreControlValues.getJavaPlugin();

        addDecorations();

        addItem(configGuiSettings.getLanguageSlot(), MessageUtil.replaceItemStack(javaPlugin, configGuiSettings.getLanguageItemStack(),
                new MessageValue("amount", oreControlValues.getConfigValues().getLanguage().getNames()[0]),
                new MessageValue("value", oreControlValues.getConfigValues().DEFAULT.defaultLanguage().getNames()[0])
        ), event -> new LanguageGui(oreControlValues).openSync(event.getWhoClicked()));

        addItem(configGuiSettings.getsafeModeSlot(), MessageUtil.replaceItemStack(javaPlugin, configGuiSettings.getsafeModeItemStack(),
                new MessageValue("amount", oreControlValues.getConfigValues().isSafeMode()),
                new MessageValue("value", oreControlValues.getConfigValues().DEFAULT.defaultSafeMode())
        ), this::handleSafeMode);

        addItem(configGuiSettings.getTranslateTabCompilationSlot(), MessageUtil.replaceItemStack(javaPlugin, configGuiSettings.getTranslateTabCompilationItemStack(),
                new MessageValue("amount", oreControlValues.getConfigValues().isTranslateTabCompilation()),
                new MessageValue("value", oreControlValues.getConfigValues().DEFAULT.defaultTranslateTabCompilation())
        ), this::handleTranslateTabCompilation);

        addItem(configGuiSettings.getVerifyCopyActionSlot(), MessageUtil.replaceItemStack(javaPlugin, configGuiSettings.getVerifyCopyActionItemStack(),
                new MessageValue("amount", oreControlValues.getConfigValues().verifyCopyAction()),
                new MessageValue("value", oreControlValues.getConfigValues().DEFAULT.defaultVerifyCopyAction())
        ), this::handleVerifyCopyAction);

        addItem(configGuiSettings.getVerifyResetActionSlot(), MessageUtil.replaceItemStack(javaPlugin, configGuiSettings.getVerifyResetActionItemStack(),
                new MessageValue("amount", oreControlValues.getConfigValues().verifyResetAction()),
                new MessageValue("value", oreControlValues.getConfigValues().DEFAULT.defaultVerifyResetAction())
        ), this::handleVerifyResetAction);
    }

    private static ConfigGuiSettings checkSettings(@NotNull final JavaPlugin javaPlugin) {
        if (booleanGuiSetting == null)
            booleanGuiSetting = new BooleanGuiSetting(javaPlugin, "data/gui/boolean-gui.yml", true);

        if (configGuiSettings == null)
            configGuiSettings = new ConfigGuiSettings(javaPlugin, "data/gui/config-gui.yml", true);

        return configGuiSettings;
    }

    private void handleSafeMode(@NotNull final InventoryClickEvent event) {
        final VerifyGui verifyGui = new VerifyGui(getPlugin(), event1 -> {
            oreControlValues.getConfigValues().SET.setSafeMode(true);
            new ConfigGui(oreControlValues).openSync(event.getWhoClicked());
        }, event1 -> {
            oreControlValues.getConfigValues().SET.setSafeMode(false);
            new ConfigGui(oreControlValues).openSync(event.getWhoClicked());
        }, booleanGuiSetting);

        verifyGui.addItem(booleanGuiSetting.getInfoSlot(), MessageUtil.replaceItemStack(getPlugin(), booleanGuiSetting.getInfoItemStack(),
                new MessageValue("amount", oreControlValues.getConfigValues().isSafeMode()),
                new MessageValue("value", oreControlValues.getConfigValues().DEFAULT.defaultSafeMode())));

        verifyGui.openSync(event.getWhoClicked());
    }

    private void handleTranslateTabCompilation(@NotNull final InventoryClickEvent event) {
        final VerifyGui verifyGui = new VerifyGui(getPlugin(), event1 -> {
            oreControlValues.getConfigValues().SET.setTranslateTabCompilation(true);
            new ConfigGui(oreControlValues).openSync(event.getWhoClicked());
        }, event1 -> {
            oreControlValues.getConfigValues().SET.setTranslateTabCompilation(false);
            new ConfigGui(oreControlValues).openSync(event.getWhoClicked());
        }, booleanGuiSetting);

        verifyGui.addItem(booleanGuiSetting.getInfoSlot(), MessageUtil.replaceItemStack(getPlugin(), booleanGuiSetting.getInfoItemStack(),
                new MessageValue("amount", oreControlValues.getConfigValues().isTranslateTabCompilation()),
                new MessageValue("value", oreControlValues.getConfigValues().DEFAULT.defaultTranslateTabCompilation())));

        verifyGui.openSync(event.getWhoClicked());
    }

    private void handleVerifyCopyAction(@NotNull final InventoryClickEvent event) {
        final VerifyGui verifyGui = new VerifyGui(getPlugin(), event1 -> {
            oreControlValues.getConfigValues().SET.setVerifyCopyAction(true);
            new ConfigGui(oreControlValues).openSync(event.getWhoClicked());
        }, event1 -> {
            oreControlValues.getConfigValues().SET.setVerifyCopyAction(false);
            new ConfigGui(oreControlValues).openSync(event.getWhoClicked());
        }, booleanGuiSetting);

        verifyGui.addItem(booleanGuiSetting.getInfoSlot(), MessageUtil.replaceItemStack(getPlugin(), booleanGuiSetting.getInfoItemStack(),
                new MessageValue("amount", oreControlValues.getConfigValues().verifyCopyAction()),
                new MessageValue("value", oreControlValues.getConfigValues().DEFAULT.defaultVerifyCopyAction())));

        verifyGui.openSync(event.getWhoClicked());
    }

    private void handleVerifyResetAction(@NotNull final InventoryClickEvent event) {
        final VerifyGui verifyGui = new VerifyGui(getPlugin(), event1 -> {
            oreControlValues.getConfigValues().SET.setVerifyResetAction(true);
            new ConfigGui(oreControlValues).openSync(event.getWhoClicked());
        }, event1 -> {
            oreControlValues.getConfigValues().SET.setVerifyResetAction(false);
            new ConfigGui(oreControlValues).openSync(event.getWhoClicked());
        }, booleanGuiSetting);

        verifyGui.addItem(booleanGuiSetting.getInfoSlot(), MessageUtil.replaceItemStack(getPlugin(), booleanGuiSetting.getInfoItemStack(),
                new MessageValue("amount", oreControlValues.getConfigValues().verifyResetAction()),
                new MessageValue("value", oreControlValues.getConfigValues().DEFAULT.defaultVerifyResetAction())));

        verifyGui.openSync(event.getWhoClicked());
    }

}
