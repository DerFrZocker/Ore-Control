package de.derfrzocker.ore.control.gui.config;

import de.derfrzocker.ore.control.OreControl;
import de.derfrzocker.spigot.utils.gui.BasicGui;
import de.derfrzocker.spigot.utils.gui.BasicSettings;
import de.derfrzocker.spigot.utils.gui.VerifyGui;
import de.derfrzocker.spigot.utils.message.MessageUtil;
import de.derfrzocker.spigot.utils.message.MessageValue;
import lombok.NonNull;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import static java.lang.String.valueOf;

public class ConfigGui extends BasicGui {


    public ConfigGui() {
        super(OreControl.getInstance(), ConfigGuiSettings.getInstance());
        addItem(ConfigGuiSettings.getInstance().getLanguageSlot(), MessageUtil.replaceItemStack(OreControl.getInstance(), ConfigGuiSettings.getInstance().getLanguageItemStack(),
                new MessageValue("amount", OreControl.getInstance().getConfigValues().getLanguage().getNames()[0]),
                new MessageValue("value", OreControl.getInstance().getConfigValues().DEFAULT.defaultLanguage().getNames()[0])
        ), event -> new LanguageGui().openSync(event.getWhoClicked()));

        addItem(ConfigGuiSettings.getInstance().getsafeModeSlot(), MessageUtil.replaceItemStack(OreControl.getInstance(), ConfigGuiSettings.getInstance().getsafeModeItemStack(),
                new MessageValue("amount", valueOf(OreControl.getInstance().getConfigValues().isSafeMode())),
                new MessageValue("value", valueOf(OreControl.getInstance().getConfigValues().DEFAULT.defaultSafeMode()))
        ), this::handlesafeMode);

        addItem(ConfigGuiSettings.getInstance().getTranslateTabCompilationSlot(), MessageUtil.replaceItemStack(OreControl.getInstance(), ConfigGuiSettings.getInstance().getTranslateTabCompilationItemStack(),
                new MessageValue("amount", valueOf(OreControl.getInstance().getConfigValues().isTranslateTabCompilation())),
                new MessageValue("value", valueOf(OreControl.getInstance().getConfigValues().DEFAULT.defaultTranslateTabCompilation()))
        ), this::handleTranslateTabCompilation);

        addItem(ConfigGuiSettings.getInstance().getVerifyCopyActionSlot(), MessageUtil.replaceItemStack(OreControl.getInstance(), ConfigGuiSettings.getInstance().getVerifyCopyActionItemStack(),
                new MessageValue("amount", valueOf(OreControl.getInstance().getConfigValues().verifyCopyAction())),
                new MessageValue("value", valueOf(OreControl.getInstance().getConfigValues().DEFAULT.defaultVerifyCopyAction()))
        ), this::handleVerifyCopyAction);

        addItem(ConfigGuiSettings.getInstance().getVerifyResetActionSlot(), MessageUtil.replaceItemStack(OreControl.getInstance(), ConfigGuiSettings.getInstance().getVerifyResetActionItemStack(),
                new MessageValue("amount", valueOf(OreControl.getInstance().getConfigValues().verifyResetAction())),
                new MessageValue("value", valueOf(OreControl.getInstance().getConfigValues().DEFAULT.defaultVerifyResetAction()))
        ), this::handleVerifyResetAction);
    }

    private void handlesafeMode(final @NonNull InventoryClickEvent event) {
        final VerifyGui verifyGui = new VerifyGui(OreControl.getInstance(), event1 -> {
            OreControl.getInstance().getConfigValues().SET.setSafeMode(true);
            new ConfigGui().openSync(event.getWhoClicked());
        }, event1 -> {
            OreControl.getInstance().getConfigValues().SET.setSafeMode(false);
            new ConfigGui().openSync(event.getWhoClicked());
        }, BooleanGuiSetting.getInstance());

        verifyGui.addItem(BooleanGuiSetting.getInstance().getInfoSlot(), MessageUtil.replaceItemStack(OreControl.getInstance(), BooleanGuiSetting.getInstance().getInfoItemStack(),
                new MessageValue("amount", valueOf(OreControl.getInstance().getConfigValues().isSafeMode())),
                new MessageValue("value", valueOf(OreControl.getInstance().getConfigValues().DEFAULT.defaultSafeMode()))));

        verifyGui.openSync(event.getWhoClicked());
    }

    private void handleTranslateTabCompilation(final @NonNull InventoryClickEvent event) {
        final VerifyGui verifyGui = new VerifyGui(OreControl.getInstance(), event1 -> {
            OreControl.getInstance().getConfigValues().SET.setTranslateTabCompilation(true);
            new ConfigGui().openSync(event.getWhoClicked());
        }, event1 -> {
            OreControl.getInstance().getConfigValues().SET.setTranslateTabCompilation(false);
            new ConfigGui().openSync(event.getWhoClicked());
        }, BooleanGuiSetting.getInstance());

        verifyGui.addItem(BooleanGuiSetting.getInstance().getInfoSlot(), MessageUtil.replaceItemStack(OreControl.getInstance(), BooleanGuiSetting.getInstance().getInfoItemStack(),
                new MessageValue("amount", valueOf(OreControl.getInstance().getConfigValues().isTranslateTabCompilation())),
                new MessageValue("value", valueOf(OreControl.getInstance().getConfigValues().DEFAULT.defaultTranslateTabCompilation()))));

        verifyGui.openSync(event.getWhoClicked());
    }

    private void handleVerifyCopyAction(final @NonNull InventoryClickEvent event) {
        final VerifyGui verifyGui = new VerifyGui(OreControl.getInstance(), event1 -> {
            OreControl.getInstance().getConfigValues().SET.setVerifyCopyAction(true);
            new ConfigGui().openSync(event.getWhoClicked());
        }, event1 -> {
            OreControl.getInstance().getConfigValues().SET.setVerifyCopyAction(false);
            new ConfigGui().openSync(event.getWhoClicked());
        }, BooleanGuiSetting.getInstance());

        verifyGui.addItem(BooleanGuiSetting.getInstance().getInfoSlot(), MessageUtil.replaceItemStack(OreControl.getInstance(), BooleanGuiSetting.getInstance().getInfoItemStack(),
                new MessageValue("amount", valueOf(OreControl.getInstance().getConfigValues().verifyCopyAction())),
                new MessageValue("value", valueOf(OreControl.getInstance().getConfigValues().DEFAULT.defaultVerifyCopyAction()))));

        verifyGui.openSync(event.getWhoClicked());
    }

    private void handleVerifyResetAction(final @NonNull InventoryClickEvent event) {
        final VerifyGui verifyGui = new VerifyGui(OreControl.getInstance(), event1 -> {
            OreControl.getInstance().getConfigValues().SET.setVerifyResetAction(true);
            new ConfigGui().openSync(event.getWhoClicked());
        }, event1 -> {
            OreControl.getInstance().getConfigValues().SET.setVerifyResetAction(false);
            new ConfigGui().openSync(event.getWhoClicked());
        }, BooleanGuiSetting.getInstance());

        verifyGui.addItem(BooleanGuiSetting.getInstance().getInfoSlot(), MessageUtil.replaceItemStack(OreControl.getInstance(), BooleanGuiSetting.getInstance().getInfoItemStack(),
                new MessageValue("amount", valueOf(OreControl.getInstance().getConfigValues().verifyResetAction())),
                new MessageValue("value", valueOf(OreControl.getInstance().getConfigValues().DEFAULT.defaultVerifyResetAction()))));

        verifyGui.openSync(event.getWhoClicked());
    }

    private static final class ConfigGuiSettings extends BasicSettings {
        private static ConfigGuiSettings instance = null;

        private static ConfigGuiSettings getInstance() {
            if (instance == null)
                instance = new ConfigGuiSettings();

            return instance;
        }

        private ConfigGuiSettings() {
            super(OreControl.getInstance(), "data/config_gui.yml");
        }

        private int getsafeModeSlot() {
            return getYaml().getInt("safe_mode.slot");
        }

        private ItemStack getsafeModeItemStack() {
            return getYaml().getItemStack("safe_mode.item_stack").clone();
        }

        private int getLanguageSlot() {
            return getYaml().getInt("language.slot");
        }

        private ItemStack getLanguageItemStack() {
            return getYaml().getItemStack("language.item_stack").clone();
        }

        private int getTranslateTabCompilationSlot() {
            return getYaml().getInt("translate_tab_compilation.slot");
        }

        private ItemStack getTranslateTabCompilationItemStack() {
            return getYaml().getItemStack("translate_tab_compilation.item_stack").clone();
        }

        private int getVerifyCopyActionSlot() {
            return getYaml().getInt("verify.copy_action.slot");
        }

        private ItemStack getVerifyCopyActionItemStack() {
            return getYaml().getItemStack("verify.copy_action.item_stack").clone();
        }

        private int getVerifyResetActionSlot() {
            return getYaml().getInt("verify.reset_action.slot");
        }

        private ItemStack getVerifyResetActionItemStack() {
            return getYaml().getItemStack("verify.reset_action.item_stack").clone();
        }
    }

    private static final class BooleanGuiSetting extends BasicSettings implements VerifyGui.VerifyGuiSettingsInterface {
        private static BooleanGuiSetting instance = null;

        private static BooleanGuiSetting getInstance() {
            if (instance == null)
                instance = new BooleanGuiSetting();

            return instance;
        }

        private BooleanGuiSetting() {
            super(OreControl.getInstance(), "data/boolean_gui.yml");
        }

        @Override
        public int getAcceptSlot() {
            return getYaml().getInt("true.slot");
        }

        @Override
        public ItemStack getAcceptItemStack() {
            return getYaml().getItemStack("true.item_stack").clone();
        }

        @Override
        public ItemStack getDenyItemStack() {
            return getYaml().getItemStack("false.item_stack").clone();
        }

        @Override
        public int getDenySlot() {
            return getYaml().getInt("false.slot");
        }

        private ItemStack getInfoItemStack() {
            return getYaml().getItemStack("info.item_stack").clone();
        }

        private int getInfoSlot() {
            return getYaml().getInt("info.slot");
        }
    }

}
